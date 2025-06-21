package com.themakers.plantlink.MainPage

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.themakers.plantlink.data.HubDevice
import com.themakers.plantlink.data.SettingEvent
import com.themakers.plantlink.data.SettingState
import com.themakers.plantlink.ui.theme.PlantLinkTheme


@Composable
fun HubCard(
    modifier: Modifier = Modifier,
    context: Context?,
    navController: NavHostController,
    plantViewModel: PlantDataViewModel?,
    hubDevice: HubDevice, // Hold which plant this card pertains to
    state: SettingState?,
    onEvent: ((SettingEvent) -> Unit)?
) {
    var hidden by remember { mutableStateOf(false) }


    Card(
        //modifier = modifier
        //    .clickable {
        //        if (clickedPlantViewModel != null) {
        //            clickedPlantViewModel.currClickedPlant = plantDevice

        //            navController.navigate("HistoryPage")
        //       }
        //    },
        modifier = modifier,
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
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxSize(),
                )
                {
                    Text(
                        maxLines = 2,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = hubDevice.hubName,
                        color = Color(0, 0, 0, 255),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.1f)
                            .padding(start = 5.dp),
                    )

                    IconButton(
                        onClick = {
                            /* TODO: Images above name of plant, maybe just template picture as soon as possible */

                            hidden = !hidden

                            //clickedPlantViewModel.currClickedPlant = plantDevice

                            //navController.navigate("PlantLinkSettings")
                        },
                        modifier = Modifier
                            //.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (hidden) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Plant Settings",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(30.dp)
                                .fillMaxSize()
                                .padding(end = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_device_thermostat_24),
                        contentDescription = "Temperature",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(iconSize)
                            .padding(start = 5.dp)
                    )
                    Text(
                        text = hubDevice.getTempString(state!!.isFahrenheit) + "° " + if (state.isFahrenheit) "F " else "C ",
                        color = Color(0, 0, 0, 255),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 30.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sharp_humidity_percentage_24),
                        contentDescription = "Humidity",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(iconSize)
                            .padding(start = 5.dp)
                    )
                    Text(
                        text = hubDevice.humidity.toString() + " RH ",
                        color = Color(0, 0, 0, 255),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 30.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_sun_24),
                        contentDescription = "Light",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(iconSize)
                            .padding(start = 5.dp)
                    )
                    Text(
                        text = hubDevice.light.toString() + " lux ",
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

@Preview(showBackground = true)
@Composable
fun HubCardPreview() {
    var hubDevice = HubDevice("00:00:00:00:00:00", "Galaxy Petunia")

    PlantLinkTheme {
        Row() {
            HubCard(
                context = null,
                navController = NavHostController(LocalContext.current),
                plantViewModel = null,
                hubDevice = hubDevice,
                state = null,
                onEvent = null
            )
        }
    }
}