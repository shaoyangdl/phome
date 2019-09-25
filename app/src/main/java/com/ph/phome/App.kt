package com.ph.phome

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class App (@PrimaryKey var packageName: String, var appName: String?){
    @Ignore
    var icon: Drawable? = null
    var isShortCut: Boolean = false
}