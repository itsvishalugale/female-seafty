package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    EditText e1_username, e2_password;
    Button b1_login, b2_signup;
    Button sendLocationButton;

    @SuppressLint({"MissingPermission", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e1_username = findViewById(R.id.emailInput);
        e2_password = findViewById(R.id.passwordInput);
        b1_login = findViewById(R.id.loginButton);
        b2_signup = findViewById(R.id.signupLink);



        b1_login.setOnClickListener(view -> {
            String username = e1_username.getText().toString();
            String password = e2_password.getText().toString();

            // Replace with secure validation logic
            if (("vishal".equals(username) && "1234".equals(password))) {
                Intent intent = new Intent(getApplicationContext(), DrawerNavigation.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Invalid credentials, please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        b2_signup.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(i);
        });






    }


}
