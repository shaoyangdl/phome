package com.ph.phome

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import java.lang.Exception
import java.util.*
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.content.pm.ApplicationInfo





class AppViewModel : ViewModel() {

    var appList : LiveData<List<App>>? = null

    var shortcutList: LiveData<List<App>>? = null

    private var timer = Timer()

    val memoryUsageLiveData = MutableLiveData<String>()

    fun initAppList(context: Context){

        addNewAppIntoDB(context)

        appList = Transformations.map(AppDatabase.getInstance(context).appDao().findAllAppLiveData()) {
            for(app in it){
                try {
                    app.icon = context.packageManager.getApplicationIcon(app.packageName)
                }catch (nne: PackageManager.NameNotFoundException){
                    val thread = Thread(
                        Runnable {
                            removeAppByPackageName(app.packageName, context)
                        }
                    )
                    thread.start()

                }
            }
            it
        }
    }

    fun addNewAppIntoDB(context: Context){
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        val thread = Thread(
            Runnable {
                val appApplist = AppDatabase.getInstance(context).appDao().findAllApp()
                for(app in appApplist){
                    if(context.packageManager.getLaunchIntentForPackage(app.packageName) == null){

                        removeAppByPackageName(app.packageName, context)

                    }
                }
            }
        )
        thread.start()
        val resolveInfoList = context.packageManager.queryIntentActivities(intent, 0)
//        Log.d("!!!!!", "${resolveInfoList.size}")
        for (resolveInfo in resolveInfoList) {
//            if (!isSystemPackage(resolveInfo)) {

                val applicationInfo = resolveInfo.activityInfo.applicationInfo
//                Log.d("!!!!!", applicationInfo.packageName)
                val thread1 = Thread(
                    Runnable {
                        try {
                            if (AppDatabase.getInstance(context).appDao().findAppByPackageName(applicationInfo.packageName).isEmpty()) {
                                val app = App(
                                    applicationInfo.packageName,
                                    applicationInfo.loadLabel(context.packageManager).toString()
                                )
                                addApp(app, context)
                            }
                        }catch (exception: Exception){
                            exception.printStackTrace()
                        }
                    })
                thread1.start()
//            }
        }


    }

    private fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
        return resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    fun initShortcutList(context: Context){
        shortcutList = Transformations.map(AppDatabase.getInstance(context).appDao().findShortcutApps()) {
            for(app in it){
                app.icon = context.packageManager.getApplicationIcon(app.packageName)
            }
            it
        }
    }

    fun startMemoryObserver(context: Context){
        timer.schedule(object: TimerTask(){
            override fun run() {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val mi = ActivityManager.MemoryInfo()
                am.getMemoryInfo(mi)
                memoryUsageLiveData.postValue("${((mi.totalMem - mi.availMem) * 100) / mi.totalMem}%")
            }

        }, 0,10000)
    }

    fun addApp(app: App, context: Context){
        AppDatabase.getInstance(context).appDao().insertApp(app)
    }

    fun removeApp(app: App, context: Context){
        AppDatabase.getInstance(context).appDao().deleteApp(app)
    }

    fun removeAppByPackageName(packageName: String, context: Context){
        Log.d("!!!","?????")
        val count = AppDatabase.getInstance(context).appDao().deleteAppByPackageName(packageName)
        Log.d("!!!"," trying to delete $packageName, and $count has been deleted")
    }

    fun updateApp(app: App, context: Context){
        AppDatabase.getInstance(context).appDao().updateApp(app)
    }

    fun startApp(app: App, context: Context){
        val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
        if(intent != null) {
            context.startActivity(intent)
        }else{
            Toast.makeText(context, "No launch intent is found", Toast.LENGTH_SHORT).show()
        }
    }

    fun startAppByPackage(packageName: String, context: Context){
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if(intent != null) {
            context.startActivity(intent)
        }else{
            Toast.makeText(context, "No launch intent is found", Toast.LENGTH_SHORT).show()
        }
    }

    fun startDateAction(context: Context){
        val action = "android.settings.DATE_SETTINGS"
        startActivity(context, action)
    }

    fun startSoundAction(context: Context){
        val action = "com.android.settings.SOUND_SETTINGS"
        startActivity(context, action)
    }

    fun startWifiAction(context: Context){
        val action = "com.android.net.wifi.SETUP_WIFI_NETWORK"
        startActivity(context, action)
    }

    fun startNetworkAction(context: Context){
        val action = "android.settings.WIFI_SETTINGS"
        startActivity(context, action)
    }

    fun startUsbAction(context: Context){
        val action = "android.settings.INTERNAL_STORAGE_SETTINGS"
        startActivity(context, action)
    }

    fun startSettingsAction(context: Context){
        val action = "android.settings.SETTINGS"
        startActivity(context, action)
    }

    private fun startActivity(context: Context, action: String){
        val intent = Intent(action)
        try{
            context.startActivity(intent)
        }catch (exp: Exception){
            Toast.makeText(context,"failt to start activity", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        timer.purge()
        timer.cancel()
        super.onCleared()
    }
}