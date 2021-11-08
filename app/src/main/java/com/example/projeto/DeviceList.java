package com.example.projeto;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Set;

public class DeviceList extends ListActivity {

    private BluetoothAdapter bAdapter = null;
    Bundle b = new Bundle();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        bAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> devicesSet = bAdapter.getBondedDevices();
        if(devicesSet.size() > 0) {
            for (BluetoothDevice device : devicesSet) {
                String name = device.getName();
                String address = device.getAddress();
                ArrayBluetooth.add("\n"+name+"\n"+address);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String info = ((TextView)v).getText().toString();
        String addressMAC = info.substring(info.length() - 17); //Pega somente o Enderco MAC da String
        Toast.makeText(getApplicationContext(), addressMAC, Toast.LENGTH_LONG).show(); //Depuração
        Intent mac = new Intent();
        b.putString("stringMAC", addressMAC);
        mac.putExtra("MAC", b);
        setResult(RESULT_OK, mac);
        finish();
    }
}
