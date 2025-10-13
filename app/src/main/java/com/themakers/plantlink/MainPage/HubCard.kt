package com.themakers.plantlink.MainPage

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
fun HubCard( // Can add trailing and "action" (leading) icons and text to make way easier to change
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
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = cardColors(
            containerColor = Color(217, 217, 217, 255),
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Text(
                        maxLines = 2,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = hubDevice.hubName,
                        color = Color(0, 0, 0, 255),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 5.dp)
                            .weight(1f),
                    )


                    IconButton(
                        onClick = {
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



                if (!hidden) {
                    SensorRow(
                        "Temperature",
                        hubDevice.getTempString(state!!.isFahrenheit),
                        if (state.isFahrenheit) "F" else "C",
                        R.drawable.baseline_device_thermostat_24
                    )

                    SensorRow(
                        "Humidity",
                        hubDevice.humidity.toString(),
                        "RH",
                        R.drawable.sharp_humidity_percentage_24
                    )

                    SensorRow(
                        "Light",
                        hubDevice.light.toString(),
                        "lux",
                        R.drawable.baseline_sun_24
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HubCardPreview() {
    val hubDevice = HubDevice("00:00:00:00:00:00", "Galaxy Petunia")

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