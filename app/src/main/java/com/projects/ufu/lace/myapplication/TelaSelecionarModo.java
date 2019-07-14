package com.projects.ufu.lace.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.projects.ufu.lace.myapplication.bluetooth.Bluetooth;

public class TelaSelecionarModo extends Activity {
    ButtonEspecial gamePad, terminal;
    Button voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_selecionar_modo);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        voltar = findViewById(R.id.voltarselecionarMod);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gamePad = new ButtonEspecial(findViewById(R.id.game_pad));
        terminal = new ButtonEspecial(findViewById(R.id.terminal_mode));


        terminal.setAction(new ButtonEspecial.Clicavel() {
            @Override
            public void acao() {
                startActivity(new Intent(getApplicationContext(), TelaTerminalMode.class));
                finish();
            }

            @Override
            public void acaoLonga() {
                Toast.makeText(getApplicationContext(), "Modo Terminal ", Toast.LENGTH_SHORT).show();
            }
        });
        gamePad.setAction(new ButtonEspecial.Clicavel() {
            @Override
            public void acao() {
                startActivity(new Intent(getApplicationContext(), TelaGamePad.class));
                finish();
            }

            @Override
            public void acaoLonga() {

            }
        });
        if (!Bluetooth.isConected()) {
            startActivity(new Intent(getApplicationContext(), TelaBluetooth.class));
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        Bluetooth.clear();
        startActivity(new Intent(getApplicationContext(),TelaBluetooth.class));
        finish();
    }
}
