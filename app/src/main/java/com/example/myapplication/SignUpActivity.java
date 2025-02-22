package com.example.myapplication;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputEditText nameField, mobileField, ageField, locationField, passwordField;
    private AutoCompleteTextView genderField;
    private CheckBox cbShowPassword;
    private MaterialButton signupButton, btnLogin;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameField = findViewById(R.id.nameField);
        mobileField = findViewById(R.id.mobileField);
        ageField = findViewById(R.id.ageField);
        locationField = findViewById(R.id.locationField);
        passwordField = findViewById(R.id.passwordField);
        genderField = findViewById(R.id.genderField);
        cbShowPassword = findViewById(R.id.cbShowPassword);
        signupButton = findViewById(R.id.signupButton);
        btnLogin = findViewById(R.id.btnLogin);

        // Setup Gender Dropdown
        String[] genderOptions = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderField.setAdapter(adapter);

        // Show/Hide Password
        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordField.setSelection(passwordField.getText().length()); // Move cursor to the end
        });

        // Sign Up Button Click
        signupButton.setOnClickListener(v -> registerUser());

        // Redirect to Login
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = nameField.getText().toString().trim();
        String mobile = mobileField.getText().toString().trim();
        String age = ageField.getText().toString().trim();
        String gender = genderField.getText().toString().trim();
        String location = locationField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (name.isEmpty() || mobile.isEmpty() || age.isEmpty() || gender.isEmpty() || location.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Authentication (Using Mobile as Email)
        mAuth.createUserWithEmailAndPassword(mobile + "@example.com", password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user.getUid(), name, mobile, age, gender, location);
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Intent i=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(i);
    }

    private void saveUserData(String userId, String name, String mobile, String age, String gender, String location) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("mobile", mobile);
        user.put("age", age);
        user.put("gender", gender);
        user.put("location", location);

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUpActivity.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUpActivity.this, "Error saving user data", Toast.LENGTH_SHORT).show();
                });
    }
}
