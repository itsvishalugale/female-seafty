package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DrawerNavigation extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton img_btn;
    EditText contactName, et_number;
    Button bt_police, bt_callnumber, bt_parent, saveContact, sendMessageButton, sendLocationButton;
    LinearLayout llSavedContacts;
    TextView tvSavedContactsTitle;
    Gson gson;
    SharedPreferences sharedPreferences;
    List<Contact> contactList;
    private static final String PREF_NAME = "ContactsPrefs";
    private static final String CONTACTS_KEY = "SavedContacts";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private MediaPlayer mediaPlayer;

    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_navigation); // Use your layout XML

        drawerLayout = findViewById(R.id.drawer_layout);
        img_btn = findViewById(R.id.img);
        contactName = findViewById(R.id.contactName);
        et_number = findViewById(R.id.et_number);
        llSavedContacts = findViewById(R.id.ll_saved_contacts);
        saveContact = findViewById(R.id.savecontact);
        sendMessageButton = findViewById(R.id.btn_send_msg);
        sendLocationButton = findViewById(R.id.btn_sendLocation);
        tvSavedContactsTitle = findViewById(R.id.tv_saved_contacts_title);

        bt_police=findViewById(R.id.btn_emr_police);
        bt_callnumber=findViewById(R.id.call_number);
        bt_parent=findViewById(R.id.btn_emr_parent);

        bt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:8459409264"));
                startActivity(callIntent);

            }
        });

        bt_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:8459409264"));
                startActivity(callIntent);

            }
        });

        bt_callnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String n=et_number.getText().toString().trim();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+n));
                startActivity(callIntent);

            }
        });


        Button sirenButton = findViewById(R.id.btn_siren);
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound);

        sirenButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                } else {
                    mediaPlayer.start();
                }
            }
        });





        // Initialize shared preferences and Gson
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        gson = new Gson();
        contactList = loadContacts(); // Load existing contacts

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Button Click Listeners
        saveContact.setOnClickListener(v -> saveContact());
        sendMessageButton.setOnClickListener(v -> sendMessage());
        sendLocationButton.setOnClickListener(v -> sendLocation());

        // Load the saved contacts
        displayContacts();
    }

    private void saveContact() {
        String name = contactName.getText().toString().trim();
        String number = et_number.getText().toString().trim();

        if (name.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Please enter both name and number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new contact object
        Contact newContact = new Contact(name, number);
        contactList.add(newContact);

        // Save updated contact list to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(contactList);
        editor.putString(CONTACTS_KEY, json);
        editor.apply();

        // Update the UI with the new contact
        displayContacts();

        // Clear input fields
        contactName.setText("");
        et_number.setText("");
    }

    private List<Contact> loadContacts() {
        String json = sharedPreferences.getString(CONTACTS_KEY, null);
        if (json != null) {
            try {
                Type contactListType = new TypeToken<List<Contact>>(){}.getType();
                return gson.fromJson(json, contactListType);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("DrawerNavigation", "Error loading contacts", e);
                return new ArrayList<>(); // If there's an error, return an empty list
            }
        } else {
            return new ArrayList<>(); // If no contacts are saved, return an empty list
        }
    }

    private void displayContacts() {
        llSavedContacts.removeAllViews(); // Clear existing views

        // Display saved contacts from SharedPreferences
        for (Contact contact : contactList) {
            View contactView = LayoutInflater.from(this).inflate(R.layout.contact_item_layout, llSavedContacts, false);

            TextView tvContactName = contactView.findViewById(R.id.tv_contact_name);
            TextView tvContactNumber = contactView.findViewById(R.id.tv_contact_number);
            Button btnCall = contactView.findViewById(R.id.btn_call);
            Button btnEdit = contactView.findViewById(R.id.btn_edit);
            Button btnDelete = contactView.findViewById(R.id.btn_delete);
            Button btnSendMsg = contactView.findViewById(R.id.btn_send_msg);
            Button btnSendLocation = contactView.findViewById(R.id.btn_send_location);

            tvContactName.setText(contact.getName());
            tvContactNumber.setText(contact.getNumber());

            // Call button listener
            btnCall.setOnClickListener(v -> makeCall(contact.getNumber()));

            // Edit button listener
            btnEdit.setOnClickListener(v -> {
                contactName.setText(contact.getName());
                et_number.setText(contact.getNumber());
                contactList.remove(contact);
                displayContacts();
            });

            // Delete button listener
            btnDelete.setOnClickListener(v -> {
                contactList.remove(contact);
                saveContacts(); // Save the updated list after removal
                displayContacts();
            });

            // Send Message button listener
            btnSendMsg.setOnClickListener(v -> sendMessageToContact(contact.getNumber()));

            // Send Location button listener
            btnSendLocation.setOnClickListener(v -> sendLocationToContact(contact.getNumber()));

            // Add the new contact to the layout
            llSavedContacts.addView(contactView);
        }

        tvSavedContactsTitle.setVisibility(contactList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void makeCall(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
    }

    private void sendMessageToContact(String number) {
        String message = "Emergency Message";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
        Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show();
    }

    private void sendLocationToContact(String number) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            String locationMessage = "Current Location: " + location.getLatitude() + ", " + location.getLongitude();
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(number, null, locationMessage, null, null);
                            Toast.makeText(this, "Location Sent!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveContacts() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(contactList);
        editor.putString(CONTACTS_KEY, json);
        editor.apply();
    }

    // Contact class for the contact model
    public class Contact {
        private String name;
        private String number;

        public Contact(String name, String number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }

    private void sendMessage() {
        String number = et_number.getText().toString().trim();
        if (number.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = "Emergency Message";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
        Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show();
    }

    private void sendLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            String number = et_number.getText().toString().trim();
                            if (number.isEmpty()) {
                                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String locationMessage = "Current Location: " + location.getLatitude() + ", " + location.getLongitude();
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(number, null, locationMessage, null, null);
                            Toast.makeText(this, "Location Sent!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
