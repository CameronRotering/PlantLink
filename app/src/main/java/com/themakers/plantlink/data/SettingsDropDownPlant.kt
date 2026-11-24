package com.themakers.plantlink.data

data class SettingsDropDownPlant(
    val name: String,
    val minMoisture: Int,
    val maxMoisture: Int,
    val id: String? = null,
    val scientificName: String? = null,
    val imageUrl: String? = null
)
