package com.example.myapplication;

public class WeatherForecastModel {
    private String date;
    private double temperature;

    public WeatherForecastModel(String date, double temperature) {
        this.date = date;
        this.temperature = temperature;
    }

    public String getDate() {
        return date;
    }

    public double getTemperature() {
        return temperature;
    }
}