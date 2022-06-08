package com.example.m8recuuf2.settings;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DefaultStart {
    public static final String appid = "835babac7f4d7e37f8f51a1abac4fe63";
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
