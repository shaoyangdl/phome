package com.ph.phome


import android.os.Bundle
import android.os.Handler
import androidx.preference.Preference
import android.util.ArrayMap
import androidx.leanback.preference.LeanbackPreferenceFragment
import java.lang.IllegalStateException


class CityFragment : LeanbackPreferenceFragment(){
    private val TAG = "LanguageFragment"

    private val LANGUAGE_RADIO_GROUP = "language"

    private val mCityInfoMap = ArrayMap<String, String>()

    // Adjust this value to keep things relatively responsive without janking animations
    private val LANGUAGE_SET_DELAY_MS = 500
    private val mDelayHandler = Handler()
    private var mNewCity: String? = null
    private val mSetLanguageRunnable = Runnable {
        mNewCity?.let {
            SharedPreferencesUtil.getInstance(context).putString("city", it)
        }
    }



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val themedContext = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(themedContext)
        screen.setTitle(R.string.expand_button_title)

        var currentCity: String = SharedPreferencesUtil.getInstance(context).getString("city","Baku") ?: "Baku"



        val cityKey = context.resources.getStringArray(R.array.city_key)


        val cityList = context.resources.getStringArray(R.array.city_key)

        if(cityKey.size != cityList.size){
            throw IllegalStateException("city key must be same size with city list, please check city list and city keys in xml")
        }

        var activePref: Preference? = null
        for (city in cityList) {

            val index = cityList.indexOf(city)



            mCityInfoMap[cityKey[index]] = city

            val radioPreference = RadioPreference(themedContext)
            radioPreference.key = cityKey[index]
            radioPreference.isPersistent = false
            radioPreference.title = city
            radioPreference.setRadioGroup(LANGUAGE_RADIO_GROUP)
            radioPreference.layoutResource = R.layout.preference_reversed_widget

            if (cityKey[index] == currentCity) {
                radioPreference.isChecked = true
                activePref = radioPreference
            }
            screen.addPreference(radioPreference)
        }

        if (activePref != null && savedInstanceState == null) {
            scrollToPreference(activePref)
        }

        preferenceScreen = screen
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference is RadioPreference) {
            val radioPreference = preference as RadioPreference
            radioPreference.clearOtherRadioPreferences(preferenceScreen)
            if (radioPreference.isChecked) {
                mNewCity = radioPreference.key
                mDelayHandler.removeCallbacks(mSetLanguageRunnable)
                mDelayHandler.postDelayed(mSetLanguageRunnable, LANGUAGE_SET_DELAY_MS.toLong())
            } else {
                radioPreference.isChecked = true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    companion object {
        fun newInstance(): CityFragment {
            return CityFragment()
        }
    }


}