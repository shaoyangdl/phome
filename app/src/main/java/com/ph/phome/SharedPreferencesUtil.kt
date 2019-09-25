package com.ph.phome

import android.content.Context
import android.content.SharedPreferences

/**
 * @author Droid
 */
class SharedPreferencesUtil//private static SharedPreferences.Editor mSharedPreferences.edit();
private constructor(context: Context) {
    init {
        mSharedPreferences = context.getSharedPreferences(
            SHAREDPREFERENCE_NAME, Context.MODE_PRIVATE)
        //mSharedPreferences.edit() = mSharedPreferences.edit();
    }

    fun putString(key: String, value: String) {
        mSharedPreferences?.edit()?.putString(key, value)?.apply()

    }

    fun putInt(key: String, value: Int) {
        mSharedPreferences?.edit()?.putInt(key, value)?.apply()

    }

    fun putLong(key: String, value: Long) {
        mSharedPreferences?.edit()?.putLong(key, value)?.apply()

    }

    fun putBool(key: String, value: Boolean) {
        mSharedPreferences?.edit()?.putBoolean(key, value)?.apply()
    }

    fun getBool(key: String, value: Boolean): Boolean {
        return mSharedPreferences?.getBoolean(key, value) ?: false
    }

    fun getString(key: String, value: String): String? {
        return mSharedPreferences?.getString(key, value)
    }

    fun getInt(key: String, value: Int): Int {
        return mSharedPreferences?.getInt(key, value) ?: 0
    }

    fun getLong(key: String, value: Long): Long {
        return mSharedPreferences?.getLong(key, value) ?: -1
    }


    companion object {

        private val SHAREDPREFERENCE_NAME = "sharedpreferences_droid"
        private var mInstance: SharedPreferencesUtil? = null
        private var mSharedPreferences: SharedPreferences? = null

        fun getInstance(context: Context): SharedPreferencesUtil {
            if (mInstance == null) {
                mInstance =
                    SharedPreferencesUtil(context)
            }
            return mInstance!!
        }
    }


}
