package com.themakers.plantlink.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SettingsDao {
    @Query("SELECT * FROM Settings WHERE id =1")
    suspend fun getSetting(): Settings?

    @Query("UPDATE Settings SET isFahrenheit = :newValue WHERE id = 1")
    suspend fun updateFahrenheit(newValue: Boolean)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(setting: Settings)
}