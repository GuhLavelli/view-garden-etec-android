package com.example.projeto;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MonitoramentoActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navigationView;
    private ProgressBar progressBarVaso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoramento);
        navigationView = findViewById(R.id.navbar);
        progressBarVaso = findViewById(R.id.progressBarVaso);
        progressBarVaso.setVisibility(View.INVISIBLE);
        navigationView.setOnItemSelectedListener(this);

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

    public void onClick_vaso1(View view) {
        progressBarVaso.setVisibility(View.VISIBLE);
        Intent i = new Intent(this, VasoActivity.class);
        i.putExtra("vaso", 1);
        startActivity(i);
    }
    public void onClick_vaso2(View view) {
        progressBarVaso.setVisibility(View.VISIBLE);
        Intent i = new Intent(this, VasoActivity.class);
        i.putExtra("vaso", 2);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressBarVaso.setVisibility(View.INVISIBLE);
    }
}
