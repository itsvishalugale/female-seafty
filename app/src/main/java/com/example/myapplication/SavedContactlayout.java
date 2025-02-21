package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SavedContactlayout extends Activity {

    LinearLayout llSavedContacts;

    TextView contactname,contactnumber;
    MediaPlayer mediaPlayer;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_contact_layout); // Use saved_contact_layout.xml

        llSavedContacts = findViewById(R.id.ll_saved_contacts);
//        contactname=findViewById(R.id.tv_contact_name);
//        contactnumber=findViewById(R.id.tv_contact_number);

        // Display saved contacts
        displaySavedContacts();



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
    }

    private void displaySavedContacts() {
        // Retrieve saved contacts from SharedPreferences or a database
//        Intent intent = getIntent();
//        String receivedMessage = intent.getStringExtra("name");
//        String receivedMobileNumber = intent.getStringExtra("number");
//
//        contactname.setText(receivedMessage);;
//        contactnumber.setText(receivedMobileNumber);
        // For now, we assume the contacts are already added to llSavedContacts in DrawerNavigation.java

//        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        String mobileNumber = sharedPreferences.getString("mobile_number", "No Number Found");
//        String contactName=sharedPreferences.getString("contact_name","No name found");
//        contactname.setText(contactName);;
//        contactnumber.setText(mobileNumber);

//
//        View contactView = LayoutInflater.from(this).inflate(R.layout.saved_contact_layout, llSavedContacts, false);
//
//        TextView tvContactName = contactView.findViewById(R.id.tv_contact_name);
//        TextView tvContactNumber = contactView.findViewById(R.id.tv_contact_number);
//        Button btnCall = contactView.findViewById(R.id.btn_call);
//
//        tvContactName.setText(contactName);
//        tvContactNumber.setText(mobileNumber);
//
//


    }


    public void openMedicalStock(View view) {
        Intent intent = new Intent(this, NearbyHospitalList.class);
        startActivity(intent);
}



    public void openEmergencyContacts(View view) {
        Intent i=new Intent(getApplicationContext(), EmergencyContact.class);
        startActivity(i);

    }
    public void openPoliceStation(View view) {
        Intent i=new Intent(getApplicationContext(), NearbyPoliceList.class);
        startActivity(i);

    }

    public void openSelfDefence(View view){
        Intent intent = new Intent(getApplicationContext(), SelfDefenceActivity.class);
        startActivity(intent);

    }

    public void openChatBot(View view) {
        Intent intent = new Intent(getApplicationContext(), MetaAiActivity.class);
        startActivity(intent);
    }
    public void openWhether(View view) {
        Intent intent = new Intent(getApplicationContext(), WeatherForecastActivity.class);
        startActivity(intent);
    }




}