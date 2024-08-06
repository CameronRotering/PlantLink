package com.themakers.plantlink.MainPage

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.themakers.plantlink.PlantDataViewModel
import com.themakers.plantlink.R
import com.themakers.plantlink.SettingsPage.CurrClickedPlantViewModel
import com.themakers.plantlink.data.PlantDevice
import com.themakers.plantlink.data.SettingEvent
import com.themakers.plantlink.data.SettingState

val iconSize = 30.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceCard(
    //card: StoriesData,
    modifier: Modifier = Modifier,
    //onCardClick: () -> Unit,
    context: Context,
    navController: NavHostController,
    plantViewModel: PlantDataViewModel,
    plantDevice: PlantDevice, // Hold which plant this card pertains to
    state: SettingState,
    onEvent: (SettingEvent) -> Unit,
    clickedPlantViewModel: CurrClickedPlantViewModel
) {
    Card(
        onClick = {
            clickedPlantViewModel.currClickedPlant = plantDevice

            navController.navigate("HistoryPage")
        },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        backgroundColor = Color(217, 217, 217, 255), //MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Column {
//              Image would go here


            Column(
                //modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = plantDevice.name,
                    color = Color(0, 0, 0, 255),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

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
//                        Text(
//                            text = "Temperature",
//                            color = Color(0, 0, 0, 255),
//                            textAlign = TextAlign.Left,
//                            fontSize = 20.sp
//                        )
                    Text(
                        text = "74" + "° " + if (state.isFahrenheit) "F " else "C ",
                        color = Color(0, 0, 0, 255),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 30.sp,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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
//                        Text(
//                            text = "Humidity",
//                            color = Color(0, 0, 0, 255),
//                            textAlign = TextAlign.Left,
//                            fontSize = 20.sp
//                        )
                    Text(
                        text = "34.7 RH ",
                        color = Color(0, 0, 0, 255),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 30.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
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
//                    Text(
//                        text = "Soil Moisture",
//                        color = Color(0, 0, 0, 255),
//                        textAlign = TextAlign.Left,
//                        fontSize = 20.sp
//                    )
                    Text(
                        text = "75.32%",
                        color = Color(0, 0, 0, 255),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 30.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row (
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
//                    Text(
//                        text = " Light",
//                        color = Color(0, 0, 0, 255),
//                        textAlign = TextAlign.Left,
//                        fontSize = 20.sp
//                    )
                    Text(
                        text = "99.99%",
                        color = Color(0, 0, 0, 255),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 30.sp
                    )
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.End
        )
        {
            IconButton(
                onClick = {
                    /* TODO: Take to history page which loads average data of that plant */
                    /* TODO: Images above name of plant, maybe just template picture as soon as possible */

//                    Toast.makeText(
//                        context,
//                        "Navigate to plant settings",
//                        Toast.LENGTH_LONG
//                    ).show()

                    clickedPlantViewModel.currClickedPlant = plantDevice

                    navController.navigate("PlantLinkSettings")

                },

                ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Plant Settings",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }
    }
}