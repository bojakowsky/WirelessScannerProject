package com.example.brzydal.envscanner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends Activity {

    private TextView deviceMainText;

    private Switch wifiSwitch;
    private Switch bluetoothSwitch;

    private Button wifiButton;
    private Button bluetoothButton;

    //Wifi section
    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;

    private List<ScanResult> wifiScanResultList;
    private ListView wifiScanResultListView;

    private ArrayList<String> wifiSSIDList = new ArrayList<String>();
    private ArrayAdapter<String> wifiArrayAdapter;

    StringBuilder sb = new StringBuilder();

    //BlueTooth section
    private BluetoothReceiver receiverBluetooth;
    private ArrayList<BluetoothDevice> bluetoothScanResultList = new ArrayList<>();
    private ListView bluetoothScanResultListView;

    private ArrayList<String> bluetoothDeviceNameList = new ArrayList<String>();
    private ArrayAdapter<String> bluetoothArrayAdapter;
    BluetoothAdapter bluetoothAdapter;
    private final Handler handler = new Handler();



    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        deviceMainText = (TextView) findViewById(R.id.mainText);

        wifiSwitch = (Switch) findViewById(R.id.wifiSwitcher);
        bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitcher);

        wifiButton = (Button) findViewById(R.id.wifiButton);
        bluetoothButton = (Button) findViewById(R.id.bluetoothButton);

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    WifiConfiguration();
                    wifiButton.setEnabled(true);
                }
                else wifiButton.setEnabled(true);
            }
        });

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    BluetoothConfiguration();
                    bluetoothButton.setEnabled(true);
                }
                else bluetoothButton.setEnabled(false);
            }
        });


        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WifiScanSection.class);
                intent.putExtra("SCAN_RESULT", wifiScanResultList.toArray());
                startActivity(intent);
            }
        });


        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothScanSection.class);
                intent.putExtra("SCAN_RESULT", bluetoothScanResultList);
                startActivity(intent);
            }
        });


        doScanning();
    }

    public void WifiConfiguration()
    {
        // Initiate wifi service manager
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        wifiScanResultListView = (ListView) findViewById(R.id.wifiScanResults);

        // Check for wifi is disabled
        if (mainWifi.isWifiEnabled() == false)
        {
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "Wifi is disabled.. making it enabled",
                    Toast.LENGTH_LONG).show();

            mainWifi.setWifiEnabled(true);
        }

        wifiArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wifiSSIDList);
        wifiScanResultListView.setAdapter(wifiArrayAdapter);

        wifiScanResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult scanResult = wifiScanResultList.get(position);
                Intent intent = new Intent(MainActivity.this, WifiScanDetails.class);
                intent.putExtra("SCAN_RESULT", scanResult);
                startActivity(intent);
            }
        });

        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void BluetoothConfiguration()
    {
        bluetoothScanResultListView = (ListView) findViewById(R.id.bluetoothScanResults);

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothReceiver receiverBluetooth = new BluetoothReceiver();
        registerReceiver(receiverBluetooth, filter);

        bluetoothArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bluetoothDeviceNameList);
        bluetoothScanResultListView.setAdapter(bluetoothArrayAdapter);
    }

    public void doScanning()
    {
        handler.postDelayed(new Runnable() {

            @Override
            public void run()
            {
                if (wifiSwitch.isEnabled())
                    mainWifi.startScan();
                if (bluetoothSwitch.isEnabled())
                    bluetoothAdapter.startDiscovery();

                doScanning();
            }
        }, 12000);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        mainWifi.startScan();
        deviceMainText.setText("Starting Scan");
        return super.onMenuItemSelected(featureId, item);
    }

    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    // Broadcast receiver class called its receive method
    // when number of wifi connections changed
    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {

            sb = new StringBuilder();
            wifiScanResultList = mainWifi.getScanResults();
            sb.append("Wifi connections :" + wifiScanResultList.size() + "\n");
            sb.append("Android API :" + Integer.valueOf(android.os.Build.VERSION.SDK));
            deviceMainText.setText(sb);

            wifiSSIDList.clear();
            for (int i = 0 ; i < wifiScanResultList.size(); i++)
                wifiSSIDList.add(wifiScanResultList.get(i).SSID);
            wifiArrayAdapter.notifyDataSetChanged();
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                bluetoothScanResultList.clear();
                bluetoothDeviceNameList.clear();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Toast.makeText(getApplicationContext(), "ENDEEED", Toast.LENGTH_LONG);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!bluetoothScanResultList.contains(device) && !bluetoothDeviceNameList.contains(device.getName()))
                {
                    bluetoothScanResultList.add(device);
                    bluetoothDeviceNameList.add(device.getName());
                }
            }
            bluetoothArrayAdapter.notifyDataSetChanged();
        }
    }
}
