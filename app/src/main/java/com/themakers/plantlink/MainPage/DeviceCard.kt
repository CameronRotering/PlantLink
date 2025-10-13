package com.themakers.plantlink.MainPage

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.themakers.plantlink.PlantDataViewModel
import com.themakers.plantlink.R
import com.themakers.plantlink.SettingsPage.CurrClickedPlantViewModel
import com.themakers.plantlink.data.PlantDevice
import com.themakers.plantlink.data.SettingEvent
import com.themakers.plantlink.data.SettingState
import com.themakers.plantlink.ui.theme.PlantLinkTheme

val iconSize = 20.dp

@Composable
fun DrawCircleWithColor(insideColor: Color) {
    Canvas(
        modifier = Modifier
            .size(20.dp)
            .fillMaxHeight()
    ) {
        val radius = size.minDimension / 2
        drawCircle(
            color = insideColor,
            radius = radius
        )
        drawCircle(
            color = Color.Black,
            radius = radius - (2.dp.toPx() / 2), // Adjust radius for the border thickness
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun DeviceCard(
    modifier: Modifier = Modifier,
    context: Context?,
    navController: NavHostController,
    plantViewModel: PlantDataViewModel?,
    plantDevice: PlantDevice, // Hold which plant this card pertains to
    state: SettingState?,
    onEvent: ((SettingEvent) -> Unit)?,
    clickedPlantViewModel: CurrClickedPlantViewModel?,
    health: Int = 0 // 0-100 0 being bad health, 100 being best health
) {
    var hidden by remember { mutableStateOf(false) }

    val healthColorLerp: Color = when {
        health <= 50 -> lerp(Color.Red, Color.Yellow, health / 50f)
        else -> lerp(Color.Yellow, Color.Green, (health - 50) / 50f)
    }

    Card(
        modifier = modifier
            .clickable {
                if (clickedPlantViewModel != null) {
                    clickedPlantViewModel.currClickedPlant = plantDevice

                    navController.navigate("HistoryPage")
                }
            },
        shape = MaterialTheme.shapes.medium,
        colors = cardColors(
            containerColor = Color(217, 217, 217, 255),
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column {
//              Image would go here
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize(),
                )
                {
                    Text(
                        maxLines = 2,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = plantDevice.plantName,
                        color = Color(0, 0, 0, 255),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 5.dp)
                            .weight(1f),
                    )

                    Row(
//                        modifier = Modifier
//                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //if (hidden) { // Only show the icon when the device card is hidden. (Might change)
                        DrawCircleWithColor(healthColorLerp)
                        //}

                        IconButton(
                            onClick = {
                                /* TODO: Images above name of plant, maybe just template picture as soon as possible */

                                hidden = !hidden
                            }
                        ) {
                            Icon(
                                imageVector = if (hidden) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = if (hidden) "Expand" else "Collapse",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(5.dp)) // Moved to space out top info on card from sensor data. Was placed wrong before?

                if (!hidden) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_water_drop_24),
                            contentDescription = "Soil Moisture",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(iconSize)
                                .padding(start = 5.dp)
                        )
                        Text(
                            text = plantDevice.moisture.toString() + " % ",//"880 ",
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

@Preview(showBackground = true)
@Composable
fun DeviceCardPreview() {
    val plantDevice = PlantDevice("00:00:00:00:00:00", "Galaxy Petunia", "1", "10")
    val lazyGridState = rememberLazyGridState()

    PlantLinkTheme {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalArrangement = Arrangement.Start,
            state = lazyGridState,
            columns = GridCells.Adaptive(175.dp)
        ) {
            item {
                DeviceCard(
                    modifier = Modifier
                        .padding(deviceBoxPadding),
                    context = null,
                    navController = NavHostController(LocalContext.current),
                    plantViewModel = null,
                    plantDevice = plantDevice,
                    state = null,
                    onEvent = null,
                    clickedPlantViewModel = null
                )
            }

            item {
                DeviceCard(
                    modifier = Modifier
                        .padding(deviceBoxPadding),
                    context = null,
                    navController = NavHostController(LocalContext.current),
                    plantViewModel = null,
                    plantDevice = plantDevice,
                    state = null,
                    onEvent = null,
                    clickedPlantViewModel = null,
                    health = 25
                )
            }

            item {
                DeviceCard(
                    modifier = Modifier
                        .padding(deviceBoxPadding),
                    context = null,
                    navController = NavHostController(LocalContext.current),
                    plantViewModel = null,
                    plantDevice = plantDevice,
                    state = null,
                    onEvent = null,
                    clickedPlantViewModel = null,
                    health = 50
                )
            }

            item {
                DeviceCard(
                    modifier = Modifier
                        .padding(deviceBoxPadding),
                    context = null,
                    navController = NavHostController(LocalContext.current),
                    plantViewModel = null,
                    plantDevice = plantDevice,
                    state = null,
                    onEvent = null,
                    clickedPlantViewModel = null,
                    health = 75
                )
            }

            item {
                DeviceCard(
                    modifier = Modifier
                        .padding(deviceBoxPadding),
                    context = null,
                    navController = NavHostController(LocalContext.current),
                    plantViewModel = null,
                    plantDevice = plantDevice,
                    state = null,
                    onEvent = null,
                    clickedPlantViewModel = null,
                    health = 100
                )
            }
        }
    }
}