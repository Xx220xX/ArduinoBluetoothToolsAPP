package com.projects.ufu.lace.myapplication;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class ButtonEspecial {
    private View v;
    private Clicavel clicavel;
    private Tocavel tocavel;
    private boolean isLong = false;
    private Drawable up, donw;
    private float X0, Y0, x, y;
    private int percentoal = 10;

    public void setImage(Drawable press, Drawable drop) {
        up = drop;
        donw = press;
    }

    public ButtonEspecial(View v, Drawable press, Drawable drop) {
        this(v);
        setImage(press, drop);
    }

    public ButtonEspecial(View view) {
        this.v = view;
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (donw != null) {
                            v.setBackground(donw);
                        }
                        if (percentoal != 0) {
                            v.setScaleX(x);
                            v.setScaleY(y);
                        }
                        if (tocavel != null)
                            tocavel.down();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (up != null) {
                            v.setBackground(up);
                        }
                        if (percentoal != 0) {
                            v.setScaleX(X0);
                            v.setScaleY(Y0);
                        }
                        if (tocavel != null)
                            tocavel.up();
                        break;
                    default:
                        break;

                }
                return false;
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLong) {
                    if (clicavel != null)
                        clicavel.acao();
                }
                isLong = false;
            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLong = true;
                if (clicavel != null)
                    clicavel.acaoLonga();
                return false;
            }
        });
        X0 = v.getScaleX();
        Y0 = v.getScaleY();
        setScale(percentoal);
    }

    public void setScale(int percentoal) {
        this.percentoal = percentoal;
        x = X0 + ((float) percentoal * X0) / 100f;
        y = Y0 + ((float) percentoal * Y0) / 100f;

    }

    public void setAction(Clicavel clicavel) {
        this.clicavel = clicavel;
    }

    public void setTouch(Tocavel tocavel) {
        this.tocavel = tocavel;
    }

    public interface Clicavel {
        void acao();

        void acaoLonga();
    }

    public interface Tocavel {
        void down();

        void up();
    }
}

