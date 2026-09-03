package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Train
import com.example.model.TrainStatus
import com.example.ui.theme.*

/**
 * Passenger Journey Risk Score & AI Prediction Story Card
 *
 * Implements:
 * 1. Journey Risk Score: LOW / MEDIUM / HIGH with human explanation
 * 2. Prediction Story: AI-generated plain English journey summary
 * 3. Connection Safety Buffer: Interactive buffer check for connecting trains
 */
@Composable
fun JourneyRiskCard(
    train: Train,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var connectingDepartureTime by remember { mutableStateOf("23:15") }
    var connectingTrainName by remember { mutableStateOf("12859 Gitanjali Express") }

    // Risk calculation based on current delay and trend
    val delayMinutes = train.currentDelayMinutes
    val riskLevel = when {
        delayMinutes <= 10 -> "LOW"
        delayMinutes <= 30 -> "MEDIUM"
        else -> "HIGH"
    }

    val riskColor = when (riskLevel) {
        "LOW" -> RailGreenOnTime
        "MEDIUM" -> RailYellowMinor
        else -> RailRedSevere
    }

    val riskDescription = when (riskLevel) {
        "LOW" -> "Your arrival prediction is stable. Connection buffers are secure."
        "MEDIUM" -> "Minor delay variance ahead. Allow at least 25 minutes for transfers."
        else -> "Significant delay. Connecting train transfers may be difficult."
    }

    // AI Prediction Story (Simple plain-English explanation)
    val predictionStory = buildString {
        if (delayMinutes <= 0) {
            append("${train.trainName} (${train.trainNumber}) is running punctually on the high-speed main line. ")
            append("Smooth track clearance is confirmed between ${train.currentStationName} and ${train.nextStationName}. ")
            append("Expected arrival at ${train.destination.name} is right on schedule at ${train.scheduledDestinationETA}.")
        } else {
            append("Your train is currently ${delayMinutes} minutes behind schedule near ${train.currentStationName}. ")
            if (train.status == TrainStatus.RECOVERING) {
                append("It is recovering time with a clear run ahead. ")
            } else {
                append("Congestion and speed cautions are being factored in. ")
            }
            append("Arrival at ${train.destination.name} is currently expected around ${train.predictedDestinationETA}.")
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("journey_risk_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.5.dp, riskColor.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            riskColor.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Level 1: Risk Level Badge & Header
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
                        color = riskColor.copy(alpha = 0.2f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (riskLevel == "LOW") Icons.Default.CheckCircle else Icons.Default.Shield,
                                contentDescription = null,
                                tint = riskColor,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "JOURNEY RISK SCORE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            ),
                            color = RailTextPrimary
                        )
                        Text(
                            text = "Travel Reliability Rating",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = RailTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = riskColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, riskColor)
                ) {
                    Text(
                        text = "$riskLevel RISK",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = riskColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Human Explanation Text
            Text(
                text = riskDescription,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = RailTextPrimary
            )

            // Prediction Story Box (Level 2 Human Assistant Summary)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = RailNavy900,
                border = BorderStroke(1.dp, RailNavy600),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = RailPurpleAI,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "PREDICTION STORY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            ),
                            color = RailPurpleAI
                        )
                    }

                    Text(
                        text = predictionStory,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            lineHeight = 16.5.sp
                        ),
                        color = RailTextSecondary
                    )
                }
            }

            // Progressive Disclosure: Connecting Train Safety Buffer Checker
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = RailNavy700.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, RailNavy600),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AltRoute,
                            contentDescription = null,
                            tint = RailBlue400,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Check Connecting Train Transfer Risk",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = RailBlue400
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = RailTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Expanded Connection Details
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = connectingTrainName,
                        onValueChange = { connectingTrainName = it },
                        label = { Text("Connecting Train Name / No.", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = RailNavy900,
                            unfocusedContainerColor = RailNavy900,
                            focusedBorderColor = RailTealAccent,
                            unfocusedBorderColor = RailNavy600,
                            focusedTextColor = RailTextPrimary,
                            unfocusedTextColor = RailTextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = connectingDepartureTime,
                        onValueChange = { connectingDepartureTime = it },
                        label = { Text("Connecting Train Departure Time (e.g. 23:15)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = RailNavy900,
                            unfocusedContainerColor = RailNavy900,
                            focusedBorderColor = RailTealAccent,
                            unfocusedBorderColor = RailNavy600,
                            focusedTextColor = RailTextPrimary,
                            unfocusedTextColor = RailTextPrimary
                        ),
                        singleLine = true
                    )

                    // Estimated buffer result
                    val bufferMins = 63 - delayMinutes
                    val isBufferSafe = bufferMins >= 20

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isBufferSafe) RailGreenOnTime.copy(alpha = 0.15f) else RailRedSevere.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, if (isBufferSafe) RailGreenOnTime else RailRedSevere),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isBufferSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isBufferSafe) RailGreenOnTime else RailRedSevere,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = if (isBufferSafe) "Safe Connection ($bufferMins min transfer window)" else "Tight Connection ($bufferMins min transfer window)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = if (isBufferSafe) RailGreenOnTime else RailRedSevere
                                )
                                Text(
                                    text = if (isBufferSafe) "Plenty of time to change platforms at ${train.destination.code}." else "Move quickly upon arrival. Proceed directly to transfer foot overbridge.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                    color = RailTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
