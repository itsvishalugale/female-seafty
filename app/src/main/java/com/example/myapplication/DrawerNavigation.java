package com.example.myapplication;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;

public class DrawerNavigation extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton img_btn;
    EditText et_call;
    Button bt_police,bt_callnumber,bt_parent,bt_send_msg,sendLocationButton;
    Button saveContact;
    NavigationView navigationView;
    EditText contactName,et_number;
    LinearLayout llSavedContacts;

    TextView tvSavedContactsTitle;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_navigation);
        drawerLayout=findViewById(R.id.drawer_layout);
        img_btn=findViewById(R.id.img);
        navigationView=findViewById(R.id.navigationView);

        et_call=findViewById(R.id.et_number);
        contactName=findViewById(R.id.contactName);
        llSavedContacts = findViewById(R.id.ll_saved_contacts);
        et_number=findViewById(R.id.et_number);
        bt_callnumber=findViewById(R.id.call_number);
        bt_parent=findViewById(R.id.btn_emr_parent);
        bt_police=findViewById(R.id.btn_emr_police);
        bt_send_msg=findViewById(R.id.btn_send_msg);
        saveContact=findViewById(R.id.savecontact);
        tvSavedContactsTitle = findViewById(R.id.tv_saved_contacts_title);



        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);


        saveContact.setOnClickListener(v -> saveContact());

        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });
        bt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:8459409264"));
                startActivity(i);

            }
        });
        bt_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:8459409264"));
                startActivity(i);

            }
        });
        bt_callnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = String.valueOf(et_call.getText());
                long n1=Long.parseLong(number);




                Intent i=new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+number+""));
                startActivity(i);

            }
        });
        bt_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage("8459409264",null,"i'm in denger plz help",null,null);

            }
        });

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, location -> {
////                    if (location != null) {

//                        shareLocation(latitude, longitude);
//                    } else {
//                        Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
//                    }
//                });



        sendLocationButton = findViewById(R.id.btn_sendLocation);
        sendLocationButton.setOnClickListener(v -> {

            // Fetch and share location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
//                        double latitude = location.getLatitude();
//                        double longitude = location.getLongitude();
                        if (location != null) {
                            shareLocation(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

        if (id == R.id.navContact) {
            // Navigate to FirstActivity
            Toast.makeText(getApplicationContext(), "you click on contact", Toast.LENGTH_SHORT).show();
//            Intent i=new Intent(getApplicationContext(), SavedContactlayout.class);
//            startActivity(i);

        } else if (id == R.id.navContactSaved) {
            // Navigate to SecondActivity
            Toast.makeText(getApplicationContext(), "you click on contact saved", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(getApplicationContext(), SavedContactlayout.class);
            startActivity(i);

        } else if (id == R.id.navfeedback) {
            // Navigate to ThirdActivity
            Toast.makeText(getApplicationContext(), "you click on Feedback", Toast.LENGTH_SHORT).show();

        }



                drawerLayout.close();
                return false;
            }
        });



    }



    private void saveContact() {
        String name = contactName.getText().toString().trim();
        String number = et_number.getText().toString().trim();

        if (name.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Please enter both name and number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate saved_contact_layout
        View contactView = LayoutInflater.from(this).inflate(R.layout.saved_contact_layout, llSavedContacts, false);

        TextView tvContactName = contactView.findViewById(R.id.tv_contact_name);
        TextView tvContactNumber = contactView.findViewById(R.id.tv_contact_number);
        Button btnCall = contactView.findViewById(R.id.btn_call);

        tvContactName.setText(name);
        tvContactNumber.setText(number);

        btnCall.setOnClickListener(v -> makeCall(number));

        // Add the new contact to the layout
        llSavedContacts.addView(contactView);

        // Show the "Saved Contacts" title
        tvSavedContactsTitle.setVisibility(View.VISIBLE);

        // Clear input fields
        contactName.setText("");
        et_number.setText("");
    }

    private void makeCall(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu
//        getMenuInflater().inflate(R.menu.drawer_items, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle menu item clicks
//        int id = item.getItemId();
//
//        if (id == R.id.navContact) {
//            // Navigate to FirstActivity
//            Toast.makeText(getApplicationContext(), "you click on contact", Toast.LENGTH_SHORT).show();
//            Intent i=new Intent(getApplicationContext(), SavedContactlayout.class);
//            startActivity(i);
//            return true;
//        } else if (id == R.id.navContactSaved) {
//            // Navigate to SecondActivity
//            Toast.makeText(getApplicationContext(), "you click on contact saved", Toast.LENGTH_SHORT).show();
//            return true;
//        } else if (id == R.id.navfeedback) {
//            // Navigate to ThirdActivity
//            Toast.makeText(getApplicationContext(), "you click on Feedback", Toast.LENGTH_SHORT).show();
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private void shareLocation(double latitude, double longitude) {
        String locationUrl = "https://www.google.com/maps?q=" + latitude + "," + longitude;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "My Location: " + locationUrl);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share Location Via");
        startActivity(shareIntent);
    }




}


