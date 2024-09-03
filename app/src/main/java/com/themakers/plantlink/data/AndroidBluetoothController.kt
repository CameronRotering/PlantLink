package com.themakers.plantlink.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.themakers.plantlink.Bluetooth.BluetoothController
import com.themakers.plantlink.Bluetooth.BluetoothDeviceDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context
): BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    val bluetoothAdapter by lazy { // Being non-private might fix problem connecting to socket: https://stackoverflow.com/questions/24573755/android-bluetooth-socket-connect-fails
        bluetoothManager?.adapter
    }

    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private var scanning = false

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()


    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }


    init {
        updatePairedDevices()
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            _scannedDevices.update { devices ->
                val newDevice = result.device.toBluetoothDeviceDomain()
                if (newDevice in devices) devices else devices + newDevice
            }

            //leDeviceListAdapter.addDevice(result.device)
            //leDeviceListAdapter.notifyDataSetChanged()
        }
    }


    override fun startDiscovery() {
//        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
//            return
//        }

        if (scanning || bluetoothLeScanner == null) { // The BluetoothLeScanner is only available from the BluetoothAdapter if Bluetooth is currently enabled on the device. If Bluetooth is not enabled, then getBluetoothLeScanner() returns null.
            return
        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )


        updatePairedDevices()

        scanning = true

        bluetoothLeScanner!!.startScan(leScanCallback)


        //bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
//        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
//            return
//        }

        if (!scanning || bluetoothLeScanner == null) { // The BluetoothLeScanner is only available from the BluetoothAdapter if Bluetooth is currently enabled on the device. If Bluetooth is not enabled, then getBluetoothLeScanner() returns null.
            return
        }

        bluetoothLeScanner!!.stopScan(leScanCallback)
        scanning = false
    }





    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
    }

    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }


        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also {devices ->
                _pairedDevices.update { devices } }
    }


    private fun hasPermission(permission: String) : Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}