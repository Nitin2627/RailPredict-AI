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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Visual Delay Ripple Chain Component
 *
 * Implements the linear visual story:
 * Primary Delayed Train (Train A) -> Delay Event -> Critical Junction -> Trailing Express (Train B) -> Connecting Local (Train C)
 */
@Composable
fun DelayRippleChainView(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "delay_chain_flow")
    val flowProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow_progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("delay_ripple_chain_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.5.dp, RailOrangeMod.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RailOrangeMod.copy(alpha = 0.08f),
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
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = RailOrangeMod,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "DELAY RIPPLE CHAIN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            ),
                            color = RailTextPrimary
                        )
                        Text(
                            text = "Cascading Junction Block Impact",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = RailTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = RailOrangeMod.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, RailOrangeMod.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "3 Trains Queued",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        ),
                        color = RailOrangeMod,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Animated Visual Linear Flow Chain
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RailNavy900)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Step 1: Root Train (Train A)
                RippleChainStepNode(
                    title = "TRAIN A (Root Source)",
                    identifier = "BOXN Freight #58219",
                    statusText = "+38m Overrun",
                    statusColor = RailRedSevere,
                    icon = Icons.Default.Warning,
                    description = "Speed restriction (30 km/h) & loco air brake pressure test"
                )

                // Connecting Animated Flow Line
                FlowConnectorLine(progress = flowProgress, color = RailRedSevere)

                // Step 2: Junction Block (The Choke Point)
                RippleChainStepNode(
                    title = "JUNCTION CHOKE POINT",
                    identifier = "Pt Deen Dayal Upadhyaya (DDU)",
                    statusText = "Route Locked",
                    statusColor = RailOrangeMod,
                    icon = Icons.Default.AltRoute,
                    description = "Platform 4 & East Diamond interchange occupied for +18 min"
                )

                // Connecting Animated Flow Line
                FlowConnectorLine(progress = flowProgress, color = RailOrangeMod)

                // Step 3: Trailing Express (Train B)
                RippleChainStepNode(
                    title = "TRAIN B (Trailing Primary)",
                    identifier = "12860 Gitanjali Express",
                    statusText = "+18m Secondary Delay",
                    statusColor = RailOrangeMod,
                    icon = Icons.Default.DirectionsTransit,
                    description = "Held at outer home signal waiting for route release"
                )

                // Connecting Animated Flow Line
                FlowConnectorLine(progress = flowProgress, color = RailYellowMinor)

                // Step 4: Connecting Feeder (Train C)
                RippleChainStepNode(
                    title = "TRAIN C (Downstream Feeder)",
                    identifier = "08728 Bilaspur Passenger",
                    statusText = "+12m Rescheduled",
                    statusColor = RailYellowMinor,
                    icon = Icons.Default.CallSplit,
                    description = "Held at Bilaspur PF 2 to maintain passenger connection integrity"
                )
            }
        }
    }
}

@Composable
private fun RippleChainStepNode(
    title: String,
    identifier: String,
    statusText: String,
    statusColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = RailNavy800,
        border = BorderStroke(1.dp, RailNavy600),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = statusColor.copy(alpha = 0.2f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = RailTextTertiary
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        ),
                        color = statusColor
                    )
                }

                Text(
                    text = identifier,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    ),
                    color = RailTextPrimary
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.5.sp,
                        lineHeight = 14.sp
                    ),
                    color = RailTextSecondary
                )
            }
        }
    }
}

@Composable
private fun FlowConnectorLine(
    progress: Float,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val height = size.height

            // Base vertical dotted track
            drawLine(
                color = Color(0xFF334155),
                start = Offset(centerX, 0f),
                end = Offset(centerX, height),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Animated pulse dot
            val dotY = height * progress
            drawCircle(
                color = color,
                radius = 3.5.dp.toPx(),
                center = Offset(centerX, dotY)
            )
        }
    }
}
