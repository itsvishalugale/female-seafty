package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

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