package com.themakers.plantlink.Bluetooth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.themakers.plantlink.ui.theme.PlantLinkTheme

@Composable
fun BluetoothDeviceCard(
    device: BluetoothDevice,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier,
    paired: Boolean = false
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = MaterialTheme.shapes.medium,
        colors = cardColors(
            containerColor = Color(217, 217, 217),
            contentColor = Color(0, 0, 0, 255)
        ),
        modifier = Modifier
            .padding(vertical = 6.dp)
            .clickable { onClick(device) }
            .fillMaxWidth(0.9f),
        //border = BorderStroke(1.dp, Color.Black)
    ) {
        if (paired) {
            Text(
                text = "Paired Device",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp),
                color = Color.Blue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (!paired) Spacer(modifier = Modifier.padding(top = 8.dp))

        Text(
            text = device.name!!,// ?:  "(No Name)",//device.address!!,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 8.dp, end = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = Modifier
//            .fillMaxWidth(),
//    ) {
//        HorizontalDivider(
//            modifier = Modifier
//                .fillMaxWidth(0.95f)
//        )
//    }
}

@Preview
@Composable
fun BluetoothDeviceCardPreview() {
    val lazyListState = rememberLazyListState()
    PlantLinkTheme() {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = MaterialTheme.shapes.medium,
            colors = cardColors(
                containerColor = Color(255, 255, 255, 255),
                contentColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Scanned Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Black
                    //modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = lazyListState
            ) {
                items(5) {
                    BluetoothDeviceCard(
                        device = BluetoothDevice("PlantLink Device", "0", null),
                        onClick = {}
                    )
                }
            }
        }
    }
}