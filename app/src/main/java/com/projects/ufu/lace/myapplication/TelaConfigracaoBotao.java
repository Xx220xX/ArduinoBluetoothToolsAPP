package com.projects.ufu.lace.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.projects.ufu.lace.myapplication.arquivos.IOData;

public class TelaConfigracaoBotao extends Activity {
    ButtonEspessial t, o, x, q, setaDireita, setaEsquerda, setaCima, setaBaixo, start, select;
    Button voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_configracao_botao);
        voltar = findViewById(R.id.confbotaovoltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        t = new ButtonEspessial(findViewById(R.id.editriangulo), 0);
        o = new ButtonEspessial(findViewById(R.id.editbolinha), 1);
        x = new ButtonEspessial(findViewById(R.id.editx), 2);
        q = new ButtonEspessial(findViewById(R.id.editquadrado), 3);
        start = new ButtonEspessial(findViewById(R.id.editstart), 4);
        select = new ButtonEspessial(findViewById(R.id.editselect), 5);
        setaDireita = new ButtonEspessial(findViewById(R.id.editsetaDireita), 6);
        setaBaixo = new ButtonEspessial(findViewById(R.id.editsetabaixo), 7);
        setaEsquerda = new ButtonEspessial(findViewById(R.id.editsetaesquerda), 8);
        setaCima = new ButtonEspessial(findViewById(R.id.editsetacima), 9);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), TelaGamePad.class));
        finish();
    }





    static class Dados {
        static TelaGamePad.BotaoGame botoes[];

        public static TelaGamePad.BotaoGame[] getBotoes() {
            return botoes;
        }

        public static void setBotoes(TelaGamePad.BotaoGame[] botoes) {
            Dados.botoes = botoes;
        }
    }

    class ButtonEspessial extends ButtonEspecial {
        int id;

        public ButtonEspessial(View view, int referenceId) {
            super(view);
            id = referenceId;
            setScale(0);
            this.setAction(new Clicavel() {
                @Override
                public void acao() {
                    TelaEditarBotao.setIndex(id);
                    startActivity(new Intent(getApplicationContext(), TelaEditarBotao.class));
                    finish();
                }

                @Override
                public void acaoLonga() {

                }
            });
        }


        public int getId() {
            return id;
        }
    }
}
