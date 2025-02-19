package com.example.myapplication;

public class PoliceStationModel {
    private String name;
    private String address;
    private String pincode;
    private String phone;
    private double latitude;
    private double longitude;

    public PoliceStationModel(String name, String address, String pincode, String phone, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.pincode = pincode;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPincode() { return pincode; }
    public String getPhone() { return phone; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude;}
}
