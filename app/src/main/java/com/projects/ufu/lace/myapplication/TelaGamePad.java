package com.projects.ufu.lace.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.projects.ufu.lace.myapplication.arquivos.IOData;
import com.projects.ufu.lace.myapplication.bluetooth.Bluetooth;

public class TelaGamePad extends Activity {
    private BotaoGame t, o, x, q;
    private BotaoGame setaDireita, setaEsquerda, setaCima, setaBaixo;
    private BotaoGame start, select;
    private ButtonEspecial configuracao;
    private BotaoGame m_allButtons[];
    private Button voltar;
    private ListView receive;

    private ArrayAdapter<String> adpterReceive;
    private Bluetooth bluetooth = Bluetooth.getInstance(this, new Bluetooth.Comunicavel() {
        @Override
        public void onRequestFinish(int request, Object result) {

        }

        @Override
        public void dadosReceived(String dados) {
            adpterReceive.add(bluetooth.getDeviceNameConected() + "> " + dados);
        }

        @Override
        public void onConnect(String bluetoothName, boolean Isconected) {

        }

        @Override
        public void onDisconect() {
            Toast.makeText(getApplicationContext(), getString(R.string.desconectado), Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), TelaBluetooth.class));
            finish();
        }

        @Override
        public void Log(String msg) {

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_game_pad);

        receive = findViewById(R.id.receivegamepad);

        adpterReceive = new ArrayAdapter<>(getApplication(), R.layout.lista_simples);
        configuracao = new ButtonEspecial(findViewById(R.id.conf));
        TelaConfigracaoBotao.Dados.setBotoes(null);
        configuracao.setAction(new ButtonEspecial.Clicavel() {
            @Override
            public void acao() {
                TelaConfigracaoBotao.Dados.setBotoes(m_allButtons);
                startActivity(new Intent(getApplicationContext(), TelaConfigracaoBotao.class));
                finish();
            }

            @Override
            public void acaoLonga() {
                Toast.makeText(getApplicationContext(), getString(R.string.config_botoes), Toast.LENGTH_LONG).show();
            }
        });
        voltar = findViewById(R.id.voltargamepad);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        receive.setAdapter(adpterReceive);
        iniciarBotoes();
        m_allButtons = new BotaoGame[]{t, o, x, q, start, select, setaDireita, setaBaixo, setaEsquerda, setaCima};
        bluetooth.iniciarOuContinuarServicos();

    }

    private void iniciarBotoes() {
        t = new BotaoGame(findViewById(R.id.triangulo), this, "triangulo");
        o = new BotaoGame(findViewById(R.id.bolinha), this, "bolinha");
        x = new BotaoGame(findViewById(R.id.x), this, "cruz");
        q = new BotaoGame(findViewById(R.id.quadrado), this, "quadrado");
        start = new BotaoGame(findViewById(R.id.btstart), this, "start");
        select = new BotaoGame(findViewById(R.id.btselect), this, "select");
        setaDireita = new BotaoGame(findViewById(R.id.setaDireita), this, "seta_direita");
        setaBaixo = new BotaoGame(findViewById(R.id.setaBaixo), this, "seta_baixo");
        setaEsquerda = new BotaoGame(findViewById(R.id.setaEsquerda), this, "seta_esquerda");
        setaCima = new BotaoGame(findViewById(R.id.setaCima), this, "seta_cima");
    }


    private void send(char caractere) {
        if (caractere != '\0') {
            bluetooth.enviarDados(caractere);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.impossivel_enviar_caractere_nulo), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        TelaConfigracaoBotao.Dados.setBotoes(null);
        startActivity(new Intent(getApplicationContext(), TelaSelecionarModo.class));
        finish();
    }

    class BotaoGame extends ButtonEspecial {
        private final String name;
        private char caracterePress = 48, caractereDrop = 48;
        private Drawable principal;
        int id;

        public Drawable getPrincipal() {
            return principal;
        }

        public void save() {
            IOData ioData = new IOData(getApplicationContext(), name);
            ioData.salveArq(getCaracterePress());
            ioData.salveArq(getCaractereDrop());
            ioData.close();

        }

        public void load() {
            IOData ioData = new IOData(getApplicationContext(), name);
            setCaracterePress((char) ioData.loadCaractere());
            setCaractereDrop((char) ioData.loadCaractere());
            ioData.close();

        }

        public char getCaracterePress() {
            return caracterePress;
        }

        public char getCaractereDrop() {
            return caractereDrop;
        }

        public void setCaracterePress(char caracterePress) {
            this.caracterePress = caracterePress;
        }

        public void setCaractereDrop(char caractereDrop) {
            this.caractereDrop = caractereDrop;
        }

        private BotaoGame(View view, final TelaGamePad atual, String name) {
            super(view);
            this.name = name;
            principal = view.getBackground();
            setTouch(new Tocavel() {
                @Override
                public void down() {
                    atual.send(caracterePress);
                }

                @Override
                public void up() {
                    atual.send(caractereDrop);

                }
            });
            load();
        }


    }

}
