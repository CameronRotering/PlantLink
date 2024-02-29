package com.themakers.plantlink.Bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ConnectedThread(_socket: BluetoothSocket): Thread() {
    private val TAG: String = "Log"
    private var socket: BluetoothSocket? = null
    private var inStream: InputStream? = null
    private var outStream: OutputStream? = null
    private var valueRead: String? = null


    init {
        socket = _socket
        var tmpIn: InputStream? = null
        var tmpOut: OutputStream? = null

        // Get the input and output streams
        try {
            tmpIn = socket!!.inputStream
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred when creating input stream", e)
        }

        try {
            tmpOut = socket!!.outputStream
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred when creating output stream", e)
        }

        inStream = tmpIn
        outStream = tmpOut
    }

    fun getValueRead(): String? {
        return valueRead
    }

    fun read() {
        val buffer = ByteArray(256)
        var bytes = 0 // bytes returned from read()
        var numberOfReadings = 0 // to control the number of reading from Arduino

        // Keep listening to the inputstream until exception occurs
        // We just want to get 1 reading from arduino

        while (numberOfReadings < 1) {
            try {
                buffer[bytes] = inStream!!.read().toByte()
                var readMessage: String

                // If I detect a newline means I already read a full measurement
                if (buffer[bytes].toInt().toChar() == '\n') {
                    readMessage = String(buffer, 0, bytes)
                    Log.e(TAG, readMessage)
                    // Value to be read by the Observer streamed by the Observable
                    valueRead = readMessage
                    bytes = 0
                    numberOfReadings++
                } else {
                    bytes++
                }
            } catch (e: IOException) {
                Log.d(TAG, "Input stream was disconnected", e)
                break
            }
        }
    }

    fun cancel() {
        try {
            socket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
    }
}