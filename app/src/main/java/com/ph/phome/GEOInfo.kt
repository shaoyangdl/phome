package com.ph.phome

import android.os.Parcel
import android.os.Parcelable

class GEOInfo(var city:String?, var zipCode:String?, var countryCode: String?, var latitude: Float?, var longitude: Float?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float
    )

    override fun toString(): String {
        return "GEOInfo(city=$city, zipCode=$zipCode, countryCode=$countryCode, latitude=$latitude, longitude=$longitude)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)
        parcel.writeString(zipCode)
        parcel.writeString(countryCode)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GEOInfo> {
        override fun createFromParcel(parcel: Parcel): GEOInfo {
            return GEOInfo(parcel)
        }

        override fun newArray(size: Int): Array<GEOInfo?> {
            return arrayOfNulls(size)
        }
    }
}