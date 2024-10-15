package com.themakers.plantlink.data

import android.bluetooth.BluetoothGattService
import androidx.compose.runtime.getValue
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

    var finalTemp: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var humidity: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var moisture: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var light: BigDecimal by mutableStateOf(BigDecimal(0))
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


    fun setTemp(pTemp: Double) {
        //temperatureC = BigDecimal(pTemp).setScale(2, RoundingMode.HALF_EVEN)
        //temperatureF = BigDecimal((pTemp * 1.8) + 32).setScale(2, RoundingMode.HALF_EVEN)

//        if (state!!.isFahrenheit) { // When getting finalTemp, if the setting is Fahrenheit, give Fahrenheit, visa versa
//            finalTemp = BigDecimal((pTemp * 1.8) + 32).setScale(2, RoundingMode.HALF_EVEN)
//        } else {
//            finalTemp = BigDecimal(pTemp).setScale(2, RoundingMode.HALF_EVEN)
//        }

        finalTemp = BigDecimal(pTemp).setScale(2, RoundingMode.HALF_EVEN)
    }

    fun getTempString(isFahrenheit: Boolean): String {
        if (isFahrenheit) {
            return BigDecimal((finalTemp.toDouble() * 1.8) + 32).setScale(2, RoundingMode.HALF_EVEN).toString()
        } else {
            return finalTemp.toString()
        }
    }

    fun setHumid(pHumid: Double) {
        humidity = BigDecimal(pHumid).setScale(2, RoundingMode.HALF_EVEN)
    }

    fun setMoist(pMoist: Double) {
        moisture = BigDecimal(100 *
                (pMoist.coerceIn(0.0, 880.0) / 880)).setScale(2, RoundingMode.HALF_EVEN)
    }

    fun setLight(pLight: Double) {
        //light = BigDecimal(pLight).setScale(2, RoundingMode.HALF_EVEN)

        light = BigDecimal(100 *
                (pLight.coerceIn(0.0, 4095.0) / 4095)).setScale(2, RoundingMode.HALF_EVEN) // With phone flashlight on max, 4095 seems to be max, peaking at 4095


    // 666 seems to be around max amount (When tested with phone flashlight on max, it got mostly below 660 except for one being 661. When tested in direct sunlight it got up to 667)
    }
}