package com.themakers.plantlink.data

import android.bluetooth.BluetoothGattService
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.math.BigDecimal
import java.math.RoundingMode

data class PlantDevice (
    //val id: Int,
    val macAddress: String, // I don't think I need this
    val name: String,
    val mMinMoisture: String = "0",
    val mMaxMoisture: String = "0",
    val device: BluetoothGattService? = null
) {
    var plantName: String by mutableStateOf(name)
        private set

    var minMoisture: String by mutableStateOf(mMinMoisture)
        private set

    var maxMoisture: String by mutableStateOf(mMaxMoisture)
        private set

    var moisture: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var light: Long by mutableLongStateOf(0)
        private set


    fun setName(pName: String) {
        plantName = pName
    }

    fun setMinMoist(pMin: String) {
        minMoisture = pMin
    }

    fun setMaxMoist(pMax: String) {
        maxMoisture = pMax
    }

    fun setMoist(pMoist: Double) {
        moisture = BigDecimal(100 *
                (pMoist.coerceIn(0.0, 880.0) / 880)).setScale(2, RoundingMode.HALF_EVEN)
    }

    fun setAmbientLight(pLight: Long) {
        light = pLight // Lux scale: https://en.wikipedia.org/wiki/Lux
    }
}