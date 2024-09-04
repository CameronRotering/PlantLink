package com.themakers.plantlink.Bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
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

    var gatt by mutableStateOf<BluetoothGatt?>(null)
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

    fun setControllerViewModel(btViewModel: BluetoothViewModel) {
        bluetoothController.assignViewModel(btViewModel)
    }

    fun isConnected(): Boolean {
        return connectedThread != null
    }

    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun setGatt(context: Context, device: BluetoothDevice, autoConnect: Boolean) {
        btModule = device
        gatt = device.connectGatt(context, autoConnect, bluetoothController.gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun setCharacteristicNotification() {
        gatt!!.services.forEach{ service ->
            if (service.uuid != UUID.fromString("00001800-0000-1000-8000-00805f9b34fb") && service.uuid != UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")) {
                service.characteristics.forEach { characteristic ->
                    gatt!!.setCharacteristicNotification(characteristic, true)
                }
            }
        }
        //gatt!!.setCharacteristicNotification(characteristic, enabled)
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