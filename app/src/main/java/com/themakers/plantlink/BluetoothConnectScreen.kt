package com.themakers.plantlink

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.themakers.plantlink.Bluetooth.BluetoothDevice
import com.themakers.plantlink.Bluetooth.BluetoothDeviceCard
import com.themakers.plantlink.Bluetooth.BluetoothUiState
import com.themakers.plantlink.Bluetooth.BluetoothViewModel
import com.themakers.plantlink.composables.BottomToolBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


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
    val scaleAnimatable =  remember { Animatable(1f) }


    LaunchedEffect(state.isScanning) {
        if (state.isScanning) {
            scaleAnimatable.snapTo(1f)

            // Start the infinite loop
            scaleAnimatable.animateTo(
                targetValue = 0.35f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        2000,
                        easing = EaseInOut
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            // Stop/Reset the animation when not scanning
            scaleAnimatable.snapTo(1f)
        }
    }

    LaunchedEffect(Unit) {
        onStartScan()
        delay(15.seconds)
        onStopScan()
    }

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
                    .graphicsLayer {
                        val currentScale = scaleAnimatable.value

                        scaleX = currentScale
                        scaleY = currentScale
                        this.alpha = alpha
                        transformOrigin = TransformOrigin(0.5f, 1f) // Grow from bottom
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                lazyListState = lazyListState,
                onStartScan = onStartScan,
                onStopScan = onStopScan,
                state = state
            )

            // Using a regular row brings in loading circle from left while
            // lazy row brings in loading circle from top
            // (added in specific animation details to only come in from top)
//            LazyRow(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceAround
//            ) {
//                item {
//                    LoadingAnimation(isScanning = state.isScanning)
//                }
//
//            }

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
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(isConnected = state.isConnected, color = Color(255, 0, 0))
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
fun LoadingAnimation(modifier: Modifier = Modifier, isScanning: Boolean, containerColor: Color = Color.Transparent, color: Color = Color(0, 255, 0)) {
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
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawCircle(
                        color = containerColor,
                        radius = size.minDimension / 2
                    )


                }

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
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    state: BluetoothUiState,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = state.isScanning,
        onRefresh = {
            coroutineScope.launch {
                onStartScan()
                delay(15.seconds)
                onStopScan()
            }

        },
        modifier = Modifier
            .fillMaxSize(),
        state = pullToRefreshState,
        //contentAlignment = Alignment.Center,

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = lazyListState
        ) {
            items(scannedDevices) { device ->
                if (device.name != null) {// && device.name.length >= 9 && device.name.substring(0, 9).lowercase() == "plantlink") {
                    BluetoothDeviceCard(
                        device = device,
                        onClick = onClick
                    )
                }
            }
        }
    }
}