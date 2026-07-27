package com.themakers.plantlink.Bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.themakers.plantlink.data.AndroidBluetoothController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class BluetoothViewModel(
    private val bluetoothController: AndroidBluetoothController
): ViewModel() {
    private val _state = MutableStateFlow(BluetoothUiState())

    var btModule: BluetoothDevice? by mutableStateOf(null)
        private set

    var gatt by mutableStateOf<BluetoothGatt?>(null)
        private set

    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        bluetoothController.isScanning,
        bluetoothController.isConnected,
        _state
    ) { scannedDevices, pairedDevices, isScanning, isConnected, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            isScanning = isScanning,
            isConnected = isConnected
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun setControllerViewModel(btViewModel: BluetoothViewModel) {
        bluetoothController.assignViewModel(btViewModel)
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
        @Suppress("DEPRECATION")
        gatt = device.connectGatt(context, autoConnect, bluetoothController.gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    private fun enableNotification(characteristic: BluetoothGattCharacteristic) {
        gatt?.let { gatt ->
            gatt.setCharacteristicNotification(characteristic, true)
            val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            descriptor?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeDescriptor(it, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    @Suppress("DEPRECATION")
                    it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    @Suppress("DEPRECATION")
                    gatt.writeDescriptor(it)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun setCharacteristicNotification() {
        viewModelScope.launch {
            gatt?.services?.forEach { service ->
                if (service.uuid == UUID.fromString("eb9782b0-44a6-4799-873d-1e7580893e40")) {
                    service.characteristics.forEach { characteristic ->
                        enableNotification(characteristic)
                        delay(100.milliseconds) // Small delay between GATT operations
                    }

                } else if (service.uuid != UUID.fromString("eb9782b0-44a6-4799-873d-1e7580893e40") && service.uuid != UUID.fromString("00001800-0000-1000-8000-00805f9b34fb") && service.uuid != UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")) { // General characteristics I don't change
                    service.characteristics.forEach { characteristic ->
                        enableNotification(characteristic)
                        delay(100.milliseconds)

                        service.getCharacteristic(UUID.fromString("b761e2e9-fac9-439c-a321-123d7f404e36"))?.let {
                            gatt?.readCharacteristic(it)
                            delay(100.milliseconds)
                        }
                        service.getCharacteristic(UUID.fromString("39aec0bb-21c9-4519-8e32-e25c7523fde9"))?.let {
                            gatt?.readCharacteristic(it)
                            delay(100.milliseconds)
                        }
                        service.getCharacteristic(UUID.fromString("437fcdb7-74c7-4968-a669-384aa06f20c1"))?.let {
                            gatt?.readCharacteristic(it)
                            delay(100.milliseconds)
                        }
                    }
                }
            }
        }
    }
}