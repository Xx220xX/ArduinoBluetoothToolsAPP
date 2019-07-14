package com.projects.ufu.lace.myapplication.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;


public class Bluetooth {
    public static final int REQUIRE_TURN_ON = 2143;
    public static final int REQUIRE_TURN_OFF = 1144;
    public static final int REQUIRE_SEARCH_STARTED = 1876;
    public static final int REQUIRE_SEARCH_FOUND_DEVICE = 5785;
    public static final int REQUIRE_SEARCH_FINISHED = 481984;
    public static final int REQUIRE_STOP_SEARCH = 5451;
    public static final int REQUIRE_CONNECT_START = 786419;
    public static final int REQUIRE_CONNECT_FAIL = 8316;

    private boolean requestalert = true;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;

    private final BroadcastReceiver broadcastReceiver;
    private Activity activityAtual;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    private AcceptThread aceitarConexao;
    //    private ConnectThread connectThread;
    private EstaConectadoThread conectadoThread;
    private Comunicavel comunicacao;
    private final static String TAG = "Bluetooth";

    private static Bluetooth instance = null;

    public static Bluetooth getInstance(Activity activity, Comunicavel comunicacao) {
        if (instance == null) instance = new Bluetooth(activity);
        instance.activityAtual = activity;
        instance.comunicacao = comunicacao;
        if (instance.activityAtual == null || instance.comunicacao == null)
            throw new NullPointerException("instancia nao inciada");
        return instance;
    }


    private Bluetooth(Activity activity) {
        activityAtual = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= 23) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ContextCompat.checkSelfPermission(activityAtual.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(activityAtual, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                }
            });
        }
        instance = this;
    }

    public static boolean dispositivoPossuiBluetooth() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }


    public void turnOn() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!bluetoothAdapter.isEnabled()) ;
                    onRequestFinish(REQUIRE_TURN_ON);
                }
            }, "wait turn on Complete").start();
        }
    }

    public void turnOff() {
        bluetoothAdapter.disable();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (bluetoothAdapter.isEnabled()) ;
                onRequestFinish(REQUIRE_TURN_OFF);
            }
        }, "wait turn off Complete").start();
    }

    public boolean isSearching() {
        return bluetoothAdapter.isDiscovering();
    }

    public Set<BluetoothDevice> getDevicesPaired() {
        return bluetoothAdapter.getBondedDevices();
    }

    public void buscarDispositivosProximos() {
        if (!bluetoothAdapter.isEnabled()) return;
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activityAtual.registerReceiver(broadcastReceiver, filter1);
        activityAtual.registerReceiver(broadcastReceiver, filter2);
        activityAtual.registerReceiver(broadcastReceiver, filter3);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    public void pararBusca() {
        try {
            activityAtual.unregisterReceiver(broadcastReceiver);
        } catch (java.lang.IllegalArgumentException e) {
            e.printStackTrace();
        }
        onRequestFinish(REQUIRE_STOP_SEARCH);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        onRequestFinish(REQUIRE_SEARCH_FINISHED);

    }

    public boolean isEnable() {
        return bluetoothAdapter.isEnabled();
    }

    public void aceitarComoServidor() {
        Log.d(TAG, "dispositivo iniciado para conexao");
        if (aceitarConexao != null) aceitarConexao.cancel();
        aceitarConexao = new AcceptThread("esperando conectar");
        aceitarConexao.start();
    }


    public void conectar(final String deviceAddress) {
        if (!bluetoothAdapter.isEnabled()) {
            onRequestFinish(Bluetooth.REQUIRE_TURN_OFF);
            return;
        }
        pararBusca();
        Log(TAG + "  address " + deviceAddress);
        new Thread() {
            public void run() {
                boolean fail = false;
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
                onRequestFinish(REQUIRE_CONNECT_START);
                try {
                    socket = createBluetoothSocket(device);
                } catch (IOException e) {
                    fail = true;
                    Log("Socket creation failed");
                    e.printStackTrace();
                }
                // Establish the Bluetooth socket connection.
                try {
                    socket.connect();
                } catch (IOException e) {
                    Log(e.getMessage());
                    e.printStackTrace();
                    try {
                        fail = true;
                        socket.close();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Log("Socket creation failed");
                        e.printStackTrace();
                    }
                }
                if (!fail) {
                    onConect(device.getName(), socket.isConnected());
                } else {
                    onRequestFinish(REQUIRE_CONNECT_FAIL, "Falha");
                }
            }
        }.start();

    }

    public void iniciarOuContinuarServicos() {
        if (conectadoThread == null) {
            conectadoThread = new EstaConectadoThread(socket);
            conectadoThread.start();
        } else {
            if (!conectadoThread.isAlive()) {
                conectadoThread = new EstaConectadoThread(socket);
                conectadoThread.start();
            }
        }
    }


    public void desconectar() {
        conectadoThread.cancelWithOutCallBack();
    }

    public synchronized void enviarDados(String dados) {
        conectadoThread.write(dados.getBytes());
    }

    public synchronized void enviarDados(char dados) {
        conectadoThread.write((int) dados);

    }

    /*Instanciando broadcastReceiver*/ {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    onRequestFinish(REQUIRE_SEARCH_STARTED);
                }
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (comunicacao != null) {
                        onRequestFinish(REQUIRE_SEARCH_FOUND_DEVICE, device);
                    }
                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    onRequestFinish(REQUIRE_SEARCH_FINISHED);
                    activityAtual.unregisterReceiver(broadcastReceiver);
                }
            }

        };

    }

    private void onDisconect() {
        if (requestalert)
            activityAtual.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    comunicacao.onDisconect();
                }
            });
    }

    private void Log(final String msg) {
        activityAtual.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                comunicacao.Log(msg);
            }
        });
    }

    private void onRequestFinish(int request) {
        if (comunicacao != null)
            onRequestFinish(request, null);
    }

    private void onRequestFinish(final int request, final Object result) {
        activityAtual.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                comunicacao.onRequestFinish(request, result);
            }
        });

    }

    public boolean isAccepintgDevices() {
        return aceitarConexao != null && aceitarConexao.isAlive();
    }

    public static boolean isConected() {
        if (instance == null) return false;
        if (instance.socket == null) return false;
        return instance.socket.isConnected();
    }

    private void onConect(final String bluetoothName, final boolean isconected) {
        if (aceitarConexao != null)
            aceitarConexao.cancel();
        pararBusca();
        activityAtual.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                comunicacao.onConnect(bluetoothName, isconected);
            }
        });
    }

    private void dadosReceived(int size, byte bytes[]) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append((char) b);
        }
        final String s = stringBuilder.toString();
        activityAtual.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                comunicacao.dadosReceived(s);
            }
        });
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public String getDeviceNameConected() {
        return socket.getRemoteDevice().getName();
    }

    public static void clear() {
        if (instance != null) {
            if (instance.socket != null) {
                instance.requestalert = false;
                try {
                    instance.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            instance = null;
        }
    }

    class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServersocket;

        private AcceptThread(String name) {
            super(name);
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("servico", BTMODULEUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmServersocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket bluetoothSocket = null;
            for (; ; ) {
                try {
                    bluetoothSocket = mmServersocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                //se a conexao foi estabelecida
                if (bluetoothSocket != null) {
                    socket = bluetoothSocket;
                    if (comunicacao != null)
                        onConect(bluetoothSocket.getRemoteDevice().getName(), bluetoothSocket.isConnected());
                    try {
                        mmServersocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

        }

        public void cancel() {
            try {
                mmServersocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class EstaConectadoThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInput;
        private final OutputStream mmOutput;

        EstaConectadoThread(BluetoothSocket socket) {
            super("ESPERANDO DADOS a receber");
            mmSocket = socket;
            InputStream inTmp = null;
            OutputStream outTmp = null;
            try {
                inTmp = socket.getInputStream();
                outTmp = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInput = inTmp;
            mmOutput = outTmp;

        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (socket.isConnected()) {
                try {
                    // Read from the InputStream
                    bytes = mmInput.available();
                    if (bytes != 0) {
                        SystemClock.sleep(50);
                        bytes = mmInput.available();
                        buffer = new byte[bytes];
                        bytes = mmInput.read(buffer, 0, bytes);
                        dadosReceived(bytes, buffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            cancel();

        }

        private synchronized void write(int dado) {
            try {
                mmOutput.write(dado);
            } catch (IOException e) {
                cancel();
                e.printStackTrace();
            }

        }

        private synchronized void write(byte[] bytes) {
            if (bytes == null) return;
            try {
                mmOutput.write(bytes);
            } catch (IOException e) {
                cancel();
                e.printStackTrace();
            }
        }

        public void cancel() {

            onDisconect();
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancelWithOutCallBack() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Comunicavel {
        void onRequestFinish(int request, Object result);

        void dadosReceived(String dados);

        void onConnect(String bluetoothName, boolean Isconected);

        void onDisconect();

        void Log(String msg);
    }
}