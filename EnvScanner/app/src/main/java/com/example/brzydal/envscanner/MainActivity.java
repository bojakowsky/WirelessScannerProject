package com.example.brzydal.envscanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.provider.Settings.Secure;

import com.example.brzydal.envscanner.DataStructure.Bluetooth;
import com.example.brzydal.envscanner.DataStructure.JSONSerializableAndroidData;
import com.example.brzydal.envscanner.DataStructure.Wifi;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import static android.content.pm.PackageManager.*;

public class MainActivity extends Activity {

    //Switches
    private Switch wifiSwitch;
    private Switch bluetoothSwitch;

    //Buttons
    private Button wifiButton;
    private Button bluetoothButton;
    //TextViews
    private TextView wifiText;
    private TextView bluetoothText;
    private TextView locationText;
    private TextView phoneIdText;

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

        wifiText = (TextView) findViewById(R.id.numberOfWifiText);
        bluetoothText = (TextView) findViewById(R.id.numberOfBlueToothText);
        locationText = (TextView) findViewById(R.id.locationText);
        phoneIdText = (TextView) findViewById(R.id.phoneIdText);

        wifiSwitch = (Switch) findViewById(R.id.wifiSwitcher);
        bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitcher);

        wifiButton = (Button) findViewById(R.id.wifiButton);
        bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
    }

    public void EnableButton(Button button)
    {
        button.setEnabled(true);
        button.setAlpha(1);
    }

    public void DisableButton(Button button)
    {
        button.setEnabled(false);
        button.setAlpha(0.1f);
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
    public void WifiDeconfiguration(){
        Toast.makeText(getApplicationContext(), "Wifi is now disabled.",
                Toast.LENGTH_LONG).show();
        mainWifi.setWifiEnabled(false);
        mainWifi.disconnect();
        wifiScanResultList.clear();
        PrintScanningData();

        DisableButton(wifiButton);
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

    public void BluetoothDeconfiguration(){
        Toast.makeText(getApplicationContext(), "Bluetooth is now disabled.",
                Toast.LENGTH_LONG).show();

        bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.disable();
        bluetoothScanResultList.clear();
        PrintScanningData();

        DisableButton(bluetoothButton);
    }

    public void InitializeWifi()
    {
        DisableButton(wifiButton);
        //Check change listener - user click
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    WifiConfiguration();
                }
                else
                {
                    WifiDeconfiguration();
                }
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
        DisableButton(bluetoothButton);
        //Listener on check changed - user click on slider
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BluetoothConfiguration();
                }
                else {
                    BluetoothDeconfiguration();
                }
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

                if (wifiScanResultList.size() < 1)
                    DisableButton(wifiButton);
                else
                    EnableButton(wifiButton);

                if (bluetoothScanResultList.size() < 1)
                    DisableButton(bluetoothButton);
                else
                    EnableButton(bluetoothButton);

                if (wifiSwitch.isChecked())
                    mainWifi.startScan();
                if (bluetoothSwitch.isChecked())
                    bluetoothAdapter.startDiscovery();

                PrintScanningData();
                SendDataAsyncToServer();
                doScanning();
            }
        }, 12000);

    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(); return dateFormat.format(date);
    }

    protected void SendDataAsyncToServer()
    {
        if ((GPSLocalizer.latitude != 0 && GPSLocalizer.longitude != 0) && (wifiSwitch.isChecked() || bluetoothSwitch.isChecked())) {
            JSONSerializableAndroidData dataToSend = new JSONSerializableAndroidData();
            //Id and general
            dataToSend.setId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString());
            dataToSend.setAndroidAPI(String.valueOf(android.os.Build.VERSION.SDK));
            dataToSend.setNumberOfBtConnections(bluetoothScanResultList.size());
            dataToSend.setNumberOfWifiConnections(wifiScanResultList.size());
            dataToSend.setDateAndTime(getDateTime());
            dataToSend.setGPSlatitude(GPSLocalizer.latitude);
            dataToSend.setGPSLongtitude(GPSLocalizer.longitude);
            //wifi
            List<Wifi> wifiListToAdd = new ArrayList<>();
            for (ScanResult wifi : wifiScanResultList) {
                Wifi wifiToAdd = new Wifi();
                wifiToAdd.setBSSID(wifi.BSSID);
                wifiToAdd.setFrequency(wifi.frequency);
                wifiToAdd.setLevel(wifi.level);
                wifiToAdd.setSecurity(wifi.capabilities);
                wifiToAdd.setSSID(wifi.SSID);
                wifiToAdd.setTimestamp(String.valueOf(wifi.timestamp));
                wifiListToAdd.add(wifiToAdd);
            }
            dataToSend.setWifis(wifiListToAdd);

            //bluetooth
            List<Bluetooth> btListToAdd = new ArrayList<>();
            for (BluetoothDevice bluetooth : bluetoothScanResultList) {
                Bluetooth btToAdd = new Bluetooth();
                btToAdd.setDeviceName(bluetooth.getName());
                btToAdd.setMAC(bluetooth.getAddress());
                btListToAdd.add(btToAdd);
            }
            dataToSend.setBluetooths(btListToAdd);


            AsyncDataSend dataSender = new AsyncDataSend();
            dataSender.AndroidData = dataToSend;
            dataSender.execute();
        }
    }
    protected void PrintScanningData() {
        wifiText.setText(String.valueOf(wifiScanResultList.size()));
        bluetoothText.setText(String.valueOf(bluetoothScanResultList.size()));
        locationText.setText(GPSLocalizer.GetLocation());
        phoneIdText.setText(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString());
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
            if (wifiSwitch.isChecked())
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
