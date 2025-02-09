package com.example.myapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EmergencyContactActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private FloatingActionButton btnOpenDialog;
    private RecyclerContactAdapter adapter;
    private ArrayList<ContactModel> arrContact = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergancy_contact);

        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.RvContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button btnSendLocation = findViewById(R.id.btnSendLocation);
        Button btnEmergencyCall = findViewById(R.id.btnEmergencyCall);
        btnOpenDialog = findViewById(R.id.btnOpenDialog);

        sharedPreferences = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
        loadContactsFromPreferences(); // Load saved contacts

        adapter = new RecyclerContactAdapter(this, arrContact);
        recyclerView.setAdapter(adapter);

        btnOpenDialog.setOnClickListener(view -> openAddContactDialog());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void openAddContactDialog() {
        Dialog dialog = new Dialog(EmergencyContactActivity.this);
        dialog.setContentView(R.layout.fragment_add_contact_dialog);

        EditText edtName = dialog.findViewById(R.id.edtName);
        EditText edtNumber = dialog.findViewById(R.id.edtNumber);
        Button btnAction = dialog.findViewById(R.id.btnAction);

        btnAction.setOnClickListener(view -> {
            String name = edtName.getText().toString();
            String number = edtNumber.getText().toString();

            if (!name.isEmpty() && !number.isEmpty()) {
                arrContact.add(new ContactModel(R.drawable.person_logo, name, number));
                adapter.notifyItemInserted(arrContact.size() - 1);
                recyclerView.scrollToPosition(arrContact.size() - 1);
                saveContactsToPreferences(); // Save the new contact
                dialog.dismiss();
            } else {
                Toast.makeText(EmergencyContactActivity.this, "Please enter a contact name and number", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void loadContactsFromPreferences() {
        Set<String> contactSet = sharedPreferences.getStringSet("contacts", new HashSet<>());
        arrContact.clear();
        for (String contact : contactSet) {
            String[] parts = contact.split("\\|");
            if (parts.length == 2) {
                arrContact.add(new ContactModel(R.drawable.person_logo, parts[0], parts[1]));
            }
        }
    }

    public void saveContactsToPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> contactSet = new HashSet<>();
        for (ContactModel contact : arrContact) {
            contactSet.add(contact.name + "|" + contact.number);
        }
        editor.putStringSet("contacts", contactSet);
        editor.apply();
    }
}
