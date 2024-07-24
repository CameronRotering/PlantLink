package com.themakers.plantlink.data

sealed interface SettingEvent {
    //object ChangeTempUnit : SettingEvent
    //object Create : SettingEvent
    data class SetTempUnit(val isFahrenheit: Boolean) : SettingEvent
}