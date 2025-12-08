package com.themakers.plantlink.graphs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.themakers.plantlink.data.SettingState
import java.text.DecimalFormat
import java.util.Calendar
import kotlin.math.roundToInt
import kotlin.random.Random

private val secRangeProvider = CartesianLayerRangeProvider.auto()

class BottomAxisValueFormatter(private val xLabels: List<String>) : CartesianValueFormatter {
    override fun format(
        context: CartesianMeasuringContext,
        value: Double,
        verticalAxisPosition: Axis.Position.Vertical?
    ): CharSequence {
        return xLabels.getOrNull(value.roundToInt()) ?: value.toString()
    }
}

@Composable
private fun MonthlyTemperatureChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    xLabels: List<String>,
    state: SettingState,
    lineColor: Color,
    gradientArray: Array<Color>
) {
    val temperatureUnit = if (state.isFahrenheit) "F" else "C"
    val yDecimalFormat = DecimalFormat("#.##'°$temperatureUnit'")
    val startAxisValueFormatter = CartesianValueFormatter.decimal(yDecimalFormat)
    val markerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(yDecimalFormat)

    Box(
        //contentAlignment = Alignment.BottomEnd,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    pointSpacing = 20.dp,
                    lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                                areaFill =
                                    LineCartesianLayer.AreaFill.single(
                                        fill(
                                            ShaderProvider.verticalGradient(
                                                gradientArray
                                            )
                                        )
                                    ),
                            )
                        ),
                    rangeProvider = secRangeProvider,
                ),
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = startAxisValueFormatter,
                    label = rememberTextComponent(
                        color = Color.Black
                    ),
                    guideline = rememberAxisGuidelineComponent(
                        shape = Shape.Rectangle
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = BottomAxisValueFormatter(xLabels),
                    label = rememberTextComponent(
                        color = Color.Black
                    ),
                    guideline = rememberAxisGuidelineComponent(
                        shape = Shape.Rectangle
                    )
                ),
                marker = rememberMarker(markerValueFormatter),
            ),
            modelProducer,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
            //.width(100.dp)
            ,
            scrollState = rememberVicoScrollState(
                scrollEnabled = true,
                autoScrollCondition = AutoScrollCondition.OnModelGrowth
            ),
            consumeMoveEvents = true,
            zoomState = rememberVicoZoomState(true),
            animateIn = false
        )
    }

}

@Composable
fun TemperatureChart(
    modifier: Modifier = Modifier,
    state: SettingState
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var xLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        xLabels = (1..daysInMonth).map { day -> "$month/$day" }
        val y = List(daysInMonth) { Random.nextFloat() * 30 + 60 }
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/vmml6t.
            lineSeries { series(y) }
        }
    }
    MonthlyTemperatureChart(
        modelProducer,
        modifier,
        xLabels,
        state,
        Color(0x7FFF6363),
        arrayOf(Color(0x66E57373), Color(0x66FFFFFF), Color(0x6664B5F6))) // 0x66 being 0.4f alpha
}
