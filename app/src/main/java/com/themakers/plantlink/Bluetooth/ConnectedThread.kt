package com.themakers.plantlink.Bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log
import com.themakers.plantlink.PlantDataViewModel
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ConnectedThread(_socket: BluetoothSocket, private var plantViewModel: PlantDataViewModel): Thread() {
    private val TAG: String = "PlantLinkLog"
    private var socket: BluetoothSocket? = null
    private var inStream: InputStream? = null
    private var outStream: OutputStream? = null


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

    fun read() {
        val buffer = ByteArray(256)
        var bytes = 0 // bytes returned from read()
        var readingNumber = 1 // 1 = Temp, 2 = Humidity, 3 = Moisture

        // Keep listening to the inputstream until exception occurs
        // We just want to get 1 reading from arduino

        while (readingNumber <= 3) {
            try {
                buffer[bytes] = inStream!!.read().toByte()
                var readMessage: String

                // If I detect a newline means I already read a full measurement

                if (buffer[bytes].toInt().toChar() == '~') { // POSSIBLE OPTIMIZATION: Only get data from bluetooth, then format/rip-apart data in phone
                    //Log.e(TAG, String(buffer, 0, bytes))
                    readMessage = String(buffer, 0, bytes)
                    //Log.e(TAG, readMessage)
                    // Value to be read by the Observer streamed by the Observable
                    //Log.e(TAG, readMessage.toFloat().toString())

                    if (readingNumber == 1) {
                        plantViewModel.setTemp(readMessage.toDouble())
                    } else if (readingNumber == 2) {
                        plantViewModel.setHumid(readMessage.toDouble())
                    } else if (readingNumber == 3) {
                        plantViewModel.setMoist(readMessage.toDouble())
                    }

                    bytes = 0
                    readingNumber++
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

            Log.e(TAG, "SOCKET CLOSED")
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
    }
}