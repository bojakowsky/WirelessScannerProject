package com.example.brzydal.envscanner;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.widget.TextView;

public class WifiScanDetails extends Activity {

    protected ScanResult scanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            scanResult = (ScanResult) extras.get("SCAN_RESULT");

            TextView bssid = (TextView)findViewById(R.id.BSSID_Label);
            bssid.setText("BSSID: " + scanResult.BSSID);

            TextView security = (TextView)findViewById(R.id.Security_Label);
            security.setText("SECURITY: " + scanResult.capabilities);

            TextView frequency = (TextView)findViewById(R.id.Frequency_Label);
            frequency.setText("FREQ: " + scanResult.frequency);

            TextView level = (TextView)findViewById(R.id.Level_Label);
            level.setText("LEVEL: " + scanResult.level);

            TextView SSID = (TextView)findViewById(R.id.SSID_Label);
            SSID.setText("SSID: " + scanResult.SSID);

            TextView timestamp = (TextView)findViewById(R.id.Timestamp_Label);
            timestamp.setText("TIMESTAMP: " + scanResult.timestamp);

        }


    }
}
