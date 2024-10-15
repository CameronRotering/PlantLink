package com.themakers.plantlink.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Settings::class], version = 1)
abstract class SettingsDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao

    //abstract val dao: SettingsDao
}