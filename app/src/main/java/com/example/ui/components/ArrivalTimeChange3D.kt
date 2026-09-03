package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.stringResource

@Composable
fun ArrivalTimeChange3D(
    earlierEta: String = "09:49 PM",
    newEta: String = "09:55 PM",
    change: String = "+6 min",
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val infiniteTransition = rememberInfiniteTransition(label = "eta_3d")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailPurpleAI.copy(alpha = 0.5f))
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
                Text(
                    text = strings.arrivalTimeChange.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = RailPurpleAI
                )
                Icon(
                    imageVector = Icons.Default.Update,
                    contentDescription = null,
                    tint = RailPurpleAI,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(RailNavy900, RailNavy800)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // 3D Circular Rings
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(rotationX = 60f)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    for (i in 1..3) {
                        drawCircle(
                            color = RailPurpleAI.copy(alpha = 0.2f / i),
                            radius = (40.dp.toPx() + (i * 20).dp.toPx()) * pulse,
                            center = center,
                            style = Stroke(2.dp.toPx())
                        )
                    }
                    
                    // Central Glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(RailPurpleAI.copy(alpha = 0.3f), Color.Transparent),
                            center = center,
                            radius = 80.dp.toPx()
                        ),
                        radius = 80.dp.toPx(),
                        center = center
                    )
                }

                // Central ETA Info
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = newEta,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        ),
                        color = RailTextPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = RailOrangeMod.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, RailOrangeMod)
                    ) {
                        Text(
                            text = change,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = RailOrangeMod
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = earlierEta, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = RailTextSecondary)
                    Text(text = strings.earlierPrediction, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = newEta, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = RailTealAccent)
                    Text(text = strings.newPrediction, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                }
            }
        }
    }
}
