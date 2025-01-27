package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SavedContactlayout extends Activity {

    ListView listView;
    ArrayList<String> numbersList;
    Button save;
    EditText et_number,contactName;

    ArrayAdapter<String> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_contact_layout);
//        listView=findViewById(R.id.listSavedContact);
        save=findViewById(R.id.savecontact);
        et_number=findViewById(R.id.et_number);
        contactName=findViewById(R.id.contactName);


//save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = contactName.getText().toString().trim();
//                String phone = et_number.getText().toString().trim();
//
//                if (!name.isEmpty() && !phone.isEmpty()) {
//                    saveContact(name, phone);
//                } else {
//                    Toast.makeText(SavedContactlayout.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void saveContact(String name, String phone) {
//        // Create a list to hold the contact data
//        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
//
//        // Add name
//        operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
//                .build());
//
//        // Add display name
//        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
//                .build());
//
//        // Add phone number
//        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
//                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
//                .build());
//
//        try {
//            // Apply the operations
//            getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
//            Toast.makeText(this, "Contact saved successfully!", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to save contact", Toast.LENGTH_SHORT).show();
//        }

//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                numbersList = new ArrayList<>();
//
//
//                adapter = new ArrayAdapter<>(SavedContactlayout.this, android.R.layout.simple_list_item_1, numbersList);
//                listView.setAdapter(adapter);
//
//                String number = et_number.getText().toString().trim();
//
//
//                if (!number.isEmpty()) {
//
//                    numbersList.add(number);
//                    adapter.notifyDataSetChanged();
//
//                    et_number.setText("");
//                }
//
//            }
//        });




    }
}
