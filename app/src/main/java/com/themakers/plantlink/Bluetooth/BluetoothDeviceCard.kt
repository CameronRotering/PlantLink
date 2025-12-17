package com.themakers.plantlink.Bluetooth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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

@Composable
fun BluetoothDeviceCard(
    device: BluetoothDevice,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = MaterialTheme.shapes.medium,
        colors = cardColors(
            containerColor = Color(217, 217, 217, 255),
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier
            .padding(vertical = 8.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = device.name!!,// ?:  "(No Name)",//device.address!!,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(device) }
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
fun BluetoothDeviceCardPreview() {
    val lazyListState = rememberLazyListState()

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = MaterialTheme.shapes.medium,
        colors = cardColors(
            containerColor = Color(217, 217, 217, 255),
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
                //modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            state = lazyListState
        ) {
            item {
                BluetoothDeviceCard(
                    device = BluetoothDevice("PlantLink Device", "0", null),
                    onClick = {}
                )
            }
        }
    }
}