package com.themakers.plantlink

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.themakers.plantlink.data.SettingEvent
import com.themakers.plantlink.data.SettingState
import com.themakers.plantlink.data.Settings
import com.themakers.plantlink.data.SettingsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dao: SettingsDao
): ViewModel() {

    private val _state = MutableStateFlow(SettingState())
    private val setting = dao.getSetting().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val state = combine(_state, setting) {state, setting ->
        state.copy(
            isFahrenheit = setting?.isFahrenheit ?: true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingState())
        //setting.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingState())

    fun onEvent(event: SettingEvent) {
        when(event) {
//            SettingEvent.ChangeTempUnit -> {
//                _state.update { it.copy(
//                    isFahrenheit = !it.isFahrenheit
//                ) }
//            }
            is SettingEvent.SetTempUnit -> {

                Log.e("TEMPCHANGE", "Temperature is changing to ${if (event.isFahrenheit) "Fahrenheit" else "Celsius"}")

                val settingChange = Settings(event.isFahrenheit)

                _state.update { it.copy(
                    isFahrenheit = event.isFahrenheit
                ) }


                viewModelScope.launch {
                    dao.upsertSetting(settingChange)
                }

                Log.e("TEMPCHANGE", "Temperature has been changed to ${if (_state.value.isFahrenheit != false) "Fahrenheit" else "Celsius"}")
            }

    fun getSetting(context: Context): Boolean { //database: AppDatabase
        var isFahrenheit = true

        viewModelScope.launch {
            isFahrenheit =
                AppDatabase.getDatabase(context).settingsDao().getSetting()?.isFahrenheit ?: true
        }
    }
}