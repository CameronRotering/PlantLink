package com.themakers.plantlink.MainPage

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SensorRow(
    sensorName: String,
    sensorValue: String,
    sensorUnit: String,
    sensorIcon: Int,
    sensorNameModifier: Modifier = Modifier
        .fillMaxWidth(0.5f)
        .fillMaxHeight()
        .padding(start = 5.dp),
    sensorValueModifier: Modifier = Modifier
        .fillMaxSize()
        //.padding(end = 5.dp)
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
//            .border(
//                width = 2.dp,
//                color = Color.Black,
//                shape = MaterialTheme.shapes.medium
//            )
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            maxLines = 2,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = sensorName,
            color = Color(0, 0, 0, 255),
            textAlign = TextAlign.Left,
            modifier = sensorNameModifier
                .weight(1f),
            fontSize = 20.sp,
            //fontWeight = FontWeight.SemiBold,
        )


        Text(
            maxLines = 1,
            text = "$sensorValue $sensorUnit",
            color = Color(0, 0, 0, 255),
            textAlign = TextAlign.Right,
            modifier = sensorValueModifier
                .weight(1f),
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.padding(start = 5.dp)) // Since changing padding directly on icon shrinks it

        Icon(
            painter = painterResource(sensorIcon),
            contentDescription = sensorName,
            tint = Color.Black,
            modifier = Modifier
                //.fillMaxWidth()
                .size(iconSize)
                //.padding(end = 5.dp)
        )

        Spacer(modifier = Modifier.padding(end = 5.dp))

    }
    Spacer(modifier = Modifier.height(5.dp))
}