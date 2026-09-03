package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Train
import com.example.model.TrainStatus
import com.example.ui.theme.*
import com.example.util.stringResource

/**
 * Signature RailPredict AI "Arrival Time Change" Component
 *
 * Dynamically visualizes the arrival prediction band, confidence interval,
 * and real-time drift trajectory (Recovering, Stabilizing, or Increasing delay).
 */
@Composable
fun EtaDriftRadar(
    train: Train,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    // Dynamic calculation of the arrival window
    val delayMinutes = train.currentDelayMinutes
    val isRecovering = train.status == TrainStatus.RECOVERING
    val isOnTime = delayMinutes <= 0

    val minArrivalMins = (delayMinutes - 4).coerceAtLeast(0)
    val maxArrivalMins = delayMinutes + 6

    val driftInterpretation = when {
        isRecovering -> "Train is recovering time"
        delayMinutes > 25 -> "Delay risk increasing ahead"
        isOnTime -> "Arrival timeline is on schedule"
        else -> "Arrival time is stabilizing"
    }

    val driftColor = when {
        isRecovering -> RailTealAccent
        isOnTime -> RailGreenOnTime
        delayMinutes > 25 -> RailRedSevere
        else -> RailYellowMinor
    }

    val driftIcon = when {
        isRecovering -> Icons.Default.TrendingDown
        delayMinutes > 25 -> Icons.Default.TrendingUp
        else -> Icons.Default.TrendingFlat
    }

    // Animation for pulse & radar band sweep
    val infiniteTransition = rememberInfiniteTransition(label = "radar_drift_anim")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("eta_drift_radar_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.5.dp, RailPurpleAI.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RailPurpleAI.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Level 1 Identity & Confidence Tag
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
                        color = RailPurpleAI.copy(alpha = 0.2f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = RailPurpleAI,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = strings.arrivalTimeChange.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            ),
                            color = RailTextPrimary
                        )
                        Text(
                            text = strings.predictedArrivalWindow,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = RailTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = RailTealAccent.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, RailTealAccent.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = RailTealAccent,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${train.confidenceScore}% Confidence",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            ),
                            color = RailTealAccent
                        )
                    }
                }
            }

            // Radar Prediction Band Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(RailNavy900)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2

                    // Baseline track
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, centerY),
                        end = Offset(width, centerY),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Target ETA Dot Position (normalized 0.25 to 0.75 based on delay)
                    val targetProgress = if (isOnTime) 0.35f else (0.35f + (delayMinutes.toFloat() / 100f).coerceIn(0f, 0.45f))
                    val targetX = width * targetProgress

                    // Prediction Confidence Band Range
                    val bandStart = (targetX - width * 0.18f).coerceAtLeast(10f)
                    val bandEnd = (targetX + width * 0.18f).coerceAtMost(width - 10f)

                    // Active Prediction Band
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                RailPurpleAI.copy(alpha = 0.25f),
                                RailTealAccent.copy(alpha = 0.45f * pulseAlpha),
                                RailPurpleAI.copy(alpha = 0.25f)
                            ),
                            startX = bandStart,
                            endX = bandEnd
                        ),
                        topLeft = Offset(bandStart, centerY - 14.dp.toPx()),
                        size = Size(bandEnd - bandStart, 28.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
                    )

                    // Radar sweep highlight line
                    val sweepX = bandStart + (bandEnd - bandStart) * sweepProgress
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = Offset(sweepX, centerY - 12.dp.toPx()),
                        end = Offset(sweepX, centerY + 12.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Scheduled Anchor Tick (Level 2 marker)
                    val scheduledX = width * 0.35f
                    drawLine(
                        color = RailTextTertiary,
                        start = Offset(scheduledX, centerY - 10.dp.toPx()),
                        end = Offset(scheduledX, centerY + 10.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Current Predicted ETA Point (Level 1 highlight)
                    drawCircle(
                        color = driftColor.copy(alpha = 0.3f),
                        radius = 12.dp.toPx() * pulseAlpha,
                        center = Offset(targetX, centerY)
                    )
                    drawCircle(
                        color = driftColor,
                        radius = 6.dp.toPx(),
                        center = Offset(targetX, centerY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.5.dp.toPx(),
                        center = Offset(targetX, centerY)
                    )
                }

                // Top & Bottom Label Overlays
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Early: ${train.predictedDestinationETA.take(3)}${minArrivalMins.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = RailTextTertiary
                    )
                    Text(
                        text = "Likely: ${train.predictedDestinationETA}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        ),
                        color = driftColor
                    )
                    Text(
                        text = "Late: ${train.predictedDestinationETA.take(3)}${maxArrivalMins.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = RailTextTertiary
                    )
                }
            }

            // Radar Drift Interpretation Banner (Human Language)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = driftColor.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, driftColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = driftIcon,
                        contentDescription = null,
                        tint = driftColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = driftInterpretation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = driftColor
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (isOnTime) "Stable" else if (isRecovering) "-4m delta" else "+${delayMinutes}m delay",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        ),
                        color = driftColor
                    )
                }
            }
        }
    }
}
