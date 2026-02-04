package com.themakers.plantlink.data

import android.bluetooth.BluetoothGattService
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.math.BigDecimal
import java.math.RoundingMode

data class HubDevice (
    //val id: Int,
    val macAddress: String, // I don't think I need this
    val name: String,
    val device: BluetoothGattService? = null
) {
    var hubName: String by mutableStateOf(name)
        private set

    var finalTemp: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var humidity: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var light: Long by mutableLongStateOf(0)
        private set

    fun setName(pName: String) {
        hubName = pName
    }


    fun setTemp(pTemp: Double) {
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

    fun setAmbientLight(pLight: Long) {
        light = pLight // Lux scale: https://en.wikipedia.org/wiki/Lux
    }
}