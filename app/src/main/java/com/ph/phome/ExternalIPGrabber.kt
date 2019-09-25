package com.ph.phome

import okhttp3.OkHttpClient
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by yang on 20/12/16.
 */

class ExternalIPGrabber {
    private val retrofit: Retrofit
    private val service: ExternalIPService

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
        service = retrofit.create(ExternalIPService::class.java)
    }

    fun getIP(callback: Callback<GEOIP>) {
        val call = service.getGEOIP("d9c5c770c6c24ea3841e116f4799a7e8")
        call.enqueue(callback)
    }

    companion object {
        private val baseUrl = "http://api.ipstack.com"
    }


}
