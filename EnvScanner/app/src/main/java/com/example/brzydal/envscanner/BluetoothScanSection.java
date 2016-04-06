package com.example.brzydal.envscanner;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class BluetoothScanSection extends Activity {
    private ListView bluetoothScanResultListView;
    private ArrayAdapter<String> bluetoothArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan_section);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            ArrayList<BluetoothDevice> bluetoothScanResultList = (ArrayList<BluetoothDevice>) extras.get("ALL_SCAN_RESULTS");
            ArrayList<String> devicesInformationList = new ArrayList<>();
            for (BluetoothDevice device : bluetoothScanResultList)
                devicesInformationList.add(device.getName() + ": " + device.getAddress());

            bluetoothScanResultListView = (ListView) findViewById(R.id.bluetoothScanResults);
            bluetoothArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devicesInformationList);
            bluetoothScanResultListView.setAdapter(bluetoothArrayAdapter);
        }
    }
}
