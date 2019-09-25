package com.ph.phome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_city_selection.*

class CitySelectionFragment : DialogFragment() {


    lateinit var weatherViewModel: WeatherViewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val cityListAdapter = CityListAdapter()
        weatherViewModel = ViewModelProviders.of(activity!!).get(WeatherViewModel::class.java)
        val cities = activity!!.resources.getStringArray(R.array.city_key)
        val cityKeys = activity!!.resources.getStringArray(R.array.city_key)
        val defaultCity = SharedPreferencesUtil.getInstance(activity!!).getString("city","Baku") ?: "Baku"
        val default = cityKeys.indexOf(defaultCity)
        if(default >= 0){
            cityListAdapter.default = default
        }
        cityListAdapter.submitCityList(cities)
        cityListAdapter.mOnCityClickListener = {
            SharedPreferencesUtil.getInstance(activity!!).putString("city",cityKeys[it])
            weatherViewModel.stopWeatherTimer()
            weatherViewModel.getWeather(cityKeys[it],"az")
        }
        city_list?.adapter = cityListAdapter
        city_list?.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_city_selection,container,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        super.onCreate(savedInstanceState)
    }
}