package com.example.myapplication;

import com.google.android.gms.maps.model.LatLng;

public class HospitalModel {
    private String name;
    private LatLng location;

    public HospitalModel(String name, LatLng location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
}
}