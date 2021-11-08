package com.example.projeto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navigationView;

    private String ARQUIVO = "arquivo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.navbar);

        SharedPreferences sharedPreferences = getSharedPreferences("configs", MODE_PRIVATE);

        navigationView.setOnNavigationItemSelectedListener(this);
        SharedPreferences.Editor editor = getSharedPreferences(ARQUIVO, 0).edit();
        editor.putBoolean("statusConexao", false);
        editor.putString("MAC", "");
        editor.commit();
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