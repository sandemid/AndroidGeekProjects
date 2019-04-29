package com.example.androidgeekproject.rest;

import com.example.androidgeekproject.rest.entites.WeatherRequestRestModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherCoordinates {
    @GET("data/2.5/weather")
    Call<WeatherRequestRestModel> loadWeather(@Query("lat") String latitude,
                                              @Query("lon") String longitude,
                                              @Query("appid") String keyApi,
                                              @Query("units") String units);
}
