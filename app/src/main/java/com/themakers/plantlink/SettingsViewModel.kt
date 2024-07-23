package com.themakers.plantlink

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.themakers.plantlink.data.AppDatabase
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {
    fun changeSetting(context: Context, isFahrenheit: Boolean) { //database: AppDatabase
        val database = AppDatabase.getDatabase(context)

        viewModelScope.launch {
            database.settingsDao().updateFahrenheit(isFahrenheit)
            //val currentStatus = myRepository.getItemById(itemId)?.isFavorite ?: false
            //myRepository.updateFavoriteStatus(itemId, !currentStatus)
        }



        //database.settingsDao().updateFahrenheit(isFahrenheit)
    }

    fun getSetting(context: Context): Boolean { //database: AppDatabase
        var isFahrenheit = true

        viewModelScope.launch {
            isFahrenheit =
                AppDatabase.getDatabase(context).settingsDao().getSetting()?.isFahrenheit ?: true
        }

        return isFahrenheit
    }
}