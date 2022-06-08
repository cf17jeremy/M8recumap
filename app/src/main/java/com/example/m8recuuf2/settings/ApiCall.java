package com.example.m8recuuf2.settings;

import com.example.m8recuuf2.Models.ModelApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiCall {
    @GET("data/2.5/weather")
    Call<ModelApi> getCity(@Query("q") String city, @Query("appid") String apiid, @Query("lang") String lang);
    @GET("data/2.5/weather")
    Call<ModelApi> getLoc(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String apiid, @Query("lang") String lang);
}
