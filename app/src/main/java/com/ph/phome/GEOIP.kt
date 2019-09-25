package com.ph.phome


/**
 * Created by yang on 20/12/16.
 */

class GEOIP {
    var ip: String? = null
    var country_code: String? = null
    var country_name: String? = null
    var region_code: String? = null
    var region_name: String? = null
    var city: String? = null
    var zip: String? = null
    var latitude: Float? = null
    var longitude: Float? = null
    @Transient
    var time_zone: String? = null

    override fun toString(): String {
        return "GEOIP(ip=$ip, country_code=$country_code, country_name=$country_name, region_code=$region_code, region_name=$region_name, city=$city, zip=$zip, latitude=$latitude, longitude=$longitude, time_zone=$time_zone)"
    }


}
