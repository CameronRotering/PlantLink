package com.themakers.plantlink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.math.BigDecimal
import java.math.RoundingMode

class PlantDataViewModel {

    var temperatureC: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var temperatureF: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var humidity: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var moisture: BigDecimal by mutableStateOf(BigDecimal(0))
        private set


    fun setTemp(pTemp: Double) {
        temperatureC = BigDecimal(pTemp).setScale(2, RoundingMode.HALF_EVEN)
        temperatureF = BigDecimal((pTemp * 1.8) + 32).setScale(2, RoundingMode.HALF_EVEN)
    }

    fun setHumid(pHumid: Double) {
        humidity = BigDecimal(pHumid).setScale(2, RoundingMode.HALF_EVEN)
    }

    fun setMoist(pMoist: Double) {
        moisture = BigDecimal(100 *
                (pMoist.coerceIn(0.0, 880.0) / 880)).setScale(2, RoundingMode.HALF_EVEN)
    }
}