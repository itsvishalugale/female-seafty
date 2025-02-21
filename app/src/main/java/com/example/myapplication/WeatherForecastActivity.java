package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WeatherForecastActivity extends AppCompatActivity {

    private static final String API_KEY = "86d0beef3d54557db4fda06022d489e6";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private TextView tvLocation, tvDateTime, tvTemperature, tvCondition, tvPrecipitation, tvHumidity, tvWindSpeed;
    private ImageView ivWeatherIcon;
    private RecyclerView recyclerViewForecast;
    private WeatherForecastAdapter forecastAdapter;
    private ArrayList<WeatherForecastModel> forecastList;

    private LocationManager locationManager;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        // Initialize UI elements
        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvCondition = findViewById(R.id.tvCondition);
        tvPrecipitation = findViewById(R.id.tvPrecipitation);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        recyclerViewForecast = findViewById(R.id.recyclerViewForecast);

        // Set up RecyclerView
        forecastList = new ArrayList<>();
        forecastAdapter = new WeatherForecastAdapter(this, forecastList);

        recyclerViewForecast.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewForecast.setAdapter(forecastAdapter);

        // Initialize location and request queue
        requestQueue = Volley.newRequestQueue(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                fetchWeatherData(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void fetchWeatherData(double latitude, double longitude) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject city = response.getJSONObject("city");
                            String cityName = city.getString("name");
                            tvLocation.setText(cityName);

                            JSONArray forecastArray = response.getJSONArray("list");
                            forecastList.clear(); // Clear previous data

                            for (int i = 0; i < 7; i++) { // Get 7-day forecast
                                JSONObject forecast = forecastArray.getJSONObject(i);
                                String date = forecast.getString("dt_txt");
                                double futureTemp = forecast.getJSONObject("main").getDouble("temp");

                                forecastList.add(new WeatherForecastModel(date, futureTemp));
                            }

                            forecastAdapter.notifyDataSetChanged(); // Update RecyclerView

                            // Get current weather details
                            JSONObject firstForecast = forecastArray.getJSONObject(0);
                            JSONObject main = firstForecast.getJSONObject("main");
                            double temp = main.getDouble("temp");
                            int humidity = main.getInt("humidity");
                            JSONObject wind = firstForecast.getJSONObject("wind");
                            double windSpeed = wind.getDouble("speed");
                            String weatherCondition = firstForecast.getJSONArray("weather").getJSONObject(0).getString("description");
                            int weatherId = firstForecast.getJSONArray("weather").getJSONObject(0).getInt("id");

                            tvTemperature.setText(temp + "°C");
                            tvCondition.setText(weatherCondition);
                            tvHumidity.setText(humidity + "%\nHumidity");
                            tvWindSpeed.setText(windSpeed + " km/h\nWind Speed");

                            tvDateTime.setText(new SimpleDateFormat("EEEE, d MMMM HH:mm", Locale.getDefault()).format(new Date()));

                            setWeatherIcon(weatherId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WeatherForecastActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WeatherForecastActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }

    private void setWeatherIcon(int weatherId) {
        if (weatherId >= 200 && weatherId < 300) {
            ivWeatherIcon.setImageResource(R.drawable.ic_storm);
        } else if (weatherId >= 300 && weatherId < 600) {
            ivWeatherIcon.setImageResource(R.drawable.ic_rain);
        } else if (weatherId >= 600 && weatherId < 700) {
            ivWeatherIcon.setImageResource(R.drawable.ic_snow);
        } else if (weatherId >= 700 && weatherId < 800) {
            ivWeatherIcon.setImageResource(R.drawable.ic_fog);
        } else if (weatherId == 800) {
            ivWeatherIcon.setImageResource(R.drawable.ic_sunny);
        } else {
            ivWeatherIcon.setImageResource(R.drawable.ic_cloudy);
        }
    }
}
