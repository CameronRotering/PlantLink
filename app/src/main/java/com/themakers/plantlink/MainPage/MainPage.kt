package com.themakers.plantlink.MainPage

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.themakers.plantlink.PlantDataViewModel
import com.themakers.plantlink.R
import com.themakers.plantlink.SettingsPage.CurrClickedPlantViewModel
import com.themakers.plantlink.data.HubDevice
import com.themakers.plantlink.data.PlantDevice
import com.themakers.plantlink.data.SettingEvent
import com.themakers.plantlink.data.SettingState
import kotlinx.coroutines.delay

var deviceBoxPadding = PaddingValues(10.dp) // Was 30 dp for non-block version


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    destinationName: String,
    context: Context,
    navController: NavHostController,
    plantViewModel: PlantDataViewModel,
    state: SettingState,
    onEvent: (SettingEvent) -> Unit,
    clickedPlantViewModel: CurrClickedPlantViewModel,
    plantDeviceList: MutableList<PlantDevice>,
    hubDevice: MutableState<HubDevice>
) {
    val lazyGridState = rememberLazyGridState()
    var health by remember { mutableStateOf(0) }

    LaunchedEffect(true) {
        while (true) {
            delay(10)
            health = (health + 1) % 101
        }
    }

    Scaffold(
//        modifier = Modifier // Creating gradient background of whole page.
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(listOf(Color(36, 164, 255), Color(148, 210, 255)))
//            ),
        //containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                title = {
                    Text(
                        text = "Home",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
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
                containerColor = Color(226, 114, 91, 255),
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
                            //fontSize = 15.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home",
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
                        navController.navigate("settings")
                    },
                    label = {
                        Text(
                            text = "Settings",
                            color = MaterialTheme.colorScheme.secondary,
                            //fontSize = 15.sp
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
        },
        contentWindowInsets = WindowInsets.safeContent // Safe content so no content is hidden under system things like camera AND is interactive
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize(),
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
                .fillMaxSize(), // Would adding padding to top and bottom let me not have on start and end item?
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(padding.calculateTopPadding()))

            LazyVerticalGrid(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Start,
                contentPadding = PaddingValues(bottom = padding.calculateBottomPadding()), // Only add padding to bottom to see any items that have to be scrolled to and not add padding to top
                state = lazyGridState,
                columns = GridCells.Adaptive(150.dp)
            ) {

                item (
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HubCard(
                            modifier = Modifier
                                .padding(deviceBoxPadding)
                                .fillMaxWidth(0.5f),
                            context = context,
                            navController = navController,
                            plantViewModel = plantViewModel,
                            hubDevice = hubDevice.value,
                            state = state,
                            onEvent = onEvent
                        )
                    }
                }

                items(plantDeviceList.size) { deviceIndex ->
                    DeviceCard(
                        modifier = Modifier
                            .padding(deviceBoxPadding),
                        context = context,
                        navController = navController,
                        plantViewModel = plantViewModel,
                        plantDevice = plantDeviceList[deviceIndex],
                        state = state,
                        onEvent = onEvent,
                        clickedPlantViewModel = clickedPlantViewModel
                    )
                }

                // TODO:  Enabling this, spams logcat. Need to check if having health change through bluetooth data will replicate this error.
                // Also happens with loading icon, I guess it might just be animated objects

//                item {
//                    DeviceCard(
//                        modifier = Modifier
//                            .padding(deviceBoxPadding),
//                        context = context,
//                        navController = navController,
//                        plantViewModel = plantViewModel,
//                        plantDevice = plantDeviceList[0],
//                        state = state,
//                        onEvent = onEvent,
//                        clickedPlantViewModel = clickedPlantViewModel,
//                        health = health
//                    )
//                }
            }
        }
    }
}