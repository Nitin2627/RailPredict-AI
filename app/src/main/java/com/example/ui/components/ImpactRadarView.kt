package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.stringResource

/**
 * Signature RailPredict AI "Network Effect" Component
 *
 * Visualizes how an upstream rail disruption creates expanding ripple waves
 * affecting subsequent trains, key junctions, and platform dwell times.
 */
@Composable
fun ImpactRadarView(
    disruptionTitle: String = "Freight Overrun at Pt Deen Dayal Upadhyaya",
    affectedTrainsCount: Int = 6,
    affectedStationsCount: Int = 4,
    minDelayImpact: Int = 5,
    maxDelayImpact: Int = 18,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val infiniteTransition = rememberInfiniteTransition(label = "impact_waves")

    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave3"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("impact_radar_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.5.dp, RailOrangeMod.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RailOrangeMod.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = RailOrangeMod.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = RailOrangeMod,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = strings.networkEffect.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            ),
                            color = RailTextPrimary
                        )
                        Text(
                            text = strings.networkEffectSub,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = RailTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RailOrangeMod.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, RailOrangeMod)
                ) {
                    Text(
                        text = strings.live,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = RailOrangeMod,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Radar Visualizing Canvas with Expanding Waves
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(RailNavy900)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val maxRadius = size.height * 0.75f

                    // Concentric background grid circles
                    drawCircle(color = RailNavy700.copy(alpha = 0.5f), radius = maxRadius * 0.33f, center = center, style = Stroke(1.dp.toPx()))
                    drawCircle(color = RailNavy700.copy(alpha = 0.5f), radius = maxRadius * 0.66f, center = center, style = Stroke(1.dp.toPx()))
                    drawCircle(color = RailNavy700.copy(alpha = 0.5f), radius = maxRadius, center = center, style = Stroke(1.dp.toPx()))

                    // Grid crosshairs
                    drawLine(color = RailNavy700.copy(alpha = 0.4f), start = Offset(center.x - maxRadius, center.y), end = Offset(center.x + maxRadius, center.y), strokeWidth = 1.dp.toPx())
                    drawLine(color = RailNavy700.copy(alpha = 0.4f), start = Offset(center.x, center.y - maxRadius), end = Offset(center.x, center.y + maxRadius), strokeWidth = 1.dp.toPx())

                    // Animated expanding wave rings
                    fun drawWave(progress: Float) {
                        val r = maxRadius * progress
                        val alpha = (1f - progress).coerceIn(0f, 1f)
                        drawCircle(
                            color = RailOrangeMod.copy(alpha = 0.55f * alpha),
                            radius = r,
                            center = center,
                            style = Stroke(2.5.dp.toPx())
                        )
                    }

                    drawWave(wave1)
                    drawWave(wave2)
                    drawWave(wave3)

                    // Epicenter pulse
                    drawCircle(color = RailRedSevere.copy(alpha = 0.4f), radius = 14.dp.toPx(), center = center)
                    drawCircle(color = RailRedSevere, radius = 6.dp.toPx(), center = center)
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = center)

                    // Surrounding affected nodes dots
                    val nodeOffsets = listOf(
                        Offset(center.x - maxRadius * 0.5f, center.y - maxRadius * 0.3f),
                        Offset(center.x + maxRadius * 0.6f, center.y - maxRadius * 0.2f),
                        Offset(center.x - maxRadius * 0.4f, center.y + maxRadius * 0.45f),
                        Offset(center.x + maxRadius * 0.5f, center.y + maxRadius * 0.35f),
                        Offset(center.x + maxRadius * 0.2f, center.y - maxRadius * 0.6f)
                    )

                    nodeOffsets.forEach { nodePos ->
                        drawCircle(color = RailYellowMinor.copy(alpha = 0.3f), radius = 6.dp.toPx(), center = nodePos)
                        drawCircle(color = RailYellowMinor, radius = 3.5.dp.toPx(), center = nodePos)
                    }
                }

                // Center Disruption Origin Tag
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = RailNavy900.copy(alpha = 0.85f),
                    border = BorderStroke(0.5.dp, RailRedSevere),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = "EPICENTER: $disruptionTitle",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = RailRedSevere,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Impact Step Sequence (The 4-Step Chain Story)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metric 1: Affected Trains
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RailNavy900,
                    border = BorderStroke(1.dp, RailNavy600),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$affectedTrainsCount",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = RailOrangeMod
                        )
                        Text(
                            text = strings.trains,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = RailTextSecondary
                        )
                    }
                }

                // Metric 2: Affected Stations
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RailNavy900,
                    border = BorderStroke(1.dp, RailNavy600),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$affectedStationsCount",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = RailYellowMinor
                        )
                        Text(
                            text = strings.stations,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = RailTextSecondary
                        )
                    }
                }

                // Metric 3: Ripple Delay Window
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RailNavy900,
                    border = BorderStroke(1.dp, RailNavy600),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "+${minDelayImpact}–${maxDelayImpact}m",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            color = RailRedSevere
                        )
                        Text(
                            text = strings.possibleDelay,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = RailTextSecondary
                        )
                    }
                }
            }
        }
    }
}
