package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RailwayDataRepository
import com.example.model.DelayTrendPoint
import com.example.ui.theme.*

@Composable
fun DelayTrendChart(
    dataPoints: List<DelayTrendPoint> = RailwayDataRepository.DELAY_TREND_HOURLY,
    modifier: Modifier = Modifier,
    testTag: String = "delay_trend_chart"
) {
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NETWORK DELAY TREND OVER 24 HOURS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = RailTextSecondary
                    )
                    Text(
                        text = "Actual Delay vs RailPredict AI Forecast",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = RailTextTertiary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChartLegendItem("Actual Delay", RailOrangeMod)
                    ChartLegendItem("AI Forecast", RailTealAccent)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val width = size.width
                val height = size.height
                val paddingBottom = 24f
                val paddingTop = 12f
                val chartHeight = height - paddingBottom - paddingTop
                val maxVal = 45f // max delay in minutes

                // Draw background grid lines
                for (i in 0..4) {
                    val y = paddingTop + chartHeight * (1f - (i / 4f))
                    drawLine(
                        color = RailNavy600.copy(alpha = 0.4f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                    // Grid label
                    val label = "${(i * 10)}m"
                    val tl = textMeasurer.measure(
                        text = label,
                        style = TextStyle(color = RailTextTertiary, fontSize = 9.sp)
                    )
                    drawText(tl, topLeft = Offset(4f, y - 14f))
                }

                if (dataPoints.size >= 2) {
                    val stepX = width / (dataPoints.size - 1)

                    val actualPath = Path()
                    val predictedPath = Path()
                    val areaPath = Path()

                    dataPoints.forEachIndexed { i, pt ->
                        val x = i * stepX
                        val yActual = paddingTop + chartHeight * (1f - (pt.avgDelayMinutes / maxVal).coerceIn(0f, 1f))
                        val yPred = paddingTop + chartHeight * (1f - (pt.predictedDelayMinutes / maxVal).coerceIn(0f, 1f))

                        if (i == 0) {
                            actualPath.moveTo(x, yActual)
                            predictedPath.moveTo(x, yPred)
                            areaPath.moveTo(x, yPred)
                        } else {
                            actualPath.lineTo(x, yActual)
                            predictedPath.lineTo(x, yPred)
                            areaPath.lineTo(x, yPred)
                        }

                        // Time label on X axis
                        val timeLayout = textMeasurer.measure(
                            text = pt.timeSlot,
                            style = TextStyle(color = RailTextSecondary, fontSize = 9.sp)
                        )
                        drawText(timeLayout, topLeft = Offset(x - timeLayout.size.width / 2f, height - 16f))
                    }

                    // Fill area under AI curve
                    areaPath.lineTo(width, paddingTop + chartHeight)
                    areaPath.lineTo(0f, paddingTop + chartHeight)
                    areaPath.close()
                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(RailTealAccent.copy(alpha = 0.2f), Color.Transparent),
                            startY = paddingTop,
                            endY = paddingTop + chartHeight
                        )
                    )

                    // Draw actual curve
                    drawPath(
                        path = actualPath,
                        color = RailOrangeMod,
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )

                    // Draw predicted curve
                    drawPath(
                        path = predictedPath,
                        color = RailTealAccent,
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )

                    // Draw data point dots
                    dataPoints.forEachIndexed { i, pt ->
                        val x = i * stepX
                        val yPred = paddingTop + chartHeight * (1f - (pt.predictedDelayMinutes / maxVal).coerceIn(0f, 1f))
                        drawCircle(
                            color = RailNavy900,
                            radius = 4.5f,
                            center = Offset(x, yPred)
                        )
                        drawCircle(
                            color = RailTealAccent,
                            radius = 3f,
                            center = Offset(x, yPred)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccuracyComparisonCard(
    modifier: Modifier = Modifier,
    testTag: String = "accuracy_comparison_card"
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI MODEL ACCURACY VS TRADITIONAL SYSTEM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = RailTextSecondary
                    )
                    Text(
                        text = "Dynamic ML Multi-Factor Benchmark",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = RailTextTertiary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RailGreenOnTime.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, RailGreenOnTime)
                ) {
                    Text(
                        text = "+67% ACCURACY GAIN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = RailGreenOnTime,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Comparison
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ComparisonBar(
                    label = "Traditional Linear Static ETA",
                    value = "14.6 min error",
                    ratio = 1.0f,
                    barColor = RailRedSevere.copy(alpha = 0.8f)
                )
                ComparisonBar(
                    label = "RailPredict AI Dynamic Forecast",
                    value = "4.8 min error",
                    ratio = 4.8f / 14.6f,
                    barColor = RailTealAccent
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Mini Metric Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill("Model Accuracy", "92.4%", RailTealAccent, Modifier.weight(1f))
                MetricPill("MAE (Mean Error)", "4.2 min", RailBlue400, Modifier.weight(1f))
                MetricPill("RMSE", "6.7 min", RailPurpleAI, Modifier.weight(1f))
                MetricPill("Avg Recovery", "-3.8 min", RailGreenOnTime, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ComparisonBar(
    label: String,
    value: String,
    ratio: Float,
    barColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = RailTextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(RailNavy900)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = RailNavy900,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = RailTextTertiary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ChartLegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = RailTextSecondary
        )
    }
}
