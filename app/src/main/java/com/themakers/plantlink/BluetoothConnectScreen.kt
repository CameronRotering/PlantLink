package com.themakers.plantlink

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.themakers.plantlink.Bluetooth.BluetoothDevice
import com.themakers.plantlink.Bluetooth.BluetoothUiState
import com.themakers.plantlink.Bluetooth.BluetoothViewModel
import com.themakers.plantlink.Bluetooth.ConnectThread
import com.themakers.plantlink.Bluetooth.ConnectedThread



fun connectBluetoothDevice(context: Context, viewModel: BluetoothViewModel, plantViewModel: PlantDataViewModel, connectBluetooth: ConnectThread) {
    if (connectBluetooth.mSocket == null) { // Not connected
        connectBluetooth.setThread(viewModel.btModule!!, viewModel.uuid, context)
        //connectBluetooth = ConnectThread(viewModel.btModule!!, viewModel.uuid, context)
        connectBluetooth.run()
    }

    if (connectBluetooth.getSocket()?.isConnected == true) {
        if (viewModel.connectedThread == null) {
            viewModel.setThread(ConnectedThread(connectBluetooth.getSocket()!!, plantViewModel))
        }
    }
}


@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothConnectScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    context: Context,
    navController: NavHostController,
    viewModel: BluetoothViewModel,
    plantViewModel: PlantDataViewModel
) {
    val lazyListState = rememberLazyListState()

    val connectBluetooth = ConnectThread()

    Scaffold(
        //backgroundColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                title = {
                    Text(
                        text = "Bluetooth",
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 50.dp, 0.dp),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Bluetooth",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_bluetooth_24),
                            contentDescription = "Bluetooth",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(226, 114, 91, 255),//MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedIconColor = Color(0, 0, 0, 255),
                        indicatorColor = MaterialTheme.colorScheme.background
                    ),
                    selected = false,
                    onClick = {
                        navController.navigate("Home")
                    },
                    label = {
                        Text(
                            text = "Home",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home"
                        )
                    }
                )
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedIconColor = Color(0, 0, 0, 255),
                        indicatorColor = MaterialTheme.colorScheme.background
                    ),
                    selected = false,
                    onClick = {
                        navController.navigate("Settings")
                    },
                    label = {
                        Text(
                            text = "Settings",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }
                )
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedIconColor = Color(0, 0, 0, 255),
                        indicatorColor = MaterialTheme.colorScheme.background
                    ),
                    selected = true,
                    onClick = {},
                    label = {
                        Text(
                            text = "History",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_bar_chart_24),
                            contentDescription = "Bar Chart"
                        )
                    }
                )
            }
        }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
                .padding(padding)
        ) {
            Icon(
                painter = painterResource(R.drawable.sharp_psychiatry_24),
                contentDescription = "Big Plant",
                tint = Color(61, 168, 44),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            )
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = onStartScan) {
                    Text(text = "Start Scan")
                }

                Button(onClick = onStopScan) {
                    Text(text = "Stop Scan")
                }
            }

            BluetoothDeviceList(
                pairedDevices = state.pairedDevices,
                scannedDevices = state.scannedDevices,
                onClick = {device -> // When a bluetooth device is selected

                    viewModel.stopScan() // Recommended to not be scanning while connecting

                    if (device.name!!.substring(0, 9).lowercase() == "plantlink") { // Invites possibilities of "PlantLink310" Working
                        Log.e("Log", "PlantLink Clicked!")

                        viewModel.setUUID(device.device!!.uuids[0].uuid)
                        viewModel.setModule(device.device)

                        connectBluetoothDevice(context, viewModel, plantViewModel, connectBluetooth)

                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                lazyListState = lazyListState
            )
        }
    }
}


@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        item {
            Text(
                text = "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        items(pairedDevices) { device ->
            Text(
                text = device.name ?: device.address!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }


        item {
            Text(
                text = "Scanned Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        items(scannedDevices) { device ->
            Text(
                text = device.name ?: device.address!!,//"(No Name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )

        }
    }
}