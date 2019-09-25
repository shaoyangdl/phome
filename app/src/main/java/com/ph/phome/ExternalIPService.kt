package com.ph.phome

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by yang on 20/12/16.
 */


interface ExternalIPService {
    @GET("/check")
    fun getGEOIP(@Query("access_key") access_key: String): Call<GEOIP>
}
