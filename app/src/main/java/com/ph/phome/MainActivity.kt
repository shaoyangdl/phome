package com.ph.phome


import android.content.BroadcastReceiver
import android.content.Context
import android.net.*
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Rect
import android.hardware.usb.UsbManager
import android.widget.Toast
import com.google.gson.Gson
import java.lang.Exception


class MainActivity : FragmentActivity() {

    lateinit var connectivityManager: ConnectivityManager
    lateinit var weatherViewModel: WeatherViewModel
    lateinit var appViewModel: AppViewModel
    val networkRequestCallback = NetworkRequestCallback()

    lateinit var networkRequest: NetworkRequest
    lateinit var usbReceiver: BroadcastReceiver
    lateinit var packageReceiver: BroadcastReceiver


    val allAppFragTag = "all_app"
    val appSelectionTag = "app_selection"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        usbReceiver = USBBroadcastReceiver()
        val intentFilter = IntentFilter().apply {
            this.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            this.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        registerReceiver(usbReceiver, intentFilter)

        packageReceiver = PackageChangedReceiver()
        val packageIntentFilter = IntentFilter()
        packageIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        packageIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        packageIntentFilter.addDataScheme("package")
        registerReceiver(packageReceiver, packageIntentFilter)
        val appListAdapter = AppListAdapter()
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)

        appViewModel = ViewModelProviders.of(this).get(AppViewModel::class.java)
        appViewModel.initAppList(this)
        appViewModel.initShortcutList(this)
        appViewModel.memoryUsageLiveData.observe(this, Observer {
            memory_usage?.text = it
        })
        appViewModel.startMemoryObserver(this)
/*        weatherViewModel.geoInfoLiveData.observe(this, Observer {
//            it.zipCode?.let { zip->
//                it.countryCode?.let { country->
//                    weatherViewModel.getWeather(zip, country)
//                }
//            }
            it.latitude?.let { lat->
                it.longitude?.let {lon->
                    weatherViewModel.getWeather(lat, lon)
                }

            }
        })*/

/*        Glide.with(this).load(R.drawable.background_vitv)
            .apply(bitmapTransform(BlurTransformation(25, 3)))
            .into(iptv)*/
            /*.into(object : CustomViewTarget<View, Drawable>(iptv) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    iptv.background = errorDrawable
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                    iptv.background = placeholder
                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    iptv.background = resource
                }

            })*/

//        weatherViewModel.getWeather(cityKey, "az")

        weatherViewModel.geoInfoLiveData.observe(this, Observer {
            it.city?.let {  cty->
                it.countryCode?.let { cc->
                    weatherViewModel.getWeather(cty, cc)
                }
            }
        })


        weatherViewModel.iconLiveData.observe(this, Observer {
            weather_icon?.text = it
        })
        weatherViewModel.tempLiveData.observe(this, Observer {
            temperature?.text = "$it \u2103"
        })
        weatherViewModel.cityLiveData.observe(this, Observer {
            city?.text = it
        })
        appViewModel.shortcutList?.observe(this, Observer {
            appListAdapter.submitAppList(it, true)
        })

        appListAdapter.mOnAppClickListener = {
            appViewModel.startApp(it, this@MainActivity)
        }

        appListAdapter.mOnDummyClickListener = {
            ShortcutSelectionFragment().show(supportFragmentManager, appSelectionTag)
        }

        val layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        app_list?.layoutManager = layoutManager
        app_list?.adapter = appListAdapter
        app_list?.addItemDecoration(object : RecyclerView.ItemDecoration(){

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(8,8,8,8)
            }
        })

        val shortcutList = appViewModel.shortcutList?.value
        shortcutList?.let {
            appListAdapter.submitAppList(it, true)
        }


        root?.let {root->
            val childNumber = root.childCount
            for(i in 0 until childNumber){
                val child = root.getChildAt(i)
                if(child is RecyclerView){
                    continue
                }
                child.setOnFocusChangeListener { v, hasFocus ->
                    if(hasFocus){
                        ScaleAnimator.scaleUp(v)
                    }else{
                        ScaleAnimator.scaleDown(v)
                    }
                }
                child.setOnClickListener {
                    onClick(it)
                }
            }
        }

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkRequest = NetworkRequest.Builder().build()
        connectivityManager.requestNetwork(networkRequest, networkRequestCallback)
    }

    private fun onClick(v : View){
        when(v.id){
            iptv?.id -> {
                appViewModel.startAppByPackage("kr.ph.tvon", this)
            }
            youtube?.id -> {
                appViewModel.startAppByPackage("com.google.android.youtube.tv", this)
            }
            all_app?.id -> {
                AllAppFragment().show(supportFragmentManager,allAppFragTag)
            }

            kodi?.id -> {
                appViewModel.startAppByPackage("org.xbmc.kodi", this)
            }
            play?.id -> {
                appViewModel.startAppByPackage("com.android.vending", this)
            }
            time_container?.id -> {
                appViewModel.startDateAction(this)
            }
            temp_container?.id -> {
//                CitySelectionFragment().show(supportFragmentManager, "city_selection")
            }
            memory_container?.id -> {
                startActivity(Intent(this, MemoryCleanActivity::class.java))
            }
            settings?.id -> {
                appViewModel.startSettingsAction(this)
            }
            power?.id -> {
                try {
                    val i = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
                    i.putExtra("android.intent.extra.KEY_CONFIRM", true)
                    startActivity(i)
                }catch (exp: Exception){
                    Toast.makeText(this, "Do you have permission?", Toast.LENGTH_SHORT).show()
                }
            }
            sound?.id -> {
                appViewModel.startSoundAction(this)
            }
            wifi?.id -> {
                appViewModel.startWifiAction(this)
            }
            usb?.id -> {
                appViewModel.startUsbAction(this)
            }
            ethernet?.id -> {
                appViewModel.startNetworkAction(this)
            }
        }
    }

    override fun onDestroy() {
        connectivityManager.unregisterNetworkCallback(networkRequestCallback)
        unregisterReceiver(usbReceiver)
        unregisterReceiver(packageReceiver)
        super.onDestroy()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KeyEvent.KEYCODE_HOME -> {
                val sf = supportFragmentManager.findFragmentByTag(appSelectionTag)
                sf?.let {
                    supportFragmentManager.beginTransaction().remove(it).commitAllowingStateLoss()
                }
                val af = supportFragmentManager.findFragmentByTag(allAppFragTag)
                af?.let {
                    supportFragmentManager.beginTransaction().remove(it).commitAllowingStateLoss()
                }
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onBackPressed() {

    }

    inner class NetworkRequestCallback : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network?) {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            val geoipJson = SharedPreferencesUtil.getInstance(this@MainActivity).getString("GEO","")

            var geoip: GEOIP? = null

            geoipJson?.let {
                if(it != "") {
                    val gson = Gson()
                    geoip = gson.fromJson(it, GEOIP::class.java)
                }
            }

            try {
                weatherViewModel.grabIp(geoip, this@MainActivity)
            } catch (exp: Exception) {

            }

            runOnUiThread {
                if(networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true){
                    ethernet?.setImageResource(R.drawable.ic_settings_ethernet_green_24dp)
                }else{
                    ethernet?.setImageResource(R.drawable.ic_settings_ethernet_black_24dp)
                }

                if(networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true){
                    wifi?.setImageResource(R.drawable.ic_signal_wifi_4_bar_green_24dp)
                }else{
                    wifi?.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp)
                }
            }

            super.onAvailable(network)
        }

        override fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities?) {
            runOnUiThread {
                if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true) {
                    ethernet?.setImageResource(R.drawable.ic_settings_ethernet_green_24dp)
                } else {
                    ethernet?.setImageResource(R.drawable.ic_settings_ethernet_black_24dp)
                }

                if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                    wifi?.setImageResource(R.drawable.ic_signal_wifi_4_bar_green_24dp)
                } else {
                    wifi?.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp)
                }
            }
            super.onCapabilitiesChanged(network, networkCapabilities)
        }

        override fun onLost(network: Network?) {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            runOnUiThread {
                if(networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true){
                    ethernet?.setImageResource(R.drawable.ic_settings_ethernet_green_24dp)
                }else{
                    ethernet?.setImageResource(R.drawable.ic_settings_ethernet_black_24dp)
                }

                if(networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true){
                    wifi?.setImageResource(R.drawable.ic_signal_wifi_4_bar_green_24dp)
                }else{
                    wifi?.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp)
                }
            }

//            super.onLost(network)
        }

        override fun onLinkPropertiesChanged(network: Network?, linkProperties: LinkProperties?) {
            Log.d("!!!!!!","onLinkPropertiesChanged")
            super.onLinkPropertiesChanged(network, linkProperties)
        }

        override fun onUnavailable() {
            Log.d("!!!!!!","onUnavailable")
            super.onUnavailable()
        }

        override fun onLosing(network: Network?, maxMsToLive: Int) {
            Log.d("!!!!!!","onLosing")
            super.onLosing(network, maxMsToLive)
        }
    }

    inner class USBBroadcastReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == UsbManager.ACTION_USB_DEVICE_ATTACHED){
                usb?.setImageResource(R.drawable.ic_usb_green_24dp)
            }
            if(intent?.action == UsbManager.ACTION_USB_DEVICE_DETACHED){
                usb?.setImageResource(R.drawable.ic_usb_black_24dp)
            }
        }

    }

    inner class PackageChangedReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == Intent.ACTION_PACKAGE_ADDED){
                val packageName = intent.data?.encodedSchemeSpecificPart
                packageName?.let {
                    context?.let {context->
                        if(packageManager.getLaunchIntentForPackage(it) != null){
                            val thread = Thread (
                                Runnable {
                                    appViewModel.addApp(
                                        App(
                                            packageName, packageManager
                                                .getApplicationInfo(it, PackageManager.GET_META_DATA)
                                                .loadLabel(packageManager)
                                                .toString()
                                        ), context
                                    )
                                }
                            )
                            thread.start()
                        }
                    }

                }
            }
            if(intent?.action == Intent.ACTION_PACKAGE_REMOVED){
                val packageName = intent.data?.encodedSchemeSpecificPart
                packageName?.let {
                    context?.let { context ->
                        val thread = Thread(
                            Runnable {
                                appViewModel.removeAppByPackageName(it,context)
                            }
                        )
                        thread.start()
                    }
                }
            }
        }

    }
}


