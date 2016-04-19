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
import java.util.ArrayList;
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

        wifiButton.setEnabled(false);
        //Check change listener - user click
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    WifiConfiguration();
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

        bluetoothButton.setEnabled(false);
        //Listener on check changed - user click on slider
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BluetoothConfiguration();
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

                if(wifiScanResultList.size() < 1){
                    wifiButton.setEnabled(false);
                } else {
                    wifiButton.setEnabled(true);
                }

                if(bluetoothScanResultList.size() < 1){
                    bluetoothButton.setEnabled(false);
                } else {
                    bluetoothButton.setEnabled(true);
                }

                PrintScanningData();
                if (wifiSwitch.isChecked())
                    mainWifi.startScan();
                if (bluetoothSwitch.isChecked())
                    bluetoothAdapter.startDiscovery();
                doScanning();
            }
        }, 12000);

    }

    public void execute() {
        HttpURLConnection httpcon;
        String url = "http://localhost:18608/Scanner/CollectAndroidData";
        String data = "test123";
        String result = null;
        try{
        //Connect
            httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();

            //Write
            OutputStream os = httpcon.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.close();
            os.close();

            //Read
            BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(),"UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            result = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void PrintScanningData() {
        wifiText.setText(String.valueOf(wifiScanResultList.size()));
        bluetoothText.setText(String.valueOf(bluetoothScanResultList.size()));
        locationText.setText(GPSLocalizer.GetLocation());
        phoneIdText.setText(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString());
        execute();
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
