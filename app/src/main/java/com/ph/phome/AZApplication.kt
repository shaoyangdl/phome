package com.ph.phome

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule

class AZApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Iconify.with(WeathericonsModule())
    }
}