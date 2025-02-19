package com.example.myapplication;

public class HospitalModel {
    private String name;
    private String address;
    private String pincode;
    private String phone;
    private double lat;
    private double lon;

    public HospitalModel(String name, String address, String pincode, String phone, double lat, double lon) {
        this.name = name;
        this.address = address;
        this.pincode = pincode;
        this.phone = phone;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPincode() { return pincode; }
    public String getPhone() { return phone; }
    public double getLat() { return lat; }
    public double getLon() {return lon;}
}
