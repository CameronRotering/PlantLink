package com.themakers.plantlink.data

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class BluetoothLeService : Service() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private val binder = LocalBinder()

    fun setAdapter(bluetoothAdapter: BluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : Binder(), IBinder {
        fun getService() : BluetoothLeService {
            return this@BluetoothLeService
        }
    }
}