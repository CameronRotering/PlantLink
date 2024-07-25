package com.themakers.plantlink.data

data class PlantDevice (
    val id: Int,
    val macAddress: String,
    val name: String,
    val minMoisture: String = "0",
    val maxMoisture: String = "0"
)