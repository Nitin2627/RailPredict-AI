package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.stringResource

@Composable
fun NetworkImpact3D(
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val infiniteTransition = rememberInfiniteTransition(label = "impact_3d")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_alpha"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.networkEffect.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = RailTextPrimary
                    )
                    Text(
                        text = "Medium",
                        style = MaterialTheme.typography.bodySmall,
                        color = RailOrangeMod
                    )
                }
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = RailOrangeMod,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(RailNavy700, RailNavy900),
                            center = Offset.Unspecified,
                            radius = 400f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(rotationX = 45f)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    // Layered Rings (Depth)
                    for (i in 1..4) {
                        drawCircle(
                            color = RailNavy600.copy(alpha = 0.3f),
                            radius = 30.dp.toPx() * i,
                            center = center,
                            style = Stroke(1.dp.toPx())
                        )
                    }

                    // Scan Line
                    val scanLineLength = 120.dp.toPx()
                    val scanX = center.x + Math.cos(Math.toRadians(rotation.toDouble())).toFloat() * scanLineLength
                    val scanY = center.y + Math.sin(Math.toRadians(rotation.toDouble())).toFloat() * scanLineLength
                    
                    drawLine(
                        color = RailOrangeMod.copy(alpha = 0.5f),
                        start = center,
                        end = Offset(scanX, scanY),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Glow Effect
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(RailOrangeMod.copy(alpha = 0.2f), Color.Transparent),
                            center = center,
                            radius = 60.dp.toPx()
                        ),
                        radius = 60.dp.toPx(),
                        center = center
                    )

                    // Central Train (Modern Icon)
                    drawCircle(
                        color = RailTealAccent,
                        radius = 8.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = center
                    )

                    // Nearby Trains
                    val trainPositions = listOf(
                        Offset(center.x - 60.dp.toPx(), center.y - 40.dp.toPx()),
                        Offset(center.x + 70.dp.toPx(), center.y + 20.dp.toPx())
                    )

                    trainPositions.forEach { pos ->
                        drawCircle(
                            color = RailOrangeMod.copy(alpha = waveAlpha),
                            radius = 6.dp.toPx(),
                            center = pos
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.8f),
                            radius = 2.dp.toPx(),
                            center = pos
                        )
                    }
                }
            }

            Text(
                text = strings.trainsAheadAffectArrival,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = RailTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
