package com.example.projeto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class VasoActivity extends AppCompatActivity {

    private int numVaso = 0;
    //Valores pré-setados pelo usuário ([0]=min, [1]=max)
    private int umidadeSolo[] = new int[2];
    private int temperatura[] = new int[2];
    private int umidadeAr[] = new int[2];
    private int luminosidade[] = new int[2];

    private TextView txvTitulo;
    private FloatingActionButton btnVoltar;
    private FloatingActionButton btnConfigs;
    private TextView txvUmidadeSolo;
    private TextView txvTemperatura;
    private TextView txvUmidadeAr;
    private TextView txvLuminosidade;
    private TextView txvAviso;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket  = null;

    private String ARQUIVO_MAC = "arquivo";
    private String ARQUIVO_CONFIGS = "configs";

    private UUID myUIID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //RFCOM, canal padrão para alguns módulos
    private String MAC;

    private static Handler mHandler;
    StringBuilder infoReceived = new StringBuilder();
    ConnectedThread connectedThread;

    private void avisos(double dataSolo, double dataTemp, double dataAr, double dataLum) {
        String avisos = "";
        if (dataSolo < umidadeSolo[0]) {
            avisos += "O solo está muito seco\n";
        }else if (dataSolo > umidadeSolo[1]) {
            avisos += "O solo está muito úmido\n";
        }

        if (dataTemp < temperatura[0]) {
            avisos += "A temperatura está muito baixa\n";
        }else if (dataTemp > temperatura[1]) {
            avisos += "A temperatura está muito alta\n";
        }

        if (dataAr < umidadeAr [0]) {
            avisos += "O Ar está muito seco\n";
        }else if (dataAr > umidadeAr[1]) {
            avisos += "O Ar está muito úmido\n";
        }

        if (dataLum < luminosidade[0]) {
            avisos += "A planta está recebendo pouca luz\n";
        }else if (dataLum > luminosidade[1]) {
            avisos += "A planta está recebendo muita luz\n";
        }
        txvAviso.setText(avisos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaso);

        //Arquivo de armazenamento do endereço MAC
        SharedPreferences sharedMAC = getSharedPreferences(ARQUIVO_MAC, 0);
        boolean statusConexao = sharedMAC.getBoolean("statusConexao", true);
        if (statusConexao) {
            MAC = sharedMAC.getString("MAC", null); //Pega o valor MAC de um ARQUIVO_MAC que foi gravado na BluetoothActivity
        } else {
            this.finish(); //Fecha essa Activity
            Toast.makeText(getApplicationContext(),
                    "Por favor, conecte-se a um dispositvo Bluetooth",
                    Toast.LENGTH_SHORT).show();
        }

        txvTitulo = findViewById(R.id.txvTitulo);
        btnVoltar = findViewById(R.id.btnVoltar);
        txvUmidadeSolo = findViewById(R.id.txvUmidadeSolo);
        txvTemperatura = findViewById(R.id.txvTemperatura);
        txvUmidadeAr = findViewById(R.id.txvUmidadeAr);
        txvLuminosidade = findViewById(R.id.txvLuminosidade);
        txvAviso = findViewById(R.id.txvAvisos);
        btnConfigs = findViewById(R.id.btnConfigs);

        //Pega o valor do vaso em que o usuário escolheu
        Bundle b = getIntent().getExtras();
        numVaso = b.getInt("vaso");
        txvTitulo.setText("Vaso " + numVaso);

        //Arquivo referente as configurações do vaso que foi selecionado
        ARQUIVO_CONFIGS = ARQUIVO_CONFIGS+"_vaso"+numVaso;
        SharedPreferences sharedConfigs = getSharedPreferences(ARQUIVO_CONFIGS, MODE_PRIVATE);
        umidadeSolo[0] = sharedConfigs.getInt("umidadeSolo_min", 0);
        umidadeSolo[1] = sharedConfigs.getInt("umidadeSolo_max", 100);
        temperatura[0] = sharedConfigs.getInt("temperatura_min", 0);
        temperatura[1] = sharedConfigs.getInt("temperatura_max", 50);
        umidadeAr[0] = sharedConfigs.getInt("umidadeAr_min", 20);
        umidadeAr[1] = sharedConfigs.getInt("umidadeAr_max", 80);
        luminosidade[0] = sharedConfigs.getInt("luminosidade_min", 100);
        luminosidade[1] = sharedConfigs.getInt("luminosidade_max", 0);

        if (statusConexao) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC);
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUIID);
                bluetoothSocket.connect();

                connectedThread = new ConnectedThread(bluetoothSocket);
                connectedThread.start();
            } catch (IOException e) {
                System.out.println("erro: " + e);
            }
        }

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VasoActivity.this.finish();
            }
        });

        btnConfigs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VasoActivity.this, ConfigsActivity.class);
                i.putExtra("vaso", numVaso);
                VasoActivity.this.finish();
                startActivity(i);
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String receiver = (String) msg.obj;
                    infoReceived.append(receiver);
                    int endReceiver = infoReceived.indexOf("]");
                    if (endReceiver > 0) {
                        String receiverComp = infoReceived.substring(0, endReceiver);
                        if (infoReceived.charAt(0) == '[') {
                            String dataFinal = infoReceived.substring(1, (receiverComp.length()));
                            Log.d("Recebidos", dataFinal);
                            double data[] = TrataDados.parseData(dataFinal);
                            if (data[0] == numVaso) {
                                txvUmidadeSolo.setText(TrataDados.convertAnalog_Porcentagem(data[1]) + "%");
                                txvTemperatura.setText(data[2] + "°C");
                                txvUmidadeAr.setText(data[3] + "%");
                                txvLuminosidade.setText(TrataDados.convertAnalog_Porcentagem(data[4]) + "%");
                                avisos(TrataDados.convertAnalog_Porcentagem(data[1]), data[2], data[3], TrataDados.convertAnalog_Porcentagem(data[4]));
                            }
                        }
                        infoReceived.delete(0, infoReceived.length());
                    }
                }
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            bluetoothSocket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream inStream;
        private final OutputStream outStream;


        private ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {

            }
            inStream = tmpIn;
            outStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes =  inStream.read(buffer);
                    String data = new String(buffer, 0 , bytes);
                    mHandler.obtainMessage(1, bytes, -1, data).sendToTarget();
                } catch(IOException e) {
                    System.out.println("error");
                    break;
                }
            }
        }
    }
}