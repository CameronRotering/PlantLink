package com.themakers.plantlink.graphs

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import java.text.DecimalFormat

private val secRangeProvider = CartesianLayerRangeProvider.auto()

// Use, but set to max value of data rounded to nearest 10 or 5, plus 5
private val RangeProvider = CartesianLayerRangeProvider.fixed(maxY = 100.0)
private val YDecimalFormat = DecimalFormat("#.##'F'")
private val StartAxisValueFormatter = CartesianValueFormatter.decimal(YDecimalFormat)
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(YDecimalFormat)

@Composable
private fun JetpackComposeElectricCarSales(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    val lineColor = Color(0xffa485e0)
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                            areaFill =
                                LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(lineColor.copy(alpha = 0.4f), Color.Transparent)
                                        )
                                    )
                                ),
                        )
                    ),
                rangeProvider = secRangeProvider,
            ),
            startAxis = VerticalAxis.rememberStart(valueFormatter = StartAxisValueFormatter),
            bottomAxis = HorizontalAxis.rememberBottom(),
            marker = rememberMarker(MarkerValueFormatter),
        ),
        modelProducer,
        modifier.height(220.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

private val x = listOf("2", "3", "4", "5", "6", "7", "8/9/2025", "9", "10", "11", "12", "13", "14", "15")
private val y = listOf<Number>(83.45, 91.78, 67.92, 105.3, 72.68, 98.03, 62.19, 89.54, 101.28, 76.35, 94.81, 69.07, 85.62, 108.95, 73.41, 90.28, 65.74, 81.99, 103.63, 77.17, 100)

@Composable
fun TemperatureChart(modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/vmml6t.
            lineSeries { series(y) }
            extras { x }
        }
    }
    JetpackComposeElectricCarSales(modelProducer, modifier)
}