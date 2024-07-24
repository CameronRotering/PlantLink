package com.themakers.plantlink.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Settings(
    val isFahrenheit: Boolean,
    @PrimaryKey val id: Int = 0
)