package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RailwayDataRepository
import com.example.model.Station
import com.example.model.Train
import com.example.model.TrainStatus
import com.example.ui.theme.*
import kotlin.math.sqrt

@Composable
fun LiveRailwayMap(
    trains: List<Train>,
    selectedTrain: Train?,
    onSelectTrain: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFullHeight: Boolean = false,
    testTag: String = "live_railway_map"
) {
    val textMeasurer = rememberTextMeasurer()
    val stations = remember { RailwayDataRepository.STATIONS }

    // Pulse animation for active train beacons
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // Lat/Lon bounding box for India Railway Trunk network
    // Lat: 12.0 (South) to 29.5 (North)
    // Lon: 71.5 (West) to 89.0 (East)
    val minLat = 12.0
    val maxLat = 29.8
    val minLon = 71.5
    val maxLon = 89.0

    fun toScreenOffset(lat: Double, lon: Double, width: Float, height: Float): Offset {
        val x = ((lon - minLon) / (maxLon - minLon) * (width - 60f) + 30f).toFloat()
        val y = ((maxLat - lat) / (maxLat - minLat) * (height - 60f) + 30f).toFloat()
        return Offset(x, y)
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(if (isFullHeight) 440.dp else 290.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(RailNavy900)
            .border(1.dp, RailNavy600, RoundedCornerShape(14.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(trains) {
                    detectTapGestures { tapOffset ->
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        // Find closest train to tap with enlarged hit-test area for easy mobile touch
                        var closestTrain: Train? = null
                        var minDist = Float.MAX_VALUE

                        trains.forEach { t ->
                            val trainPos = toScreenOffset(t.latitude, t.longitude, w, h)
                            val dist = sqrt((trainPos.x - tapOffset.x) * (trainPos.x - tapOffset.x) + (trainPos.y - tapOffset.y) * (trainPos.y - tapOffset.y))
                            // Generous 90f pixel radius for easy finger tap on real mobile devices
                            if (dist < 90f && dist < minDist) {
                                minDist = dist
                                closestTrain = t
                            }
                        }

                        closestTrain?.let {
                            onSelectTrain(it.trainNumber)
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw Grid Lines for High-tech GIS Radar Look
            val gridSpacing = 50f
            for (gx in 0..(width / gridSpacing).toInt()) {
                drawLine(
                    color = GridTrackColor.copy(alpha = 0.15f),
                    start = Offset(gx * gridSpacing, 0f),
                    end = Offset(gx * gridSpacing, height),
                    strokeWidth = 1f
                )
            }
            for (gy in 0..(height / gridSpacing).toInt()) {
                drawLine(
                    color = GridTrackColor.copy(alpha = 0.15f),
                    start = Offset(0f, gy * gridSpacing),
                    end = Offset(width, gy * gridSpacing),
                    strokeWidth = 1f
                )
            }

            // 2. Draw Major Railway Trunk Lines (Corridors)
            val corridors = listOf(
                // Delhi - Chennai Golden Diagonal
                listOf("NDLS", "AGC", "GWL", "VGLJ", "BPL", "ET", "NGP", "BPQ", "WL", "BZA", "MAS"),
                // Delhi - Howrah Main Line
                listOf("NDLS", "CNB", "PRYJ", "DDU", "PNBE", "HWH"),
                // Delhi - Mumbai Central Western Line
                listOf("NDLS", "JP", "ADI", "BRC", "ST", "BCT"),
                // Mumbai - Pune Central
                listOf("BCT", "CSMT", "PUNE"),
                // Bangalore - Nagpur Link
                listOf("SBC", "BPQ"),
                // Hyderabad Link
                listOf("WL", "HYB")
            )

            corridors.forEach { corridor ->
                val path = Path()
                var first = true
                corridor.forEach { code ->
                    val st = stations.find { it.code == code }
                    if (st != null) {
                        val pos = toScreenOffset(st.latitude, st.longitude, width, height)
                        if (first) {
                            path.moveTo(pos.x, pos.y)
                            first = false
                        } else {
                            path.lineTo(pos.x, pos.y)
                        }
                    }
                }

                // Glow track
                drawPath(
                    path = path,
                    color = RailBlue400.copy(alpha = 0.35f),
                    style = Stroke(width = 3.5f)
                )
                // Center rail track dashed
                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.75f),
                    style = Stroke(
                        width = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )
                )
            }

            // 3. Draw Station Nodes
            stations.forEach { station ->
                val pos = toScreenOffset(station.latitude, station.longitude, width, height)
                // Outer ring
                drawCircle(
                    color = RailNavy900,
                    radius = 5.5f,
                    center = pos
                )
                drawCircle(
                    color = RailBlue400,
                    radius = 4f,
                    center = pos
                )
                drawCircle(
                    color = Color.White,
                    radius = 2f,
                    center = pos
                )

                // Station label
                val textLayout = textMeasurer.measure(
                    text = station.code,
                    style = TextStyle(
                        color = RailTextSecondary.copy(alpha = 0.85f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(pos.x + 6f, pos.y - 6f)
                )
            }

            // 4. Draw Moving Train Markers with Status Colors
            trains.forEach { train ->
                val isSelected = selectedTrain?.trainNumber == train.trainNumber
                val trainPos = toScreenOffset(train.latitude, train.longitude, width, height)

                val trainColor = when (train.status) {
                    TrainStatus.ON_TIME -> RailGreenOnTime
                    TrainStatus.MINOR_DELAY -> RailYellowMinor
                    TrainStatus.MODERATE_DELAY -> RailOrangeMod
                    TrainStatus.SEVERE_DELAY -> RailRedSevere
                    TrainStatus.RECOVERING -> RailTealAccent
                }

                // Pulsing wave beacon
                drawCircle(
                    color = trainColor.copy(alpha = pulseAlpha),
                    radius = if (isSelected) pulseRadius * 1.4f else pulseRadius,
                    center = trainPos
                )

                // Selection Halo ring
                if (isSelected) {
                    drawCircle(
                        color = RailTealAccent,
                        radius = 16f,
                        center = trainPos,
                        style = Stroke(width = 2.5f)
                    )
                }

                // Main Train Pin
                drawCircle(
                    color = RailNavy900,
                    radius = if (isSelected) 10f else 8f,
                    center = trainPos
                )
                drawCircle(
                    color = trainColor,
                    radius = if (isSelected) 8f else 6f,
                    center = trainPos
                )
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 3.5f else 2.5f,
                    center = trainPos
                )

                // Train Number Label
                val labelLayout = textMeasurer.measure(
                    text = "${train.trainNumber} (${train.speedKmh}k)",
                    style = TextStyle(
                        color = if (isSelected) RailTealAccent else RailTextPrimary,
                        fontSize = if (isSelected) 11.sp else 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                )
                // Label background pill
                drawRoundRect(
                    color = RailNavy800.copy(alpha = 0.9f),
                    topLeft = Offset(trainPos.x - labelLayout.size.width / 2f - 4f, trainPos.y - 24f),
                    size = androidx.compose.ui.geometry.Size(labelLayout.size.width + 8f, labelLayout.size.height + 4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
                drawText(
                    textLayoutResult = labelLayout,
                    topLeft = Offset(trainPos.x - labelLayout.size.width / 2f, trainPos.y - 22f)
                )
            }
        }

        // Top Status Bar Overlay inside Map
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .background(RailNavy800.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                .border(1.dp, RailNavy600, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(RailTealAccent)
            )
            Text(
                text = "LIVE IR TELEMETRY & ROUTE MAP",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.6.sp
                ),
                color = RailTextPrimary
            )
        }

        // Bottom Map Legend
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .background(RailNavy800.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(1.dp, RailNavy600, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem("On Time", RailGreenOnTime)
            LegendItem("Minor", RailYellowMinor)
            LegendItem("Mod", RailOrangeMod)
            LegendItem("Severe", RailRedSevere)
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = RailTextSecondary
        )
    }
}
