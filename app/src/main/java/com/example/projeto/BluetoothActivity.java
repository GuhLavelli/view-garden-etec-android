package com.example.projeto;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView navigationView;
    Button btnConexao;
    ProgressBar progressBar;

    private String ARQUIVO = "arquivo";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private boolean statusConexao = false; //Inicia como desligado

    private UUID myUIID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //RFCOM, canal padrão para alguns módulos
    private String MAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);


        SharedPreferences sharedPreferences = getSharedPreferences(ARQUIVO, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        navigationView = findViewById(R.id.navbar);
        btnConexao = findViewById(R.id.btnConexao);
        progressBar = findViewById(R.id.progressBar);

        navigationView.setOnNavigationItemSelectedListener(this);

        boolean sharedValue = sharedPreferences.getBoolean("statusConexao", true);
        if (sharedValue) {
            statusConexao = true;
            btnConexao.setText("Desconectar");
        }else {
            statusConexao = false;
            btnConexao.setText("Conectar");
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    "Esse dispositivo não possui Bluetooth",
                    Toast.LENGTH_SHORT).show();
            finish();
        }else if (!bluetoothAdapter.isEnabled()) {
            Intent turnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //Intent que pede permisão para o usuário para ligar o Bluetooth
            startActivityForResult(turnBluetooth, 1);

        }

        //Botão Conectar
        btnConexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusConexao) { //Se tiver ligado
                    statusConexao = false;
                    try {
                        bluetoothSocket.close();
                        editor.commit();
                    } catch(Exception e) { //IOException
                        System.out.println(e);
                    }
                    editor.putBoolean("statusConexao", false);
                    editor.commit();
                    btnConexao.setText("Conectar");
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    Intent deviceList = new Intent(BluetoothActivity.this, DeviceList.class);
                    startActivityForResult(deviceList, 2);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: //Solicita permissão para ligar o Bluetooth
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(),
                            "Bluetooth: ON",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Bluetooth: OFF",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 2: //Solicita conexão com um Dispositivo
                if (resultCode == Activity.RESULT_OK) {
                    //System.out.println("aaaa");
                    Bundle blunde = data.getBundleExtra("MAC"); //"Pega" o blunde de endereço MAC
                    MAC = blunde.getString("stringMAC");
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC);
                    try {
                        //System.out.println("try");
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUIID);
                        //System.out.println("antes");
                        bluetoothSocket.connect();
                        //System.out.println("conectc");
                        statusConexao = true;
                        btnConexao.setText("Desconectar");
                        Toast.makeText(getApplicationContext(),
                                "Você está conectado com: " + MAC,
                                Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),
                                "Não foi possivel realizar a conexão com esse Dispositvo\n" + e,
                                Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Você não se conectou a nenhum dispositivo",
                            Toast.LENGTH_LONG).show();
                }
                if(statusConexao) { //Se a Conexão foi possivel encerro o socket e passo os valores pra um Arquivo interno
                    try {
                        bluetoothSocket.close();
                    } catch(IOException e) {}
                    SharedPreferences.Editor editor = getSharedPreferences(ARQUIVO, 0).edit();
                    editor.putString("MAC", MAC);
                    editor.putBoolean("statusConexao", true);
                    editor.commit();
                }
                progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth:
                Intent bAct = new Intent(this, BluetoothActivity.class);
                startActivity(bAct);
                return true;
            case R.id.monitoramento:
                Intent mAct = new Intent(this, MonitoramentoActivity.class);
                startActivity(mAct);
                return true;
        }
        return false;
    }
}
