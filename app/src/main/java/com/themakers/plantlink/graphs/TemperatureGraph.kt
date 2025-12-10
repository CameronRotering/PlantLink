package com.themakers.plantlink.graphs

import android.text.SpannableStringBuilder
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
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.themakers.plantlink.data.SettingState
import java.text.DecimalFormat
import java.util.Calendar
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

private val secRangeProvider = object : CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) = floor(minY - 5)
    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = ceil(maxY + 5)
}

private val zeroToHundredRangeProvider = object : CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) = 0.0
    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = 100.0
}

private val lightRangeProvider = object : CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) = 0.0001
    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = 32635.0
}

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
    val markerValueFormatter = remember(xLabels, yDecimalFormat) {
        DefaultCartesianMarker.ValueFormatter { _, targets ->
            val builder = SpannableStringBuilder()
            targets.forEachIndexed { index, target ->
                if (target is LineCartesianLayerMarkerTarget) {
                    val point = target.points.firstOrNull()
                    if (point != null) {
                        val x = point.entry.x.toInt()
                        val label = xLabels.getOrNull(x)
                        if (label != null) {
                            builder.append(label).append(" ")
                        }
                        builder.append(yDecimalFormat.format(point.entry.y))
                    }
                }
                if (index != targets.lastIndex) {
                    builder.append(", ")
                }
            }
            builder
        }
    }

    Box(
        //contentAlignment = Alignment.BottomEnd,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    pointSpacing = 5.dp,
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
                .height(250.dp)
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
private fun MonthlyChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    xLabels: List<String>,
    format: String,
    lineColor: Color,
    gradientArray: Array<Color>,
    rangeProvider: CartesianLayerRangeProvider
) {
    val yDecimalFormat = DecimalFormat(format)
    val startAxisValueFormatter = CartesianValueFormatter.decimal(yDecimalFormat)
    val markerValueFormatter = remember(xLabels, yDecimalFormat) {
        DefaultCartesianMarker.ValueFormatter { _, targets ->
            val builder = SpannableStringBuilder()
            targets.forEachIndexed { index, target ->
                if (target is LineCartesianLayerMarkerTarget) {
                    val point = target.points.firstOrNull()
                    if (point != null) {
                        val x = point.entry.x.toInt()
                        val label = xLabels.getOrNull(x)
                        if (label != null) {
                            builder.append(label).append(" ")
                        }
                        builder.append(yDecimalFormat.format(point.entry.y))
                    }
                }
                if (index != targets.lastIndex) {
                    builder.append(", ")
                }
            }
            builder
        }
    }

    Box(
        //contentAlignment = Alignment.BottomEnd,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    pointSpacing = 5.dp,
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
                    rangeProvider = rangeProvider,
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
                .height(250.dp)
                .fillMaxWidth()
            //.width(100.dp)
            ,
            scrollState = rememberVicoScrollState(
                scrollEnabled = true,
                autoScrollCondition = AutoScrollCondition.OnModelGrowth
            ),
            consumeMoveEvents = true,
            zoomState = rememberVicoZoomState(true),
            animateIn = false,
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
        val y = List(daysInMonth) { Random.nextFloat() * 100 } // 30 + 60
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
        Color(0x66F44336),
        arrayOf(
            Color(0x99E57373),
            Color(0x99E57373),
            Color(0x99FFFFFF),
            Color(0x9964B5F6)
        )// 0x66 being 0.4f alpha
    )
}

@Composable
fun SoilMoistureChart(
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var xLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        xLabels = (1..daysInMonth).map { day -> "$month/$day" }
        val y = List(daysInMonth) { Random.nextFloat() * 100 }
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/vmml6t.
            lineSeries { series(y) }
        }
    }
    MonthlyChart(
        modelProducer,
        modifier,
        xLabels,
        "#.##'%'",
        Color(0x66248721),
        arrayOf(
            Color(0x9964B5F6),
            Color(0x9964B5F6),
            Color(0x9959E05E),
            Color(0x9959E05E),
            Color(0x9959E05E),
            Color(0x99C70000)
        ),  // 0x66 being 0.4f alpha
        zeroToHundredRangeProvider
    )
}

@Composable
fun HumidityChart(
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var xLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        xLabels = (1..daysInMonth).map { day -> "$month/$day" }
        val y = List(daysInMonth) { Random.nextFloat() * 100 }
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/vmml6t.
            lineSeries { series(y) }
        }
    }
    MonthlyChart(
        modelProducer,
        modifier,
        xLabels,
        "#.##'% RH'",
        Color(0x9964B5F6),
        arrayOf(
            Color(0x9941B2FF),
            Color(0x9964B5F6),
            //Color(0x4D64B5F6),
            Color(0x66FFFFFF),
        ), // 0x66 being 0.4f alpha
        zeroToHundredRangeProvider
    )
}

@Composable
fun LightChart(
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var xLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        xLabels = (1..daysInMonth).map { day -> "$month/$day" }
        val y = List(daysInMonth) { Random.nextDouble(0.0001, 32635.0).toFloat() }
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/vmml6t.
            lineSeries { series(y) }
        }
    }
    MonthlyChart(
        modelProducer,
        modifier,
        xLabels,
        "#.##' lux'",
        Color(0x66FFC107),
        arrayOf(
            Color(0x99FF0000),
            Color(0xCCFFC107),
            Color(0x80FFC107),
            Color(0x66FFDA03),
            Color(0x66FFDA03),
            Color(0x4DDABA01),
            //Color(0x33FFFFFF),
            Color(0x22FFFFFF),
            //Color(0x33646464),
            //Color(0x00000000),
            Color(0x99000000)
        ), // 0x66 being 0.4f alpha
        lightRangeProvider
    )
}