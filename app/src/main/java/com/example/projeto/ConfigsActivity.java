package com.example.projeto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ConfigsActivity extends AppCompatActivity {

    private Button btnConfirm;
    private Button btnCancel;

    private TextView txvTitulo;
    private TextView txvUmidadeSolo_max;
    private TextView txvUmidadeSolo_min;
    private TextView txvUmidadeAr_max;
    private TextView txvUmidadeAr_min;
    private TextView txvTemperatura_max;
    private TextView txvTemperatura_min;
    private TextView txvLuminosidade_max;
    private TextView txvLuminosidade_min;

    private SeekBar seekBar_umidadeSolo_max;
    private SeekBar seekBar_umidadeSolo_min;
    private SeekBar seekBar_temperatura_max;
    private SeekBar seekBar_temperatura_min;
    private SeekBar seekBar_umidadeAr_max;
    private SeekBar seekBar_umidadeAr_min;
    private SeekBar seekBar_luminosidade_max;
    private SeekBar seekBar_luminosidade_min;

    private int numVaso;
    private String ARQUIVO = "configs";
    private int umidadeSolo[] = new int[2];
    private int temperatura[] = new int[2];
    private int umidadeAr[] = new int[2];
    private int luminosidade[] = new int[2];

    private void seekBar_showValues(SeekBar seekBar, final TextView txv, final String sufixo) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txv.setText(String.valueOf(i)+sufixo);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configs);

        btnConfirm = findViewById(R.id.buttonConfig_Confirmar);
        btnCancel = findViewById(R.id.buttonConfig_Cancelar);
        txvTitulo = findViewById(R.id.txvTitulo);
        Bundle b = getIntent().getExtras();
        numVaso = b.getInt("vaso");
        txvTitulo.setText("Vaso "+numVaso+" - Configurações");

        ARQUIVO = ARQUIVO+"_vaso"+numVaso;

        SharedPreferences sharedPreferences = getSharedPreferences(ARQUIVO, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

    //Umidade do Solo
        txvUmidadeSolo_max = findViewById(R.id.txvUmidadeSolo_porceMax);
        txvUmidadeSolo_min = findViewById(R.id.txvUmidadeSolo_porceMin);
        seekBar_umidadeSolo_max = findViewById(R.id.seekUmidadeSolo_max);
        seekBar_umidadeSolo_max.setProgress(sharedPreferences.getInt("umidadeSolo_max", 100));
        seekBar_umidadeSolo_min = findViewById(R.id.seekUmidadeSolo_min);
        seekBar_umidadeSolo_min.setProgress(sharedPreferences.getInt("umidadeSolo_min", 0));
        seekBar_showValues(seekBar_umidadeSolo_max, txvUmidadeSolo_max, "%");
        seekBar_showValues(seekBar_umidadeSolo_min, txvUmidadeSolo_min, "%");
        txvUmidadeSolo_max.setText(sharedPreferences.getInt("umidadeSolo_max", 100)+"%");
        txvUmidadeSolo_min.setText(sharedPreferences.getInt("umidadeSolo_min", 0)+"%");

    //Temperatura
        txvTemperatura_max = findViewById(R.id.txvTemperatura_porceMax);
        txvTemperatura_min = findViewById(R.id.txvTemperatura_porceMin);
        seekBar_temperatura_max = findViewById(R.id.seekTemperatura_max);
        seekBar_temperatura_max.setMax(50);
        seekBar_temperatura_max.setProgress(sharedPreferences.getInt("temperatura_max", 50));
        seekBar_temperatura_min = findViewById(R.id.seekTemperatura_min);
        seekBar_temperatura_min.setMax(50);
        seekBar_temperatura_min.setProgress(sharedPreferences.getInt("temperatura_min", 0));
        seekBar_showValues(seekBar_temperatura_max, txvTemperatura_max, "°C");
        seekBar_showValues(seekBar_temperatura_min, txvTemperatura_min, "°C");
        txvTemperatura_max.setText(sharedPreferences.getInt("temperatura_max", 50)+"°C");
        txvTemperatura_min.setText(sharedPreferences.getInt("temperatura_min", 0)+"°C");

    //Umidade do Ar
        txvUmidadeAr_min = findViewById(R.id.txvUmidadeAr_porceMin);
        txvUmidadeAr_max = findViewById(R.id.txvUmidadeAr_porceMax);
        seekBar_umidadeAr_max = findViewById(R.id.seekUmidadeAr_max);
        seekBar_umidadeAr_max.setMax(80);
        seekBar_umidadeAr_max.setProgress(sharedPreferences.getInt("umidadeAr_max", 80));
        seekBar_umidadeAr_min = findViewById(R.id.seekUmidadeAr_min);
        seekBar_umidadeAr_min.setMax(80);
        seekBar_umidadeAr_min.setProgress(sharedPreferences.getInt("umidadeAr_min", 20));
        seekBar_showValues(seekBar_umidadeAr_max, txvUmidadeAr_max, "%");
        seekBar_showValues(seekBar_umidadeAr_min, txvUmidadeAr_min, "%");
        txvUmidadeAr_max.setText(sharedPreferences.getInt("umidadeAr_max", 80)+"%");
        txvUmidadeAr_min.setText(sharedPreferences.getInt("umidadeAr_min", 20)+"%");

    //Luminosidade
        txvLuminosidade_min = findViewById(R.id.txvLuminosidade_porceMin);
        txvLuminosidade_max = findViewById(R.id.txvLuminosidade_porceMax);
        seekBar_luminosidade_max = findViewById(R.id.seekLuminosidade_max);
        seekBar_luminosidade_max.setProgress(sharedPreferences.getInt("luminosidade_max", 100));
        seekBar_luminosidade_min = findViewById(R.id.seekLuminosidade_min);
        seekBar_luminosidade_min.setProgress(sharedPreferences.getInt("luminosidade_min", 0));
        seekBar_showValues(seekBar_luminosidade_max, txvLuminosidade_max, "%");
        seekBar_showValues(seekBar_luminosidade_min, txvLuminosidade_min, "%");
        txvLuminosidade_max.setText(sharedPreferences.getInt("luminosidade_max", 100)+"%");
        txvLuminosidade_min.setText(sharedPreferences.getInt("luminosidade_min", 0 )+ "%");

    //Botões
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                umidadeSolo[0] = TrataDados.getInt_toString(txvUmidadeSolo_min.getText(), 1);
                umidadeSolo[1] = TrataDados.getInt_toString(txvUmidadeSolo_max.getText(), 1);
                temperatura[0] = TrataDados.getInt_toString(txvTemperatura_min.getText(), 2);
                temperatura[1] = TrataDados.getInt_toString(txvTemperatura_max.getText(), 2);
                umidadeAr[0] = TrataDados.getInt_toString(txvUmidadeAr_min.getText(), 1);
                umidadeAr[1] = TrataDados.getInt_toString(txvUmidadeAr_max.getText(), 1);
                luminosidade[0] = TrataDados.getInt_toString(txvLuminosidade_min.getText(), 1);
                luminosidade[1] = TrataDados.getInt_toString(txvLuminosidade_max.getText(), 1);

                if (umidadeSolo[0] >= umidadeSolo[1] || temperatura[0] >= temperatura[1] ||
                    umidadeAr[0] >= umidadeAr[1] || luminosidade[0] >= luminosidade[1]) {
                    Toast.makeText(getApplicationContext(),
                            "O Intervalo que você deseja definir está incorreto!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    btnConfirm.setBackgroundResource(R.color.ciano2);
                    editor.putInt("umidadeSolo_max", umidadeSolo[1]);
                    editor.putInt("umidadeSolo_min", umidadeSolo[0]);
                    editor.putInt("temperatura_max", temperatura[1]);
                    editor.putInt("temperatura_min", temperatura[0]);
                    editor.putInt("umidadeAr_max", umidadeAr[1]);
                    editor.putInt("umidadeAr_min", umidadeAr[0]);
                    editor.putInt("luminosidade_max", luminosidade[1]);
                    editor.putInt("luminosidade_min", luminosidade[0]);
                    editor.commit();
                    ConfigsActivity.this.finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCancel.setBackgroundResource(R.color.ciano2);
                ConfigsActivity.this.finish();
            }
        });

    }



}