package com.themakers.plantlink.Bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import okio.IOException
import java.util.UUID


@SuppressLint("MissingPermission")
class ConnectThread(device: BluetoothDevice, MY_UUID: UUID, context: Context): Thread() {
    var mSocket: BluetoothSocket? = null
    private val TAG: String = "Log"

    init {
        var bluetoothTmp: BluetoothSocket? = null


        try {
            // Get a Socket to connect with the given BluetoothDevice
            // MY_UUID is the app's UUID string, also used in the server code
            bluetoothTmp = device.createRfcommSocketToServiceRecord(MY_UUID)
        } catch (e: IOException) {
            Log.e(TAG, "Socket's create() method failed", e)
        }
        mSocket = bluetoothTmp
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        try {
            // Connect to the remote device through the socket
            // Call blocks until it succeeds or throws exception
            mSocket?.connect()
        } catch (connectException: IOException) {
            // Unable to connect; close socket and return
            //handler?.obtainMessage(0, "Unable to connect to the BT device")
            Log.e(TAG, "connectException: $connectException")

            try {
                mSocket?.close()
            } catch (closeException: IOException) {
                Log.e(TAG, "Could not close the client socket", closeException)
            }
        }

        // Connection attempt succeeded
    }

    // Closes the client socket and causes thread to finish
    fun cancel() {
        try {
            mSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }

    fun getSocket(): BluetoothSocket? {
        return mSocket
    }
}