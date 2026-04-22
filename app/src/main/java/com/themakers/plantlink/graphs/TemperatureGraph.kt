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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.themakers.plantlink.data.SettingState
import java.util.Calendar
import kotlin.math.ceil
import kotlin.math.floor
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

@Composable
fun TemperatureChart(
    state: SettingState,
    modifier: Modifier = Modifier,
    modelProducer: CartesianChartModelProducer = remember { CartesianChartModelProducer() },
    lineColor: Color = Color(0x66F44336),
    gradientArray: Array<Color> = arrayOf(
        Color(0xCCFC6A6A),
        Color(0x99E57373),
        Color(0x99E57373),
        //Color(0x99FFFFFF),
        Color(0x9964B5F6),
        Color(0x99248FFF),
    )
) {
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) // Keeping while still using random values
        val y = List(daysInMonth) { Random.nextFloat() * 100 } // 30 + 60
        modelProducer.runTransaction {
            lineSeries {
                series(y)
            }
        }
    }

    TemperatureGraph(
        modelProducer = modelProducer,
        modifier = modifier,
        state = state,
        lineColor = lineColor,
        gradientArray = gradientArray
    )
}

private var monthDayStringArray = mutableListOf<String>()


fun getMonthDayString(): MutableList<String> {
    if (monthDayStringArray.isNotEmpty()) return monthDayStringArray

    var xLabels: MutableList<String>

    val calendar = Calendar.getInstance()
    val month = calendar.get(Calendar.MONTH) + 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    xLabels = (1..daysInMonth).map { day -> "$month/$day" } as MutableList<String>

    monthDayStringArray = xLabels
    return monthDayStringArray
}

@Composable
fun TemperatureGraph(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    state: SettingState,
    lineColor: Color,
    gradientArray: Array<Color>
) {
    var xLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    xLabels = getMonthDayString()

    val temperatureUnit = if (state.isFahrenheit) "F" else "C"
    val startAxisValueFormatter = CartesianValueFormatter.decimal(decimalCount = 1, suffix = "°$temperatureUnit")
    val markerValueFormatter = remember(xLabels) {
        DefaultCartesianMarker.ValueFormatter { _, targets ->
            val builder = SpannableStringBuilder()
            targets.forEachIndexed { index, target ->
                if (target is LineCartesianLayerMarkerTarget) {
                    val point = target.points.firstOrNull()
                    if (point != null) {
                        builder.append(String.format("%.1f°%s", point.entry.y, temperatureUnit))
                    }
                }
            }
            builder
        }
    }

    val scrollState = rememberVicoScrollState()
    val zoomState = rememberVicoZoomState()

    val secRangeProvider = remember {
        CartesianLayerRangeProvider.auto()
    }

    Box(
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
                                fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                                areaFill =
                                    LineCartesianLayer.AreaFill.single(
                                        Fill(Brush.verticalGradient(gradientArray.toList()))
                                    ),
                            )
                        ),
                    rangeProvider = secRangeProvider,
                ),
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = startAxisValueFormatter,
                    label = rememberTextComponent(
                        style = TextStyle(color = Color.Black)
                    ),
                    guideline = rememberAxisGuidelineComponent(
                        shape = RectangleShape
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, x, _ -> xLabels.getOrNull(x.toInt()) ?: "" },
                    label = rememberTextComponent(
                        style = TextStyle(color = Color.Black)
                    ),
                    guideline = rememberAxisGuidelineComponent(
                        shape = RectangleShape
                    )
                ),
                marker = rememberMarker(markerValueFormatter),
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth(),
            scrollState = scrollState,
            zoomState = zoomState,
        )
    }
}

@Composable
private fun MonthlyChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    xLabels: List<String>,
    lineColor: Color,
    gradientArray: Array<Color>,
    format: String,
    rangeProvider: CartesianLayerRangeProvider = CartesianLayerRangeProvider.auto()
) {
    val startAxisValueFormatter = CartesianValueFormatter.decimal(decimalCount = 1, suffix = format.substringAfter("'").substringBefore("'"))
    val markerValueFormatter = remember(xLabels) {
        DefaultCartesianMarker.ValueFormatter { _, targets ->
            val builder = SpannableStringBuilder()
            targets.forEachIndexed { index, target ->
                if (target is LineCartesianLayerMarkerTarget) {
                    val point = target.points.firstOrNull()
                    if (point != null) {
                        builder.append(String.format("%.1f%s", point.entry.y, format.substringAfter("'").substringBefore("'")))
                    }
                }
            }
            builder
        }
    }

    val scrollState = rememberVicoScrollState(
        autoScrollCondition = AutoScrollCondition.OnModelGrowth
    )
    val zoomState = rememberVicoZoomState()

    Box(
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
                                fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                                areaFill =
                                    LineCartesianLayer.AreaFill.single(
                                        Fill(Brush.verticalGradient(gradientArray.toList()))
                                    ),
                            )
                        ),
                    rangeProvider = rangeProvider,
                ),
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = startAxisValueFormatter,
                    label = rememberTextComponent(
                        style = TextStyle(color = Color.Black)
                    ),
                    guideline = rememberAxisGuidelineComponent(
                        shape = RectangleShape
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, x, _ -> xLabels.getOrNull(x.toInt()) ?: "" },
                    label = rememberTextComponent(
                        style = TextStyle(color = Color.Black)
                    ),
                    guideline = rememberAxisGuidelineComponent(
                        shape = RectangleShape
                    )
                ),
                marker = rememberMarker(markerValueFormatter),
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth(),
            scrollState = scrollState,
            zoomState = zoomState,
        )
    }
}

@Composable
fun HumidityChart(
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var xLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    xLabels = getMonthDayString()

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val y = List(daysInMonth) { Random.nextFloat() * 100 }
        modelProducer.runTransaction {
            lineSeries { series(y) }
        }
    }
    MonthlyChart(
        modelProducer = modelProducer,
        modifier = modifier,
        xLabels = xLabels,
        lineColor = Color(0x665A95FC),
        gradientArray = arrayOf(
            Color(0x990700C7),
            Color(0x990042F6),
            Color(0x995A9BFC),
            //Color(0x4D64B5F6),
            //Color(0x66FFFFFF),
            Color(0x99FF7700)
        ),
        format = "#.##'% RH'",
        rangeProvider = zeroToHundredRangeProvider
    )
}

@Composable
fun LightChart(
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var xLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    xLabels = getMonthDayString()

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val y = List(daysInMonth) { Random.nextDouble(0.0001, 32635.0).toFloat() }
        modelProducer.runTransaction {
            lineSeries { series(y) }
        }
    }
    MonthlyChart(
        modelProducer = modelProducer,
        modifier = modifier,
        xLabels = xLabels,
        lineColor = Color(0x66FFC107),
        gradientArray = arrayOf(
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
        ),
        format = "#.##' lux'",
        rangeProvider = lightRangeProvider
    )
}

@Composable
fun SoilMoistureChart(
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var xLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    xLabels = getMonthDayString()

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val y = List(daysInMonth) { Random.nextFloat() * 100 }
        modelProducer.runTransaction {
            lineSeries { series(y) }
        }
    }
    MonthlyChart(
        modelProducer = modelProducer,
        modifier = modifier,
        xLabels = xLabels,
        lineColor = Color(0x66248721),
        gradientArray = arrayOf(
            Color(0x9964B5F6),
            Color(0x9964B5F6),
            Color(0x9959E05E),
            Color(0x9959E05E),
            Color(0x9959E05E),
            Color(0x99C70000)
        ),
        format = "#.##'%'",
        rangeProvider = zeroToHundredRangeProvider
    )
}
