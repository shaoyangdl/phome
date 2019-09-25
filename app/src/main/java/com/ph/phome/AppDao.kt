package com.ph.phome

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertApp(vararg apps: App)

    @Update
    fun updateApp(vararg apps: App)

    @Delete
    fun deleteApp(vararg apps: App)

    @Query("DELETE FROM App WHERE packageName= :packageName")
    fun deleteAppByPackageName(packageName: String): Int

    @Query("DELETE FROM App")
    fun deleteAll()

    @Query("SELECT * FROM App")
    fun findAllAppLiveData() : LiveData<List<App>>

    @Query("SELECT * FROM App")
    fun findAllApp() : List<App>

    @Query("SELECT * FROM App WHERE isShortCut = 1")
    fun findShortcutApps() : LiveData<List<App>>

    @Query("SELECT * FROM App WHERE packageName = :packageName")
    fun findAppByPackageName(packageName: String) : List<App>
}