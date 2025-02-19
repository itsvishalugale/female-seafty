package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class EmergencyContactActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
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
        btnOpenDialog = findViewById(R.id.btnOpenDialog);
        Button btnSendLocation = findViewById(R.id.btnSendLocation);
        Button btnEmergencyCall = findViewById(R.id.btnEmergencyCall);

        databaseReference = FirebaseDatabase.getInstance().getReference("EmergencyContacts");
        adapter = new RecyclerContactAdapter(this, arrContact, databaseReference);
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

        loadContactsFromFirebase();
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
                String id = databaseReference.push().getKey();
                ContactModel contact = new ContactModel(id, name, number);

                databaseReference.child(id).setValue(contact)
                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Contact saved successfully!"))
                        .addOnFailureListener(e -> Log.e("Firebase", "Failed to save contact", e));

                dialog.dismiss();
            } else {
                Toast.makeText(EmergencyContactActivity.this, "Please enter a contact name and number", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void loadContactsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrContact.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ContactModel contact = data.getValue(ContactModel.class);
                    arrContact.add(contact);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to retrieve contacts", error.toException());
            }
   });
}
}
