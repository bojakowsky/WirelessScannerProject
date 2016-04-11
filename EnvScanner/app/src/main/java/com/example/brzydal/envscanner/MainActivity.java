package com.example.brzydal.envscanner;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends Activity {

    private TextView deviceMainText;

    //Switches
    private Switch wifiSwitch;
    private Switch bluetoothSwitch;

    //Buttons
    private Button wifiButton;
    private Button bluetoothButton;

    //Wifi section
    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;
    private List<ScanResult> wifiScanResultList = new ArrayList<>();
    StringBuilder sb = new StringBuilder();

    //BlueTooth section
    private BluetoothReceiver receiverBluetooth;
    private ArrayList<BluetoothDevice> bluetoothScanResultList = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private final Handler handler = new Handler();

    //GPS
    GPSLocalization GPSLocalizer;


    public void InitializeView(){
        deviceMainText = (TextView) findViewById(R.id.mainText);

        wifiSwitch = (Switch) findViewById(R.id.wifiSwitcher);
        bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitcher);

        wifiButton = (Button) findViewById(R.id.wifiButton);
        bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
    }

    public void InitializeGPS(){
        GPSLocalizer = new GPSLocalization(this);

        if (!GPSLocalizer.IsGPSAvaiable()) {
            new AlertDialog.Builder(this)
                    .setTitle("GPS")
                    .setMessage("GPS is disabled, application will not work properly. Do you wish to enable it?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void WifiConfiguration()
    {
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (mainWifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "Wifi is disabled. Enabling it!",
                    Toast.LENGTH_LONG).show();

            mainWifi.setWifiEnabled(true);
        }

        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mainWifi.startScan();
    }

    public void BluetoothConfiguration()
    {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothReceiver receiverBluetooth = new BluetoothReceiver();
        registerReceiver(receiverBluetooth, filter);
        if (!bluetoothAdapter.isEnabled())
        {
            Toast.makeText(getApplicationContext(), "Bluetooth is disabled. Enabling it!",
                    Toast.LENGTH_LONG).show();

            bluetoothAdapter.enable();
        }
        bluetoothAdapter.startDiscovery();
    }

    public void InitializeWifi()
    {
        //Check change listener - user click
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    WifiConfiguration();
                    wifiButton.setEnabled(true);
                } else wifiButton.setEnabled(false);
            }
        });

        //On click listener, starting activity using intent on item click
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WifiScanSection.class);
                intent.putExtra("ALL_SCAN_RESULTS", wifiScanResultList.toArray());
                startActivity(intent);
            }
        });
    }

    public void InitializeBluetooth()
    {
        //Listener on check changed - user click on slider
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BluetoothConfiguration();
                    bluetoothButton.setEnabled(true);
                } else bluetoothButton.setEnabled(false);
            }
        });

        //on click listener - on bluetooth list item event starting new activity
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothScanSection.class);
                intent.putExtra("ALL_SCAN_RESULTS", bluetoothScanResultList);
                startActivity(intent);
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeView();
        InitializeWifi();
        InitializeBluetooth();
        InitializeGPS();
        doScanning();
    }



    public void doScanning()
    {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                PrintScanningData();
                if (wifiSwitch.isChecked())
                    mainWifi.startScan();
                if (bluetoothSwitch.isChecked())
                    bluetoothAdapter.startDiscovery();
                doScanning();
            }
        }, 12000);

    }

    protected void PrintScanningData()
    {
        sb = new StringBuilder();

        sb.append("Wifi connections : " + wifiScanResultList.size() + "\n");
        sb.append("Bluetooth connections: " + bluetoothScanResultList.size() + "\n");
        sb.append("Android API : " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n");
        sb.append("Android ID : " + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) + "\n");
        sb.append("Coordinates : " + GPSLocalizer.GetLocation());
        deviceMainText.setText(sb);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    protected void onStop()
    {
        super.onStop();
    }

    protected void onDestroy()
    {
        unregisterReceiver(receiverWifi);
        mainWifi.setWifiEnabled(false);
        bluetoothAdapter.disable();
        showToast("Remember to disable GPS, BT, WIFI!");
        super.onDestroy();
    }

    protected void onPause() {
        //unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            wifiScanResultList = mainWifi.getScanResults();
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                bluetoothScanResultList.clear();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //No action
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!bluetoothScanResultList.contains(device))
                    bluetoothScanResultList.add(device);

            }
            //bluetoothArrayAdapter.notifyDataSetChanged();
        }
    }
}
