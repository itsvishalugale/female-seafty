package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyHospitalList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HospitalAdapter adapter;
    private ArrayList<HospitalModel> hospitalList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String BASE_URL = "https://overpass-api.de/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_hospital_list_layout);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HospitalAdapter(hospitalList, this);
        recyclerView.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                fetchHospitals(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchHospitals(double userLat, double userLon) {
        String query = "[out:json];node[amenity=hospital](around:5000," + userLat + "," + userLon + ");out;";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OverpassApiService apiService = retrofit.create(OverpassApiService.class);
        apiService.getNearbyHospitals(query).enqueue(new Callback<OverpassResponse>() {
            @Override
            public void onResponse(Call<OverpassResponse> call, Response<OverpassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OverpassResponse.Element> elements = response.body().getElements();
                    hospitalList.clear();

                    for (OverpassResponse.Element element : elements) {
                        double hospitalLat = element.getLat();
                        double hospitalLon = element.getLon();
                        double distance = calculateDistance(userLat, userLon, hospitalLat, hospitalLon);

                        if (distance <= 5.0) {
                            Log.d("API_RESPONSE", "Tags: " + element.getTags().toString());
                            String name = element.getTags().getOrDefault("name", "Unknown Hospital");
                            String address = element.getTags().getOrDefault("addr:full",
                                    element.getTags().getOrDefault("addr:street", "No address available"));

                            String pincode = element.getTags().getOrDefault("addr:postcode",
                                    element.getTags().getOrDefault("postcode", "N/A"));

                            String phone = element.getTags().getOrDefault("contact:phone",
                                    element.getTags().getOrDefault("phone","N/A"));

                            hospitalList.add(new HospitalModel(name, address, pincode, phone, hospitalLat, hospitalLon));
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<OverpassResponse> call, Throwable t) {
                Toast.makeText(NearbyHospitalList.this, "API request failed!", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
}
}
