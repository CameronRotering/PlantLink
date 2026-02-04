package com.themakers.plantlink.HistoryPage

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.themakers.plantlink.SettingsPage.CurrClickedPlantViewModel
import com.themakers.plantlink.data.SettingState
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.themakers.plantlink.composables.BottomToolBar
import com.themakers.plantlink.graphs.SoilMoistureChart


/* TODO: Maybe have settings icon on this page to also allow you to get to that plants settings. */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantHistoryPage(
    destinationName: String,
    context: Context,
    navController: NavHostController,
    plantViewModel: CurrClickedPlantViewModel,
    state: SettingState
) {
    val lazyListState = rememberLazyListState()

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11) }
        }
    }

    // Create Vico models from your data
    // It's good practice to remember these producers
    //val tempChartEntryModelProducer = remember { ChartEntryModelProducer(tempOverTime.map { FloatEntry(it.x, it.y) }) }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                title = {
                    Text(
                        text = (plantViewModel.currClickedPlant?.plantName ?: "Plant Name") + "'s History",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (navController.currentDestination!!.route == destinationName) {
                                navController.navigateUp()
                            }

                        plantViewModel.currClickedPlant = null
                    },
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("PlantLinkSettings")
                    },
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Plant Settings",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(25.dp)
                                //.padding(end = 10.dp)
                        )
                    }
                }
            )
        },
        bottomBar = { BottomToolBar(navController = navController) },
        contentWindowInsets = WindowInsets.safeContent // Safe content so no content is hidden under system things like camera AND is interactive
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            /* TODO: Put title above each chart. Add average below the graphs. Maybe group all the averages? (Probably not) */


            item {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 5.dp, vertical = 20.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        SoilMoistureChart()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Average Soil Moisture: " + averageOfPoints(tempOverTime).toString() + "%",
                                color = Color(0, 0, 0, 255),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 25.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
