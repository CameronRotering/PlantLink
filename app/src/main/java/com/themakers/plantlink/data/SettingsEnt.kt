package com.themakers.plantlink.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Settings(
    @PrimaryKey val id: Int = 1, // Only using one setting
    val isFahrenheit: Boolean
)