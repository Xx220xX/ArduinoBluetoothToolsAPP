package com.projects.ufu.lace.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TelaEditarBotao extends Activity {

    private static int index = 0;
    private EditText press, drop;
    private Button back;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_botao);

        press = findViewById(R.id.caracterpress);
        drop = findViewById(R.id.caracterdrop);
        back = findViewById(R.id.buttonokeditar);
        view = findViewById(R.id.viewButton);

        view.setBackground(TelaConfigracaoBotao.Dados.getBotoes()[index].getPrincipal());
        press.setText((TelaConfigracaoBotao.Dados.getBotoes()[index].getCaracterePress() + ""));
        drop.setText((TelaConfigracaoBotao.Dados.getBotoes()[index].getCaractereDrop() + ""));


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public static void setIndex(int index) {
        TelaEditarBotao.index = index;
    }

    @Override
    public void onBackPressed() {

        String s = press.getText().toString();
        char cpress = s.length() > 0 ? s.charAt(0) : '0';
        s = drop.getText().toString();
        char cdrop = s.length() > 0 ? s.charAt(0) : '0';
        if (cpress != TelaConfigracaoBotao.Dados.getBotoes()[index].getCaracterePress()) {
            TelaConfigracaoBotao.Dados.getBotoes()[index].setCaracterePress(cpress);
        }
        if (cdrop != TelaConfigracaoBotao.Dados.getBotoes()[index].getCaractereDrop()) {
            TelaConfigracaoBotao.Dados.getBotoes()[index].setCaractereDrop(cdrop);
        }
        Toast.makeText(getApplicationContext(),cpress+"  "+ cdrop,Toast.LENGTH_SHORT).show();
        TelaConfigracaoBotao.Dados.getBotoes()[index].save();
        startActivity(new Intent(getApplicationContext(), TelaConfigracaoBotao.class));
        finish();

    }
}
