package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmergencyContact extends AppCompatActivity implements ContactAdapter.ContactActionListener {

    private RecyclerView contactsRecyclerView;
    private ContactAdapter adapter;
    private List<Contact> contacts = new ArrayList<>();
    private SearchView searchView;
    private MaterialButton sendLocationButton;
    private MaterialButton emergencyCallButton;
    private FloatingActionButton addContactFab;

    private DatabaseReference databaseRef; // Updated to your Firebase structure
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        // Initialize Firebase reference (using your structure)
        databaseRef = FirebaseDatabase.getInstance().getReference("EmergencyContacts");

        // Initialize views
        addContactFab = findViewById(R.id.addContactFab);
        sendLocationButton = findViewById(R.id.sendLocationButton);
        emergencyCallButton = findViewById(R.id.emergencyCallButton);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up click listeners
        addContactFab.setOnClickListener(v -> showContactDialog(null));
        sendLocationButton.setOnClickListener(v -> sendCurrentLocationToContacts());

        // Emergency call button logic
        emergencyCallButton.setOnClickListener(v -> {
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        showToast("No emergency contacts found!");
                        return;
                    }

                    boolean foundHighPriority = false;
                    String highPriorityPhoneNumber = null;

                    for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                        String phone = contactSnapshot.child("phone").getValue(String.class);
                        Boolean highPriority = contactSnapshot.child("highPriority").getValue(Boolean.class);

                        if (highPriority != null && highPriority && phone != null) {
                            highPriorityPhoneNumber = phone;
                            foundHighPriority = true;
                            break;
                        }
                    }

                    if (foundHighPriority && highPriorityPhoneNumber != null) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + highPriorityPhoneNumber));
                        if (ActivityCompat.checkSelfPermission(EmergencyContact.this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(intent);
                        } else {
                            ActivityCompat.requestPermissions(EmergencyContact.this, new String[]{android.Manifest.permission.CALL_PHONE}, 2);
                        }
                    } else {
                        showToast("No high-priority contacts found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to retrieve contacts: " + error.getMessage());
                }
            });
        });

        // Initialize RecyclerView and load contacts
        initializeViews();
        setupRecyclerView();
        loadContacts();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        searchView = findViewById(R.id.searchView);
        sendLocationButton = findViewById(R.id.sendLocationButton);
        emergencyCallButton = findViewById(R.id.emergencyCallButton);
        addContactFab = findViewById(R.id.addContactFab);
    }

    private void setupRecyclerView() {
        adapter = new ContactAdapter(contacts, this);
        contactsRecyclerView.setAdapter(adapter);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showContactDialog(Contact contact) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact_form, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextInputEditText nameInput = dialogView.findViewById(R.id.nameInput);
        TextInputEditText phoneInput = dialogView.findViewById(R.id.phoneInput);
        AutoCompleteTextView categoryInput = dialogView.findViewById(R.id.categoryInput);
        AutoCompleteTextView priorityInput = dialogView.findViewById(R.id.priorityInput);

        String[] categories = {"Family", "Friend", "Medical", "Other"};
        String[] priorities = {"High Priority", "Medium Priority", "Low Priority"};

        categoryInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories));
        priorityInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorities));

        if (contact != null) {
            nameInput.setText(contact.getName());
            phoneInput.setText(contact.getPhone());
            categoryInput.setText(contact.getCategory());
            priorityInput.setText(getPriorityText(contact.getPriority()));
        }

        builder.setView(dialogView)
                .setTitle(contact == null ? "Add Contact" : "Edit Contact")
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString();
                    String phone = phoneInput.getText().toString();
                    String category = categoryInput.getText().toString();
                    int priority = getPriorityValue(priorityInput.getText().toString());

                    if (validateInput(name, phone)) {
                        if (contact == null) {
                            addContact(name, phone, category, priority);
                        } else {
                            updateContact(contact, name, phone, category, priority);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addContact(String name, String phone, String category, int priority) {
        String id = databaseRef.push().getKey();
        Contact newContact = new Contact(id, name, phone, category, priority);

        if (id != null) {
            databaseRef.child(id).setValue(newContact)
                    .addOnSuccessListener(aVoid -> {
                        showToast("Contact added successfully");
                        loadContacts();
                    })
                    .addOnFailureListener(e -> showToast("Failed to add contact"));
        }
    }

    private void updateContact(Contact contact, String name, String phone, String category, int priority) {
        contact.setName(name);
        contact.setPhone(phone);
        contact.setCategory(category);
        contact.setPriority(priority);

        databaseRef.child(contact.getId()).setValue(contact)
                .addOnSuccessListener(aVoid -> showToast("Contact updated successfully"))
                .addOnFailureListener(e -> showToast("Failed to update contact"));
    }

    private void loadContacts() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contacts.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    Contact contact = contactSnapshot.getValue(Contact.class);
                    if (contact != null) {
                        contacts.add(contact);
                    }
                }
                adapter.updateContacts(contacts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load contacts: " + error.getMessage());
                showToast("Failed to load contacts");
            }
        });
    }

    private void sendCurrentLocationToContacts() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.SEND_SMS}, 1);
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showToast("Location is disabled. Please enable location.");
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }

        if (!isInternetAvailable()) {
            showToast("No internet connection. Please enable it.");
            return;
        }

        showToast("Getting current location...");

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        sendLocationToContacts(latitude, longitude);
                    } else {
                        showToast("Failed to get current location. Try enabling precise location.");
                    }
                });
    }

    private void sendLocationToContacts(String latitude, String longitude) {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    showToast("No emergency contacts found!");
                    return;
                }

                SmsManager smsManager = SmsManager.getDefault();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    String phoneNumber = contactSnapshot.child("phone").getValue(String.class);
                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
                        String message = "Emergency! My current location: https://www.google.com/maps?q=" + latitude + "," + longitude;
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    }
                }

                showToast("Current location sent to emergency contacts.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to retrieve contacts: " + error.getMessage());
            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private String getPriorityText(int priority) {
        switch (priority) {
            case 1: return "High Priority";
            case 2: return "Medium Priority";
            case 3: return "Low Priority";
            default: return "Medium Priority";
        }
    }

    private int getPriorityValue(String priorityText) {
        switch (priorityText) {
            case "High Priority": return 1;
            case "Medium Priority": return 2;
            case "Low Priority": return 3;
            default: return 2;
        }
    }

    private boolean validateInput(String name, String phone) {
        if (name.isEmpty() || phone.isEmpty()) {
            showToast("Name and phone are required");
            return false;
        }
        return true;
    }

    @Override
    public void onCallClick(Contact contact) {
        if (contact == null || contact.getPhone() == null || contact.getPhone().isEmpty()) {
            Toast.makeText(this, "Invalid Contact Number", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = contact.getPhone();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
            return;
        }

        startActivity(callIntent);
    }

    @Override
    public void onCallClick(Context context, Contact contact) {

    }

    @Override
    public void onEditClick(Contact contact) {
        showContactDialog(contact);
    }

    @Override
    public void onDeleteClick(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    databaseRef.child(contact.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> showToast("Contact deleted successfully"))
                            .addOnFailureListener(e -> showToast("Failed to delete contact"));
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}