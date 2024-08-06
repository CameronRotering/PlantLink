package com.themakers.plantlink.MainPage

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.themakers.plantlink.Bluetooth.BluetoothViewModel
import com.themakers.plantlink.PlantDataViewModel
import com.themakers.plantlink.R
import com.themakers.plantlink.SettingsPage.CurrClickedPlantViewModel
import com.themakers.plantlink.data.PlantDevice
import com.themakers.plantlink.data.SettingEvent
import com.themakers.plantlink.data.SettingState

var handler: Handler = Handler(Looper.getMainLooper())
var runnable: Runnable? = null
var loopTime: Long = 750 // Faster than arduino so we don't get old information
var deviceBoxPadding = PaddingValues(10.dp) // Was 30 dp for non-block version

//val plantDeviceList: MutableList<PlantDevice> = mutableListOf()
val plantDeviceList = mutableListOf<PlantDevice>(
    PlantDevice(0, "00:00:00:00:00:00", "Galaxy Petunia", "1", "10"),
    PlantDevice(1, "00:00:00:00:00:00", "Easter Cactus", "2", "20"),
    PlantDevice(2, "00:00:00:00:00:00", "Acoma Crape Myrtle", "3", "30"),
    PlantDevice(3, "00:00:00:00:00:00", "Katsura Tree", "4", "40"),
    PlantDevice(4, "00:00:00:00:00:00", "Panda Plant", "5", "50"),
)


fun readSensors(viewModel: BluetoothViewModel) {
    if (viewModel.isConnected()) {
        viewModel.connectedThread?.read()
    }
}

fun startSensorLoop(viewModel: BluetoothViewModel, navController: NavHostController) {
    // (If on home page) and if not already running and connected to a socket
    // Home page check because when leaving home page though it stops, it likes to start right after.
    if ((navController.currentDestination!!.route == "Home") && runnable == null && viewModel.isConnected()) {
        Log.e("Sensor Info", "Starting sensor loop")

        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, loopTime)
            readSensors(viewModel)
        }.also { runnable = it }, loopTime)
    }
}

fun stopReadingSensorLoop() {
    if (runnable != null) {
        Log.e("Sensor Info", "Stopped sensor loop")

        handler.removeCallbacks(runnable!!)

        runnable = null // Only run if it isn't null, slight performance gain
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    context: Context,
    navController: NavHostController,
    viewModel: BluetoothViewModel,
    plantViewModel: PlantDataViewModel,
    state: SettingState,
    onEvent: (SettingEvent) -> Unit,
    clickedPlantViewModel: CurrClickedPlantViewModel
) {
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                title = {
                    Text(
                        text = "Home",
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
                            stopReadingSensorLoop()

                            navController.navigate("BluetoothConnect")
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
                    selected = true,
                    onClick = {
                        viewModel.connectedThread?.sendData()
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
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.background
                    ),
                    selected = false,
                    onClick = {
                        stopReadingSensorLoop()
                        navController.navigate("settings")
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
            }
        }
    ) { padding ->

        startSensorLoop(viewModel, navController)

        Column (
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.sharp_psychiatry_24),
                contentDescription = "Big Plant",
                tint = Color(61, 168, 44),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = padding,
            state = lazyListState
        ) {
            val chunkedDevices = plantDeviceList.chunked(2)

            items(plantDeviceList.chunked(2).size) { rowIndex -> // Figure out how many rows we need (must convert to double and int)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val rowItems = chunkedDevices[rowIndex]

                    for (device in rowItems) {
                        DeviceCard(
                            modifier = Modifier
                                .weight(1f)
                                .padding(deviceBoxPadding),
                            context = context,
                            navController = navController,
                            plantViewModel = plantViewModel,
                            plantDevice = device,
                            state = state,
                            onEvent = onEvent,
                            clickedPlantViewModel = clickedPlantViewModel
                        )

                        if (rowItems.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}