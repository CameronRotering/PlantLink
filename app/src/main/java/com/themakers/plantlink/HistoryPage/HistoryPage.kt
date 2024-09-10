package com.themakers.plantlink.HistoryPage

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Card
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults.cardColors
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import com.themakers.plantlink.R
import com.themakers.plantlink.SettingsPage.CurrClickedPlantViewModel
import com.themakers.plantlink.SimpleLineChart
import com.themakers.plantlink.data.SettingState

val tempOverTime: List<Point> =
    listOf(
        Point(0.0f, 83.45f),
        Point(1.0f, 91.78f),
        Point(2.0f, 67.92f),
        Point(3.0f, 105.31f),
        Point(4.0f, 72.68f),
        Point(5.0f, 98.03f),
        Point(6.0f, 62.19f),
        Point(7.0f, 89.54f),
        Point(8.0f, 101.28f),
        Point(9.0f, 76.35f),
        Point(10.0f, 94.81f),
        Point(11.0f, 69.07f),
        Point(12.0f, 85.62f),
        Point(13.0f, 108.95f),
        Point(14.0f, 73.41f),
        Point(15.0f, 90.28f),
        Point(16.0f, 65.74f),
        Point(17.0f, 81.99f),
        Point(18.0f, 103.63f),
        Point(19.0f, 77.17f)
    )

fun averageOfPoints(points: List<Point>): Float {
    var sum = 0f

    for (point in points) {
        sum += point.y
    }

    return sum / points.size
}

/* TODO: Maybe have settings icon on this page to also allow you to get to that plants settings. */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage(
    context: Context,
    navController: NavHostController,
    plantViewModel: CurrClickedPlantViewModel,
    state: SettingState
) {
    val lazyListState = rememberLazyListState()

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
                        text = (plantViewModel.currClickedPlant?.plantName ?: "Plant Name") + "'s History",
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 50.dp),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()

                        plantViewModel.currClickedPlant = null
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(40.dp),
                            tint = Color.Black

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

                        plantViewModel.currClickedPlant = null
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

                        plantViewModel.currClickedPlant = null
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
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            /* TODO: Put title above each chart. Add average below the graphs. Maybe group all the averages? (Probably not) */


            item {
                Card (
                    shape = MaterialTheme.shapes.medium,
                    colors = cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    //SimpleLineChart(pointsData = tempOverTime)

                    LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        lineChartData = SimpleLineChart(
                            pointsData = tempOverTime,
                            dataType = "temperature",
                            state = state
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Average: " + averageOfPoints(tempOverTime).toString() + "° " + if (state.isFahrenheit) "F" else "C",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 30.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        lineChartData = SimpleLineChart(
                            pointsData = tempOverTime,
                            dataType = "humidity"
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Average: " + "34.7 RH",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 30.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        lineChartData = SimpleLineChart(
                            pointsData = tempOverTime,
                            dataType = "moisture"
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Average: " + "75.32%",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 30.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        lineChartData = SimpleLineChart(
                            pointsData = tempOverTime,
                            dataType = "light"
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Average: " + "99.99%",
                            color = Color(0, 0, 0, 255),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }
    }
}