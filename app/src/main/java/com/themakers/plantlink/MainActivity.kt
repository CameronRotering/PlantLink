package com.themakers.plantlink

import android.Manifest
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.themakers.plantlink.Bluetooth.BluetoothViewModel
import com.themakers.plantlink.HistoryPage.HistoryPage
import com.themakers.plantlink.MainPage.MainPage
import com.themakers.plantlink.SettingsPage.CurrClickedPlantViewModel
import com.themakers.plantlink.SettingsPage.PlantSettingsPage
import com.themakers.plantlink.SettingsPage.SettingsPage
import com.themakers.plantlink.data.AndroidBluetoothController
import com.themakers.plantlink.data.SettingsDatabase
import com.themakers.plantlink.ui.theme.PlantLInkTheme
import java.io.IOException
import java.io.UnsupportedEncodingException
import kotlin.experimental.and

fun activateNfc(myTag: Tag, context: Context) {
    try {
        if (myTag == null) {
            Toast.makeText(
                context,
                "No NFC Tag Detected",
                Toast.LENGTH_LONG
            ).show()
        } else {
            //write(text, myTag)
            Toast.makeText(
                context,
                "Write Success",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: IOException) {
        Toast.makeText(
            context,
            "Write Error",
            Toast.LENGTH_LONG
        ).show()
    } catch (e: FormatException) {
        Toast.makeText(
            context,
            "Write Error",
            Toast.LENGTH_LONG
        ).show()
    }
}

class MainActivity : ComponentActivity() {



    var pendingIntent: PendingIntent? = null
    var writeTagFilters: Array<IntentFilter?>? = null
    var writeMode = false
    var myTag: Tag? = null
    var nfcText = ""


    private val nfcManager by lazy {
        applicationContext.getSystemService(NfcManager::class.java)
    }
    private val nfcAdapter by lazy {
        nfcManager.defaultAdapter
    }


    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    var viewModel: BluetoothViewModel? = null

    private val settingsDb by lazy {
        Room.databaseBuilder(
            applicationContext,
            SettingsDatabase::class.java,
            "settings.db"
        ).build()
    }

    private val settingsViewModel by viewModels<SettingsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(settingsDb.settingsDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (nfcAdapter == null) {

            Toast.makeText(
                applicationContext,
                "This device does not support NFC.",
                Toast.LENGTH_LONG
            ).show()
            //finish()
        }

        //readFromIntent(intent)
        pendingIntent = PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE) // was 0
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
        writeTagFilters = arrayOf(tagDetected)


        val plantViewModel = PlantDataViewModel()


        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */}

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true


            if (canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }


        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.NFC
                )
        )

        setContent {

            // Instances of BT manager and BT adapter needed to work with BT in android // Disable because when committing it said bluetoothAdapter never used and it was set above
            //val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
            //var bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter

            PlantLInkTheme {
                viewModel = BluetoothViewModel(AndroidBluetoothController(applicationContext))
                val state by viewModel!!.state.collectAsState()

                val settingsState by settingsViewModel.state.collectAsState()

                val selectedPlantViewModel = CurrClickedPlantViewModel()



                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "Home"
                    ) {
                        composable("Home") {
                            MainPage(
                                context = applicationContext,
                                navController = navController,
                                viewModel = viewModel!!,
                                plantViewModel = plantViewModel,
                                state = settingsState,
                                onEvent = settingsViewModel::onEvent,
                                clickedPlantViewModel = selectedPlantViewModel
                            )
                        }

                        composable("Settings") {
                            SettingsPage(
                                navController = navController,
                                context = applicationContext,
                                state = settingsState,
                                onEvent = settingsViewModel::onEvent
                            )
                        }

                        composable("PlantLinkSettings") {
                            PlantSettingsPage(
                                navController = navController,
                                context = applicationContext,
                                plantViewModel = selectedPlantViewModel
                            )
                        }

                        composable("HistoryPage") {
                            HistoryPage(
                                navController = navController,
                                context = applicationContext,
                                plantViewModel = selectedPlantViewModel,
                                state = settingsState
                            )
                        }

                        composable("BluetoothConnect") {
                            BluetoothConnectScreen(
                                state = state,
                                onStartScan = viewModel!!::startScan,
                                onStopScan = viewModel!!::stopScan,
                                context = applicationContext,
                                navController = navController,
                                viewModel = viewModel!!,
                                plantViewModel = plantViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStop() { // When out of application, disconnect from bluetooth (Just in case they keep app on for a long time). Might not even be doing but be safe
        viewModel?.connectedThread?.cancel()
        super.onStop()
    }

    private fun readFromIntent(intent: Intent, nfcTextObject: String) {
        val action = intent.action

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
            || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
            || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            var msgs: Array<NdefMessage?>? = null

            if (rawMsgs != null) {
                msgs = arrayOfNulls<NdefMessage>(rawMsgs.size)


                for (i in 0..rawMsgs.size) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }
            }
            buildTagViews(msgs, nfcTextObject)
        }
    }

    private fun buildTagViews(msgs: Array<NdefMessage?>?, nfcTextObject: String) {
        if (msgs.isNullOrEmpty()) return

        var text = ""
        val payload = msgs[0]!!.records[0].payload
        val textEncoding = if ((payload[0] and 0b10000000.toByte()).toInt() == 0) {
            "UTF-8"
        } else {
            "UTF-16"
        }
        val languageCodeLength: Int =
            (payload[0] and 0b00111111).toInt()// Get language code Ex: "en

        try {
            text = java.lang.String(
                payload,
                languageCodeLength + 1,
                payload.size - languageCodeLength - 1,
                textEncoding
            ).toString()
        } catch (e: UnsupportedEncodingException) {
            Log.e("UnsupportedEncoding", e.toString())
        }

        val nfcObject = text
    }

    @Throws(IOException::class, FormatException::class)
    private fun write(text: String, tag: Tag) {
        val records: Array<NdefRecord> = arrayOf(createRecord(text))
        val writeMessage = NdefMessage(records)
        // Get Ndef instance for tag
        val ndef = Ndef.get(tag)
        // Enabled I/O
        ndef.connect()
        // Write message
        ndef.writeNdefMessage(writeMessage)
        // Close connection
        ndef.close()
    }

    @Throws(UnsupportedEncodingException::class)
    private fun createRecord(text: String): NdefRecord {
        val lang = "en"
        val textBytes = text.toByteArray()
        val langBytes = lang.toByteArray()
        val textLength = textBytes.size
        val langLength = langBytes.size
        val payload = ByteArray(1 + langLength + textLength)

        // Set status byte
        payload[0] = langLength.toByte()

        // Copy langBytes and textBytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength)
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength)

        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
    }

    @Override
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readFromIntent(intent, nfcText)
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.action)) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
    }

    @Override
    override fun onPause() {
        super.onPause()
        if (nfcAdapter != null) {
            WriteModeOff()
        }
    }

    @Override
    override fun onResume() {
        super.onResume()
        if (nfcAdapter != null) {
            WriteModeOn()
        }
    }

    private fun WriteModeOn() {
        writeMode = true
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null)
    }

    private fun WriteModeOff() {
        writeMode = false
        nfcAdapter.disableReaderMode(this)
    }
}