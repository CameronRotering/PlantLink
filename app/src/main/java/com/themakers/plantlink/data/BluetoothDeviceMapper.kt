package com.themakers.plantlink.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.themakers.plantlink.Bluetooth.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address,
        //uuid = uuids[0].uuid,
        device = this
    )
}