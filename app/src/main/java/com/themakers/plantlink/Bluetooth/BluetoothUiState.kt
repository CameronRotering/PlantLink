package com.themakers.plantlink.Bluetooth

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isScanning: Boolean = false,
    var isConnected: Int = 0 // 0 = Disconnected, 1 = Connecting, 2 = Connected
)
