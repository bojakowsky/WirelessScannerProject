package com.example.brzydal.envscanner.DataStructure;

import java.sql.Date;
import java.util.List;

/**
 * Created by BRZYDAL on 4/20/2016.
 */
public class JSONSerializableAndroidData {
    public String getAndroidAPI() {
        return AndroidAPI;
    }

    public void setAndroidAPI(String androidAPI) {
        AndroidAPI = androidAPI;
    }

    public int getNumberOfWifiConnections() {
        return NumberOfWifiConnections;
    }

    public void setNumberOfWifiConnections(int numberOfWifiConnections) {
        NumberOfWifiConnections = numberOfWifiConnections;
    }

    public int getNumberOfBtConnections() {
        return NumberOfBtConnections;
    }

    public void setNumberOfBtConnections(int numberOfBtConnections) {
        NumberOfBtConnections = numberOfBtConnections;
    }

    public double getGPSlatitude() {
        return GPSlatitude;
    }

    public void setGPSlatitude(double GPSlatitude) {
        this.GPSlatitude = GPSlatitude;
    }

    public double getGPSLongtitude() {
        return GPSLongtitude;
    }

    public void setGPSLongtitude(double GPSLongtitude) {
        this.GPSLongtitude = GPSLongtitude;
    }

    public String getDateAndTime() {
        return DateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        DateAndTime = dateAndTime;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    //Device
    private String Id;

    //General
    private String AndroidAPI;
    private int NumberOfWifiConnections;
    private int NumberOfBtConnections;
    private double GPSlatitude;
    private double GPSLongtitude;
    private String DateAndTime;



    //Wifi
    private List<Wifi> Wifis;

    //Bluetooth
    private List<Bluetooth> Bluetooths;

    public List<Wifi> getWifis() {
        return Wifis;
    }

    public void setWifis(List<Wifi> wifis) {
        Wifis = wifis;
    }

    public List<Bluetooth> getBluetooths() {
        return Bluetooths;
    }

    public void setBluetooths(List<Bluetooth> bluetooths) {
        Bluetooths = bluetooths;
    }
}
