package com.themakers.plantlink.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.MutableState
import com.themakers.plantlink.Bluetooth.BluetoothController
import com.themakers.plantlink.Bluetooth.BluetoothDeviceDomain
import com.themakers.plantlink.Bluetooth.BluetoothViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID


@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context,
    private val plantDevices: MutableList<PlantDevice>,
    private var hubDevice: MutableState<HubDevice>
): BluetoothController {
    var viewModel: BluetoothViewModel? = null



    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter by lazy { // Being non-private might fix problem connecting to socket: https://stackoverflow.com/questions/24573755/android-bluetooth-socket-connect-fails
        bluetoothManager?.adapter
    }

    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    //private var scanning = false

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean>
        get() = _isScanning.asStateFlow()

    private val _isConnected = MutableStateFlow(0)
    var isConnected: StateFlow<Int>
        get() = _isConnected.asStateFlow()
        set(value) {
            _isConnected.value = value.value
        }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

//    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }


    init {
        updatePairedDevices()
    }

    fun assignViewModel(btViewModel: BluetoothViewModel) {
        viewModel = btViewModel
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

//    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
//        val intent = Intent(action)
//
//        // This is special handling for the Heart Rate Measurement profile. Data
//        // parsing is carried out as per profile specifications.
//
//        // For all other profiles, writes the data formatted in HEX.
//        val data: ByteArray? = characteristic.value
//        if (data?.isNotEmpty() == true) {
//            val hexString: String = data.joinToString(separator = " ") {
//                String.format("%02X", it)
//            }
//            intent.putExtra(EXTRA_DATA, "$data\n$hexString")
//
//
//        }
//        sendBroadcast(intent)
//    }


    override fun startDiscovery() {
//        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
//            return
//        }

        if (_isScanning.value || bluetoothLeScanner == null) { // The BluetoothLeScanner is only available from the BluetoothAdapter if Bluetooth is currently enabled on the device. If Bluetooth is not enabled, then getBluetoothLeScanner() returns null.
            return
        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )


        updatePairedDevices()

        _isScanning.value = true

        bluetoothLeScanner!!.startScan(leScanCallback)


        //bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
//        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
//            return
//        }

        if (!_isScanning.value || bluetoothLeScanner == null) { // The BluetoothLeScanner is only available from the BluetoothAdapter if Bluetooth is currently enabled on the device. If Bluetooth is not enabled, then getBluetoothLeScanner() returns null.
            return
        }

        bluetoothLeScanner!!.stopScan(leScanCallback)
        _isScanning.value = false
    }



    val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) { // Cannot use toast on either connection state due to thread not calling looper.prepare
                BluetoothProfile.STATE_CONNECTED -> {
                    // Successfully connected to the GATT Server.
                    // You can now call gatt.discoverServices() to discover available services.
//                    Toast.makeText(
//                        context,
//                        "Bluetooth connected.",
//                        Toast.LENGTH_LONG
//                    ).show()



                    Log.w("BT CONNECT CHANGE", "Connected.")

                    _isConnected.value = 2

                    gatt.discoverServices()
                }
//                BluetoothProfile.STATE_CONNECTING -> {
//                    Log.w("BT CONNECT CHANGE", "Connecting.")
//                    _isConnecting.value = true
//                }
//                BluetoothProfile.STATE_DISCONNECTING -> {
//                    Log.w("BT CONNECT CHANGE", "Disconnecting.")
//                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    // Disconnected from the GATT Server.
                    Log.w("BT CONNECT CHANGE", "Disconnected.")
                    _isConnected.value = 0

//                    Toast.makeText(
//                        context,
//                        "Bluetooth disconnected.",
//                        Toast.LENGTH_LONG
//                    ).show()
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val buffer: ByteArray = value

                // If hub, don't search peripherals
                if (characteristic.service.uuid == UUID.fromString("eb9782b0-44a6-4799-873d-1e7580893e40")) { // UUID for hub
                    when (characteristic.uuid) {
                        UUID.fromString("7ccecf3a-cb17-4f62-b22d-671639009fc8") -> { // Temperature
                            hubDevice.value.setTemp(String(buffer, 0, buffer.size).toDouble())
                        }

                        UUID.fromString("75171ef4-4fc5-4fd4-a393-8f4cc2f9fbcd") -> { // Humidity
                            hubDevice.value.setHumid(String(buffer, 0, buffer.size).toDouble())
                        }
                    }
                } else {
                    for (i in plantDevices) {
                        if (i.device == characteristic.service) {
                            when (characteristic.uuid) {
                                // Settings characteristics

                                UUID.fromString("b761e2e9-fac9-439c-a321-123d7f404e36") -> {
                                    i.setName(String(buffer, 0, buffer.size))
                                }

                                UUID.fromString("39aec0bb-21c9-4519-8e32-e25c7523fde9") -> {
                                    i.setMinMoist(String(buffer, 0, buffer.size))
                                }

                                UUID.fromString("437fcdb7-74c7-4968-a669-384aa06f20c1") -> {
                                    i.setMaxMoist(String(buffer, 0, buffer.size))
                                }

                                // End of Settings

                                UUID.fromString("e41a3376-0c2a-4366-bf6e-43e3b59ab962") -> { // Soil Moisture
                                    i.setMoist(String(buffer, 0, buffer.size).toDouble())
                                }

                                UUID.fromString("dd72366c-d8a0-4c29-9943-30234819a3a2") -> { // Light
                                    i.setAmbientLight(String(buffer, 0, buffer.size).toLong())
                                }
                            }
                            break // Don't look through any other services if already found and changed the one
                        }
                    }
                }

                Log.w("CHARACTERISTIC READ VALUE", String(buffer, 0, buffer.size))
            } else {
                Log.e("CHARACTERISTIC READ ERROR", "Error reading characteristic: ${characteristic.uuid} from server ${characteristic.service.uuid}")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic, //Characteristic that has been updated as a result of a remote notification event. This value cannot be null.
            value: ByteArray // notified characteristic value This value cannot be null.
        ) {
            val buffer: ByteArray = value

            // If hub, don't search peripherals
            if (characteristic.service.uuid == UUID.fromString("eb9782b0-44a6-4799-873d-1e7580893e40")) { // UUID for hub
                when (characteristic.uuid) {
                    UUID.fromString("7ccecf3a-cb17-4f62-b22d-671639009fc8") -> { // Temperature
                        hubDevice.value.setTemp(String(buffer, 0, buffer.size).toDouble())
                    }

                    UUID.fromString("75171ef4-4fc5-4fd4-a393-8f4cc2f9fbcd") -> { // Humidity
                        hubDevice.value.setHumid(String(buffer, 0, buffer.size).toDouble())
                    }
                }
            } else {
                for (i in plantDevices) {
                    if (i.device == characteristic.service) {
                        when (characteristic.uuid) {
                            // Settings characteristics

                            UUID.fromString("b761e2e9-fac9-439c-a321-123d7f404e36") -> {
                                i.setName(String(buffer, 0, buffer.size))
                            }

                            UUID.fromString("39aec0bb-21c9-4519-8e32-e25c7523fde9") -> {
                                i.setMinMoist(String(buffer, 0, buffer.size))
                            }

                            UUID.fromString("437fcdb7-74c7-4968-a669-384aa06f20c1") -> {
                                i.setMaxMoist(String(buffer, 0, buffer.size))
                            }

                            // End of Settings

                            UUID.fromString("e41a3376-0c2a-4366-bf6e-43e3b59ab962") -> { // Soil Moisture
                                i.setMoist(String(buffer, 0, buffer.size).toDouble())
                            }

                            UUID.fromString("dd72366c-d8a0-4c29-9943-30234819a3a2") -> { // Light
                                i.setAmbientLight(String(buffer, 0, buffer.size).toLong())
                            }
                        }
                        break // Don't look through any other services if already found and changed the one
                    }
                }
            }

            Log.w("CHARACTERISTIC CHANGED VALUE", String(buffer, 0, buffer.size))
        }

        override fun onServicesDiscovered(
            gatt: BluetoothGatt,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                plantDevices.clear() // Clear any old services to link to any new
                hubDevice.value = HubDevice("00:00:00:00:00:00", "Hub Device", null) // Clear hub as well

                gatt.services.forEach { service ->
                    if (service.uuid == UUID.fromString("eb9782b0-44a6-4799-873d-1e7580893e40")) { // Change to service for hub
                        hubDevice.value = HubDevice("00:00:00:00:00:00", "Hub Device", service)
                    } else if (service.uuid != UUID.fromString("eb9782b0-44a6-4799-873d-1e7580893e40") && service.uuid != UUID.fromString("00001800-0000-1000-8000-00805f9b34fb") && service.uuid != UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")) {
                        plantDevices.add(PlantDevice("00:00:00:00:00:00", "Plant Name", "1", "10", service))
                    }
                }

                viewModel?.setCharacteristicNotification()
            }
        }

        // Othercallbacks like onCharacteristicRead, onCharacteristicWrite, etc.
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