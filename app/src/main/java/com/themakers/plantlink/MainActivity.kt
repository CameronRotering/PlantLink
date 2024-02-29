package com.themakers.plantlink

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.themakers.plantlink.Bluetooth.BluetoothViewModel
import com.themakers.plantlink.HistoryPage.HistoryPage
import com.themakers.plantlink.MainPage.MainPage
import com.themakers.plantlink.SettingsPage.SettingsPage
import com.themakers.plantlink.data.AndroidBluetoothController
import com.themakers.plantlink.ui.theme.PlantLInkTheme
import java.util.UUID

class MainActivity : ComponentActivity() {

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true


    private val TAG: String = "PlantLinkLogs"
    private val REQUEST_ENABLE_BT: Int = 1
    // We will use handler to get the BT connection status
    val handler: Handler? = null
    private val ERROR_READ: Int = 0
    private val arduinoBTModule: BluetoothDevice? = null
    private val arduinoUUID: UUID = UUID.randomUUID()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                )
        )

        setContent {

            // Instances of BT manager and BT adapter needed to work with BT in android
            val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
            var bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter

            PlantLInkTheme {
                val viewModel = BluetoothViewModel(AndroidBluetoothController(applicationContext))
                val state by viewModel.state.collectAsState()
                

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
                                navController = navController
                            )
                        }

                        composable("Settings") {
                            SettingsPage(
                                navController = navController,
                                context = applicationContext
                            )
                        }

                        composable("History") {
                            HistoryPage(
                                navController = navController,
                                context = applicationContext
                            )
                        }

                        composable("BluetoothConnect") {
                            BluetoothConnectScreen(
                                state = state,
                                onStartScan = viewModel::startScan,
                                onStopScan = viewModel::stopScan,
                                context = applicationContext,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}