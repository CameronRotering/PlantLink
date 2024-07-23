package com.themakers.plantlink.Bluetooth

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.themakers.plantlink.data.AndroidBluetoothController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

class BluetoothViewModel(
    private val bluetoothController: AndroidBluetoothController
): ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())

    var btModule: BluetoothDevice? by mutableStateOf(null)
        private set

    var uuid by mutableStateOf(UUID.randomUUID())
        private set

    var connectedThread: ConnectedThread? by mutableStateOf(null)
        private set


    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun isConnected(): Boolean {
        return connectedThread != null
    }

    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }


    fun setModule(module: BluetoothDevice) {
        btModule = module
    }

    fun setUUID(_UUID: UUID) {
        uuid = _UUID
    }

    fun setThread(mConnectedThread: ConnectedThread) {
        connectedThread = mConnectedThread
    }
}