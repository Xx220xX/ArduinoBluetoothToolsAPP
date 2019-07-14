package com.projects.ufu.lace.myapplication.arquivos;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOData {
    BufferedOutputStream bw;
    BufferedInputStream br;
    File file;


    public IOData(Context context, String name) {

        file = new File(context.getObbDir(), name + ".bta");
        if (!file.exists()) {
            try {
                file.createNewFile();
                salveArq('0');
                salveArq('0');
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasFile() {
        return file.exists();
    }

    public synchronized int loadCaractere() {
        if (br == null) {
            try {
                br = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            return br.read();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized void salveArq(int byt) {
        try {
            if (bw == null)
                bw = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            bw.write(byt);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (br != null)
                br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (bw != null)
                bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/***MOdelo de salvamento
 *
 * caractere correspondente a cada botao , duas vezes
 * padrao é '0' = 48
 */
