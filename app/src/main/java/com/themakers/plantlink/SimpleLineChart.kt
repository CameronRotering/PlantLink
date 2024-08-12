package com.themakers.plantlink

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

fun getPreviousDate(daysAgo: Int): String {
    val today = LocalDate.now()

    return today.minusDays(daysAgo.toLong()).format(dateTimeFormat)
}

@SuppressLint("DefaultLocale")
fun SimpleLineChart(
    pointsData: List<Point>,
    dataType: String = "",
    xLabel: String = "",
    yLabel: String = "",
): LineChartData {
    val minValue = pointsData.minOf { it.y }
    val maxValue = pointsData.maxOf { it.y }
    val steps = 5 // Adjust the divisor (10) for desired granularity

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color.White)
        .steps(pointsData.size - 1)
        .labelData { i ->
            getPreviousDate(i)
        }
        .labelAndAxisLinePadding(15.dp)
        .axisLabelDescription{ _ ->
            xLabel
        }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.White)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            var returnData = ""
            val dataAsFloat: Float
            val stepSize = (maxValue - minValue) / (steps) // Calculate the size of each step

            if (dataType == "temperature") {
                dataAsFloat = minValue + i * stepSize

                returnData = "${dataAsFloat.toInt()} °F"
            } else if (dataType == "humidity") {
                dataAsFloat = minValue + i * stepSize

                returnData = "${dataAsFloat.toInt()} RH"
            } else if (dataType == "moisture" || dataType == "light") {
                dataAsFloat = minValue + i * stepSize

                returnData = "${dataAsFloat.toInt()}%"
            }

            returnData



//            val labelValue = minValue + i * stepSize
//
//            "${labelValue.toInt()} °F"
        }
        .axisLabelDescription{ _ ->
            yLabel
        }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = null,
        backgroundColor = Color.White
    )

    return lineChartData
}