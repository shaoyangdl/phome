package com.ph.phome

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

class WeatherViewModel : ViewModel() {

    val cityLiveData = MutableLiveData<String>()
    val tempLiveData = MutableLiveData<String>()
    val iconLiveData = MutableLiveData<String>()

    val geoInfoLiveData = MutableLiveData<GEOInfo>()


    val weatherGrabber = OpenWeatherGrabber()
    val extIPGrabber = ExternalIPGrabber()
    private val TAG = "WeatherViewModel"
    private val OWM_D_PREFIX = "wi_owm_day_"
    private val OWM_N_PREFIX = "wi_owm_night_"

    var timer : Timer? = null


    fun grabIp(geoip: GEOIP?, context: Context){
        if(geoip == null) {
            extIPGrabber.getIP(object : Callback<GEOIP> {
                override fun onResponse(call: Call<GEOIP>, response: Response<GEOIP>) {
//                Log.d(TAG, call.request().toString())
                    val geoIP = response.body() ?: return
                    val gson = Gson()
                    SharedPreferencesUtil.getInstance(context).putString("GEO",gson.toJson(geoIP))
                    geoInfoLiveData.postValue(
                        GEOInfo(
                            geoIP.city,
                            geoIP.zip,
                            geoIP.country_code,
                            geoIP.latitude,
                            geoIP.longitude
                        )
                    )
                }

                override fun onFailure(call: Call<GEOIP>, t: Throwable) {
                }
            })
        }else{
            geoInfoLiveData.postValue(
                GEOInfo(
                    geoip.city,
                    geoip.zip,
                    geoip.country_code,
                    geoip.latitude,
                    geoip.longitude
                )
            )
        }
    }

    fun getWeather(lat: Float, lon: Float){
        timer = Timer()
        timer?.schedule(object: TimerTask(){
            override fun run() {

                Log.d("!!!!!", "start getting weather by lat and lon")
                weatherGrabber.getCurrentWeather(object : Callback<WeatherResponse> {
                    override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                        Log.d(TAG, call.request().toString())
                        val weatherResponse = response.body()
                        Log.d(TAG, "weather_response:" + weatherResponse!!)
                        if (weatherResponse.weather?.isNotEmpty() == true) {
                            val weather = weatherResponse.weather!![0]
                            val temperature = weatherResponse.main
                            var icon = ""

                            val sys = weatherResponse.sys
                            if (sys != null) {
                                val sunrise = sys.sunrise
                                val sunset = sys.sunset
                                if (System.currentTimeMillis() / 1000 in (sunrise + 1)..(sunset - 1)) {
                                    icon = "{" + OWM_D_PREFIX + weather.id + "}"
                                } else {
                                    icon = "{" + OWM_N_PREFIX + weather.id + "}"
                                }
                            }
                            var degree : Float? = null
                            if (temperature != null)
                                degree = weatherResponse.main?.temp
                            val temp = DecimalFormat("##.#").format(degree?.toDouble())
                            val city = weatherResponse.name

                            cityLiveData.postValue(city)
                            tempLiveData.postValue(temp)
                            iconLiveData.postValue(icon)
                        }
                        Log.d(TAG, "get weather ends")
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        Log.d(TAG, "getting weather failed")
                        Log.d(TAG, call.request().url().toString() + "")
                        Log.d(TAG, t.cause.toString() + "")
                        try {
                            throw Exception(t)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }, lat, lon)
            }

        }, 0,30 * 60 * 1000)

    }


    fun getWeather(cityName: String, countryCode: String){
        timer = Timer()
        timer?.schedule(object: TimerTask(){
            override fun run() {
                weatherGrabber.getCurrentWeather(object : Callback<WeatherResponse> {
                    override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                        Log.d(TAG, call.request().toString())
                        val weatherResponse = response.body()
                        Log.d(TAG, "weather_response:$weatherResponse")
                        if (weatherResponse?.weather?.isNotEmpty() == true) {
                            val weather = weatherResponse.weather!![0]
                            val temperature = weatherResponse.main
                            var icon = ""

                            val sys = weatherResponse.sys
                            if (sys != null) {
                                val sunrise = sys.sunrise
                                val sunset = sys.sunset
                                if (System.currentTimeMillis() / 1000 in (sunrise + 1)..(sunset - 1)) {
                                    icon = "{" + OWM_D_PREFIX + weather.id + "}"
                                } else {
                                    icon = "{" + OWM_N_PREFIX + weather.id + "}"
                                }
                            }
                            var degree : Float? = null
                            if (temperature != null)
                                degree = weatherResponse.main?.temp
                            val temp = DecimalFormat("##.#").format(degree?.toDouble())
                            val city = weatherResponse.name

                            cityLiveData.postValue(city)
                            tempLiveData.postValue(temp)
                            iconLiveData.postValue(icon)
                        }
                        Log.d(TAG, "get weather ends")
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        Log.d(TAG, "getting weather failed")
                        Log.d(TAG, call.request().url().toString() + "")
                        Log.d(TAG, t.cause.toString() + "")
                        try {
                            throw Exception(t)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }, cityName, countryCode)
            }

        }, 0,30 * 60 * 1000)

    }

    fun getWeatherNow(zipCode: String, countryCode: String){
        weatherGrabber.getCurrentWeather(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                Log.d(TAG, call.request().toString())
                val weatherResponse = response.body()
                Log.d(TAG, "weather_response:$weatherResponse")
                if (weatherResponse?.weather?.isNotEmpty() == true) {
                    val weather = weatherResponse.weather!![0]
                    val temperature = weatherResponse.main
                    var icon = ""

                    val sys = weatherResponse.sys
                    if (sys != null) {
                        val sunrise = sys.sunrise
                        val sunset = sys.sunset
                        if (System.currentTimeMillis() / 1000 in (sunrise + 1)..(sunset - 1)) {
                            icon = "{" + OWM_D_PREFIX + weather.id + "}"
                        } else {
                            icon = "{" + OWM_N_PREFIX + weather.id + "}"
                        }
                    }
                    var degree : Float? = null
                    if (temperature != null)
                        degree = weatherResponse.main?.temp
                    val temp = DecimalFormat("##.#").format(degree?.toDouble())
                    val city = weatherResponse.name

                    cityLiveData.postValue(city)
                    tempLiveData.postValue(temp)
                    iconLiveData.postValue(icon)
                }
                Log.d(TAG, "get weather ends")
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d(TAG, "getting weather failed")
                Log.d(TAG, call.request().url().toString() + "")
                Log.d(TAG, t.cause.toString() + "")
                try {
                    throw Exception(t)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }, zipCode, countryCode)
    }

    fun stopWeatherTimer(){
        timer?.cancel()
        timer?.purge()
        timer = null
    }

    override fun onCleared() {
        stopWeatherTimer()
        super.onCleared()
    }
}