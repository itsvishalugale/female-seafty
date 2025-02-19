package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OverpassApiService {
    @GET("interpreter")
    Call<OverpassResponse> getNearbyHospitals(@Query("data") String query);

    @GET("interpreter")
    Call<OverpassResponse> getNearbyPoliceStations(@Query("data") String query);
}