package com.ph.phome


import android.os.Bundle

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_memory_clean.*
import java.util.*


class MemoryCleanActivity : FragmentActivity() {


    val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_clean)
        val currentMemory = getAvailMemory(this)

        var currentUsage = getMemoryUsagePercentage(this)
        arc_progress?.progress = getMemoryUsagePercentage(this)

        killProcesses(this@MemoryCleanActivity)

        val thread = Thread {
            Thread.sleep(2000)
            while(currentUsage != 100){
                currentUsage++
                runOnUiThread {
                    arc_progress?.progress = currentUsage
                }
                Thread.sleep(50)
            }
            while (currentUsage > getMemoryUsagePercentage(this@MemoryCleanActivity)){
                currentUsage--
                runOnUiThread {
                    arc_progress?.progress = currentUsage
                }
                Thread.sleep(50)
            }


            val afterMemory = getAvailMemory(this@MemoryCleanActivity)

            val sizeFreed = (Math.abs(afterMemory - currentMemory)) / (1024 * 1024)

            runOnUiThread {
                Toast.makeText(this@MemoryCleanActivity, "$sizeFreed MB memory has been released", Toast.LENGTH_SHORT).show()
            }

            Thread.sleep(2000)
            finish()

        }
        thread.start()



    }


    override fun onDestroy() {
        timer.cancel()
        timer.purge()
        super.onDestroy()
    }

    private fun killProcesses(context: Context){
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        installedApplications.forEach {appInfo->
            if(appInfo.packageName != this.packageName){
                am.killBackgroundProcesses(appInfo.processName)
            }
        }
    }




    private fun getAvailMemory(context: Context): Long {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi.availMem
    }

    private fun getMemoryUsagePercentage(context: Context) : Int{
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val usage = mi.totalMem - mi.availMem
        return ((usage.toDouble() / mi.totalMem.toDouble()) * 100).toInt()
    }

}