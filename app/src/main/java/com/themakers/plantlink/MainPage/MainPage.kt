package com.themakers.plantlink.MainPage

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
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

var handler: Handler = Handler(Looper.getMainLooper())
var runnable: Runnable? = null
var loopTime: Long = 750 // Faster than arduino so we don't get old information

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MainPage(
    context: Context,
    navController: NavHostController,
    viewModel: BluetoothViewModel,
    plantViewModel: PlantDataViewModel
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
                    onClick = {},
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
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.background
                    ),
                    selected = false,
                    onClick = {
                        stopReadingSensorLoop()
                        navController.navigate("history")
                    },
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
                    .height(500.dp)
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
            item {
                Card (
                    shape = MaterialTheme.shapes.medium,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_device_thermostat_24),
                            contentDescription = "Temperature",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text(
                            text = "Temperature",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp
                        )
                        Text(
                            text = plantViewModel.temperatureF.toString() + "° F ",//"74° F ",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 30.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
                
                Card (
                    shape = MaterialTheme.shapes.medium,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sharp_humidity_percentage_24),
                            contentDescription = "Humidity",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text(
                            text = "Humidity",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp
                        )
                        Text(
                            text = plantViewModel.humidity.toString() + " RH ",//"34.7 RH ",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 30.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                Card (
                    shape = MaterialTheme.shapes.medium,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_water_drop_24),
                            contentDescription = "Soil Moisture",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text(
                            text = "Soil Moisture",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp
                        )
                        Text(
                            text = plantViewModel.moisture.toString() + " %",//"880 ",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }
    }
}