package com.ph.phome

/**
 * Created by yang on 19/12/16.
 */

object Fields {
    private val countryCode = "countryCode"
    private val cityName = "city"
    private val zoneName = "zoneName"
    private val gmtOffset = "gmtOffset"
    private val dst = "dst"
    private val prefix = "fields="
    private val separator = ","

    val fields: String
        get() = prefix + countryCode + separator + cityName + separator + zoneName + separator + gmtOffset + separator + dst
}
