package com.projects.ufu.lace.myapplication;


import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.ufu.lace.myapplication.arquivos.IOData;
import com.projects.ufu.lace.myapplication.bluetooth.Bluetooth;

public class MainActivity extends Activity {
    TextView textView;
    private boolean falha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.statuschangemain);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setStatus(getString(R.string.verificando_requisitos));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!Bluetooth.dispositivoPossuiBluetooth()) {
                    falha = true;
                }

                setStatus(getString(R.string.prontinho));
                iniciar();
            }
        }).start();
    }

    private void setStatus(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(txt);
            }
        });
    }

    private void iniciar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (falha) {
                    AlertDialog alerta =  new AlertDialog.Builder(MainActivity.this).create();
                    alerta.setTitle(getString(R.string.falha));
                    alerta.setMessage(getString(R.string.dispositivo_nao_suportado));
                  alerta.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                          finish();
                      }
                  });
                   alerta.show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.bem_vindo), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), TelaBluetooth.class));
                    finish();

                }
            }
        });

    }

}

