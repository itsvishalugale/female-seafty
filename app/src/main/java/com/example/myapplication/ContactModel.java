package com.example.myapplication;

public class ContactModel {
    private String id;
    private String name;
    private String number;

    public ContactModel() {
        // Empty constructor required for Firebase
    }

    public ContactModel(String id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getNumber() { return number;}
}