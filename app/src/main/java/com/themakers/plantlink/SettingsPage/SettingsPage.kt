package com.themakers.plantlink.SettingsPage

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.themakers.plantlink.R
import com.themakers.plantlink.data.SettingEvent
import com.themakers.plantlink.data.SettingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    context: Context,
    navController: NavHostController,
    state: SettingState,
    onEvent: (SettingEvent) -> Unit
) {
    val lazyListState = rememberLazyListState()
    var isFahrenheit = state.isFahrenheit

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
                        text = "Settings",
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
                    selected = true,
                    onClick = {},
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
                //NavigationBarItem(
                //    colors = NavigationBarItemDefaults.colors(
                //        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                //        selectedIconColor = Color(0, 0, 0, 255),
                //        indicatorColor = MaterialTheme.colorScheme.background
                //    ),
                //    selected = false,
                //    onClick = {
                //        navController.navigate("History")
                //    },
                //    label = {
                //        Text(
                //            text = "History",
                //            color = MaterialTheme.colorScheme.secondary,
                //            fontSize = 15.sp
                //        )
                //    },
                //    icon = {
                //        Icon(
                //            painter = painterResource(R.drawable.baseline_bar_chart_24),
                //            contentDescription = "Bar Chart"
                //        )
                //    }
                //)
            }
        }
    ) { padding ->
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
            contentPadding = padding,
            state = lazyListState
        ) {
            item {
                Card (
                    shape = MaterialTheme.shapes.medium,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Temperature Unit",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            modifier = Modifier.fillMaxSize(),
                            fontWeight = FontWeight.SemiBold
                        )
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        )
                        { // Can probably change the button info (color) to mutable or have ternary operator on when their color is what but also opens up to maybe 2 at once
                            if (isFahrenheit) {
                                Button(
                                    onClick = { // Click C button
                                        isFahrenheit = false
                                        onEvent(SettingEvent.SetTempUnit(false))
                                    },
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .width(65.dp)
                                        .height(65.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(220, 220, 220),
                                        contentColor = Color(0, 0, 0),
                                    )
                                ) {
                                    Text(
                                        text = "C",
                                        fontSize = 30.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.padding(30.dp))

                                Button(
                                    onClick = { }, // Cick F when already set to F
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .width(65.dp)
                                        .height(65.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.secondary,

                                    )
                                ) {
                                    Text(
                                        text = "F",
                                        fontSize = 30.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { }, // Click c when already set to C
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .width(65.dp)
                                        .height(65.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text(
                                        text = "C",
                                        fontSize = 30.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.padding(30.dp))

                                Button(
                                    onClick = { // Click F when C is enabled
                                        isFahrenheit = true
                                        onEvent(SettingEvent.SetTempUnit(true))
                                    },
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .width(65.dp)
                                        .height(65.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(220, 220, 220),
                                            contentColor = Color(0, 0, 0)
                                        )
                                ) {
                                    Text(
                                        text = "F",
                                        fontSize = 30.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}