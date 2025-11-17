package com.themakers.plantlink

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import com.themakers.plantlink.composables.BottomToolBar


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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                title = {
                    Text(
                        text = "Bluetooth",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        },
        bottomBar = { BottomToolBar(navController = navController) },
        contentWindowInsets = WindowInsets.safeContent // Safe content so no content is hidden under system things like camera AND is interactive
    ) { padding ->
        Column(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyRow (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                item {
                    Button(
                        onClick = onStartScan,
                        modifier = Modifier
                            .padding(15.dp),
                        //.height(65.dp)
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(220, 220, 220),
                            contentColor = Color(0, 0, 0),
                        )
                    ) {
                        Text(
                            text = "Start Scan",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Button(
                        onClick = onStopScan,
                        modifier = Modifier
                            .padding(15.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(220, 220, 220),
                            contentColor = Color(0, 0, 0),
                        )
                    ) {
                        Text(
                            text = "Stop Scan",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            BluetoothDeviceList(
                pairedDevices = state.pairedDevices,
                scannedDevices = state.scannedDevices,
                onClick = {device -> // When a bluetooth device is selected
                    if (device.name != null && device.name.length >= 9 && device.name.substring(0, 9).lowercase() == "plantlink") { // Invites possibilities of "PlantLink310" Working
                        if (device.device != viewModel.btModule) {// && device.device!!.uuids[0].uuid != viewModel.uuid) { // If connecting to different device or first device to connect to
                            viewModel.stopScan() // Recommended to not be scanning while connecting    Scanning is battery intensive

                            state.isConnected = 1


                            // Update: Thread might be unnecessary due to fixing of the connection status and forcing user to wait for full connection
                            // Perform Bluetooth connection in a background thread so Bluetooth still connects when switching pages
                            //Thread {
                                viewModel.setGatt(
                                    context = context,
                                    device = device.device!!,
                                    autoConnect = false
                                )
                            //}.start()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Not a PlantLink device.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                lazyListState = lazyListState
            )

            // Using a regular row brings in loading circle from left while
            // lazy row brings in loading circle from top
            // (added in specific animation details to only come in from top)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                item {
                    LoadingAnimation(isScanning = state.isScanning)
                }

            }

//            LazyRow(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceAround
//            ) {
//                item {
//                    LoadingAnimation(isScanning = state.isConnected, color = Color(255, 0, 0))
//                }
//
//            }
        }


        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                item {
                    LoadingAnimation(isConnected = state.isConnected, color = Color(255, 0, 0))
                }

            }
        }
    }

    // TODO: Every time entering the page, it re checks this. If already connected and go back to this page, restates "connected to device"

    LaunchedEffect(state.isConnected) {

        if (state.isConnected == 2) {
            Toast.makeText(
                context,
                "Connected to PlantLink device.",
                Toast.LENGTH_LONG
            ).show()
        }
        // When page is opened it detects as 0 and says unable to connect
        // and when connecting to a different device while already connected, this might say unable to connect.
//        else if (state.isConnected == 0) {
//            Toast.makeText(
//                context,
//                "Unable to connect to PlantLink device.",
//                Toast.LENGTH_LONG
//            ).show()
//        }

        Log.w("BT CONNECT STATE CHANGE", state.isConnected.toString())
    }
}

@Composable
fun LoadingAnimation(modifier: Modifier = Modifier, isScanning: Boolean, color: Color = Color(0, 255, 0)) {
    AnimatedVisibility(
        visible = isScanning,
        enter = expandVertically(expandFrom = Alignment.Top),
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            label = "rotation",
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = modifier
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 5.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation),
                    color = color
                )
            }
        }
    }
}

@Composable
fun LoadingAnimation(modifier: Modifier = Modifier, isConnected: Int, color: Color = Color(0, 255, 0)) {
    AnimatedVisibility(
        visible = isConnected == 1,
        enter = expandVertically(expandFrom = Alignment.Top),
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            label = "rotation",
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = modifier
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 5.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation),
                    color = color
                )
            }
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
            if (device.name != null && device.name.length >= 9 && device.name.substring(0, 9).lowercase() == "plantlink") {
                Text(
                    text = device.name,// ?:  "(No Name)",//device.address!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(device) }
                        .padding(16.dp)
                )
            }
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
            if (device.name != null && device.name.length >= 9 && device.name.substring(0, 9).lowercase() == "plantlink") {
                Text(
                    text = device.name,// ?: "(No Name)",//device.address!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(device) }
                        .padding(16.dp)
                )
            }
        }
    }
}