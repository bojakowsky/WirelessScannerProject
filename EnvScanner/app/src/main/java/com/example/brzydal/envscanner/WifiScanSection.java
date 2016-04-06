package com.example.brzydal.envscanner;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class WifiScanSection extends Activity {
    private ListView wifiScanResultListView;
    private ArrayList<ScanResult> wifiScanResultList = new ArrayList<>();
    private ArrayAdapter<String> wifiArrayAdapter;
    private ArrayList<String> wifiSSIDList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan_section);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            Object[] wifiScanResultListTemp = (Object[] ) extras.get("ALL_SCAN_RESULTS");
            for (Object result : wifiScanResultListTemp)
                wifiScanResultList.add((ScanResult)result);

            wifiSSIDList.clear();
            for (int i = 0 ; i < wifiScanResultList.size(); i++)
                wifiSSIDList.add(wifiScanResultList.get(i).SSID);

            wifiArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wifiSSIDList);

            wifiScanResultListView = (ListView) findViewById(R.id.wifiScanResults);
            wifiScanResultListView.setAdapter(wifiArrayAdapter);

            wifiScanResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ScanResult scanResult = wifiScanResultList.get(position);
                    Intent intent = new Intent(WifiScanSection.this, WifiScanDetails.class);
                    intent.putExtra("SCAN_RESULT", scanResult);
                    startActivity(intent);
                }
            });
        }
    }
}
