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

    fun sendData() {
        val delim: Byte = 126

        var dataToSend: ByteArray
                                                           // Next command                       Next command                             Next command
        dataToSend = byteArrayOf(48, delim, 48, delim, 70, delim, 48, delim, 49, delim, 72, 105, delim, 48, delim, 50, delim, 53, 48, 48, delim, 49, delim, 51, delim, 54, 49, 49)

        // Can add to byte array just by using +    (https://stackoverflow.com/questions/55250751/how-to-append-bytes-to-bytearray-in-kotlin)
        // For Example:
        // dataTosend += 50


        // 0, 2, 500
        outStream!!.write(dataToSend)
    }

    fun read() {
        val buffer = ByteArray(256)
        var bytes = 0 // bytes returned from read()
        var readingNumber = 1 // 1 = Temp, 2 = Humidity, 3 = Moisture, 4 = Light

        // Keep listening to the inputstream until exception occurs
        // We just want to get 1 reading from arduino

        while (readingNumber <= 4) {
            try {
                buffer[bytes] = inStream!!.read().toByte()
                var readMessage: String

                // If doing multiple devices, they could be separated by {} for each device and hold a list of devices.

                // If I detect a newline means I already read a full measurement
                if (buffer[bytes].toInt().toChar() == '~') { // POSSIBLE OPTIMIZATION: Only get data from bluetooth, then format/rip-apart data in phone
                    //Log.e(TAG, String(buffer, 0, bytes))
                    readMessage = String(buffer, 0, bytes)
                    //Log.e(TAG, readMessage)
                    // Value to be read by the Observer streamed by the Observable
                    //Log.e(TAG, readMessage.toFloat().toString())

                    when (readingNumber) {
                        1 -> {
                            plantViewModel.setTemp(readMessage.toDouble())
                        }
                        2 -> {
                            plantViewModel.setHumid(readMessage.toDouble())
                        }
                        3 -> {
                            plantViewModel.setMoist(readMessage.toDouble())
                        }
                        4 -> {
                            plantViewModel.setLight(readMessage.toDouble())
                        }
                    }

                    bytes = 0
                    readingNumber++
                } else if (buffer[bytes].toInt().toChar() == ';') { // Can make it either another device and separate or it can be multiple sensors like (75,43,780;404;395) {1 temp and humidity sensor, 3 moisture sensors}

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