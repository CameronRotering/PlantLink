package com.themakers.plantlink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.math.BigDecimal
import java.math.RoundingMode

class PlantDataViewModel {
    var finalTemp: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var humidity: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var moisture: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var light: BigDecimal by mutableStateOf(BigDecimal(0))
        private set


    fun setTemp(pTemp: Double) {
        //temperatureC = BigDecimal(pTemp).setScale(2, RoundingMode.HALF_EVEN)
        //temperatureF = BigDecimal((pTemp * 1.8) + 32).setScale(2, RoundingMode.HALF_EVEN)

        finalTemp = BigDecimal(pTemp).setScale(2, RoundingMode.HALF_EVEN)
    }

    fun setHumid(pHumid: Double) {
        humidity = BigDecimal(pHumid).setScale(2, RoundingMode.HALF_EVEN)
    }

    fun setMoist(pMoist: Double) {
        moisture = BigDecimal(100 *
                (pMoist.coerceIn(0.0, 880.0) / 880)).setScale(2, RoundingMode.HALF_EVEN)
    }

    fun setLight(pLight: Double) {
        light = BigDecimal(100 *
                (pLight.coerceIn(0.0, 666.0) / 666)).setScale(2, RoundingMode.HALF_EVEN) // 666 seems to be around max amount (When tested with phone flashlight on max, it got mostly below 660 except for one being 661. When tested in direct sunlight it got up to 667)
    }
}