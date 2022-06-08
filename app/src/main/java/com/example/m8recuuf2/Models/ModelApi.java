package com.example.m8recuuf2.Models;

import java.util.ArrayList;

public class ModelApi {
    public ArrayList<WeatherModel> weather;
    public MainModel main;
    public CoordModel coord;
    public String name;

    public ArrayList<WeatherModel> getWeather() {
        return weather;
    }

    public String getName() {
        return name;
    }

    public MainModel getMain() {
        return main;
    }

    public CoordModel getCoord() {
        return coord;
    }

}
