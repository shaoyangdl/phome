package com.ph.phome


import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by yang on 20/12/16.
 */

class OpenWeatherGrabber {
    private val retrofit: Retrofit
    private val service: OpenWeatherService

    init {
        retrofit = Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor(
                        LoggingInterceptor()
                    )
                    .build()
            )
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(OpenWeatherService::class.java)
    }

    fun getCurrentWeather(callback: Callback<WeatherResponse>, cityName: String, countryCode: String) {
        Log.d(TAG, "try to start retrofit call")
        val call = service.getWeather(API_KEY, "metric", "$cityName,$countryCode")
        call.enqueue(callback)
    }

    fun getCurrentWeather(callback: Callback<WeatherResponse>, lat: Float, lon: Float) {
        Log.d(TAG, "try to start retrofit call")
        val call = service.getWeather(API_KEY, "metric", lat.toInt(),lon.toInt())
        call.enqueue(callback)
    }

    companion object {

        private val baseUrl = "http://api.openweathermap.org/data/2.5/"
        private val API_KEY = "61f806dad532bad3654c034c0b524f23"
        private val TAG = "OpenWeatherGrabber"
    }
}
