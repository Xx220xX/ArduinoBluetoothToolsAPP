package com.projects.ufu.lace.myapplication;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.ufu.lace.myapplication.bluetooth.Bluetooth;

public class TelaTerminalMode extends Activity {
    private ButtonEspecial sendMsg;
    private EditText input;
    private ListView receive;
    private ArrayAdapter<String> receiveAdpter;
    private Bluetooth bluetooth;

    {
        bluetooth = Bluetooth.getInstance(this, new Bluetooth.Comunicavel() {
            @Override
            public void onRequestFinish(int request, Object result) {

            }

            @Override
            public void dadosReceived(String dados) {
                receiveAdpter.add(bluetooth.getDeviceNameConected() + ": " + dados);
            }


            @Override
            public void onConnect(String bluetoothName, boolean Isconected) {

            }

            @Override
            public void onDisconect() {
                Toast.makeText(getApplicationContext(), "desconectado", Toast.LENGTH_LONG).show();
                bluetooth.desconectar();
                startActivity(new Intent(getApplicationContext(), TelaBluetooth.class));
                finish();

            }

            @Override
            public void Log(String msg) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_terminal_mode);
        //iniciando bluetooth
        bluetooth.iniciarOuContinuarServicos();
        receiveAdpter = new ArrayAdapter<>(getApplicationContext(), R.layout.lista_simples);

        //iniciando componentes do layout

        receive = findViewById(R.id.textJanela);
        input = findViewById(R.id.input);
        sendMsg = new ButtonEspecial(findViewById(R.id.enviar));

        sendMsg.setAction(new ButtonEspecial.Clicavel() {
            @Override
            public void acao() {
                String msg = input.getText().toString();
                bluetooth.enviarDados(msg);
                receiveAdpter.add("> " + msg);
                input.setText("");
            }

            @Override
            public void acaoLonga() {

            }
        });
        receive.setAdapter(receiveAdpter);
        receive.setOnItemClickListener(itemClick);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), TelaSelecionarModo.class));
        finish();
    }

    AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String s = (String) parent.getItemAtPosition(position);
            if (s.contains(bluetooth.getDeviceNameConected() + ": ")) {
                s = s.replace(bluetooth.getDeviceNameConected() + ": ", "");
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("last msg",s);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(),R.string.copiado_para_area_de_tranferencia,Toast.LENGTH_SHORT).show();
            } else if (s.contains("> ")) {
                s = s.replace("> ", "");
                input.setText(s);
            }
        }
    };
}
