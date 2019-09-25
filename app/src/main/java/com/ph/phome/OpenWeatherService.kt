package com.ph.phome

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by yang on 20/12/16.
 */

interface OpenWeatherService {
    @GET("weather")
    fun getWeather(@Query("APPID") appid: String,
                   @Query("units") unit: String,
                   @Query("lat") lat: Int,
                   @Query("lon") lon: Int): Call<WeatherResponse>

//    @GET("weather")
//    fun getWeather(@Query("APPID") appid: String, @Query("units") unit: String, @Query("zip") zip: String): Call<WeatherResponse>

    @GET("weather")
    fun getWeather(@Query("APPID") appid: String, @Query("units") unit: String, @Query("q") cityNCountry: String): Call<WeatherResponse>
}
