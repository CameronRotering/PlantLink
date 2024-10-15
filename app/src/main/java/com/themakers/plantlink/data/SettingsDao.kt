package com.themakers.plantlink.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM Settings LIMIT 1") // WHERE id =1
    fun getSetting(): Flow<Settings?>

    //@Query("UPDATE Settings SET isFahrenheit = :newValue WHERE id = 1")
    //suspend fun updateFahrenheit(newValue: Boolean)

    @Upsert
    suspend fun upsertSetting(setting: Settings)


//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(setting: Settings)
}