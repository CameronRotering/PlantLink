package com.themakers.plantlink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.math.BigDecimal

class PlantDataViewModel {
    var moisture: BigDecimal by mutableStateOf(BigDecimal(0))
        private set

    var light: Long by mutableLongStateOf(0)
        private set
}