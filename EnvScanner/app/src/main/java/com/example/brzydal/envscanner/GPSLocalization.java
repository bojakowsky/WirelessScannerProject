package com.example.brzydal.envscanner;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;

@SuppressWarnings("ResourceType")
public class GPSLocalization implements LocationListener {

    public double latitude;
    public double longitude;

    private LocationManager locationManager;
    public GPSLocalization(Activity mainActivity)
    {
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public String GetLocation(){
        return latitude + " " + longitude;
    }

    public boolean IsGPSAvaiable()
    {
        return locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

}