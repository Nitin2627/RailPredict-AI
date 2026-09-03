package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.*
import com.example.util.DateTimeUtil
import com.example.util.Language
import com.example.util.stringResource

@Composable
fun WhyEtaChangedCard(
    train: Train,
    language: Language = Language.ENGLISH,
    modifier: Modifier = Modifier,
    testTag: String = "why_eta_changed_card"
) {
    val strings = stringResource()
    val prevEta = train.previousPredictedETA
    val curEta = train.predictedDestinationETA
    val factors = train.delayFactors
    val netImpact = factors.sumOf { it.impactMinutes }

    Card(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.5.dp, RailPurpleAI.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RailPurpleAI.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Header Row
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
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Explainability",
                                tint = RailPurpleAI,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = strings.arrivalTimeChange.uppercase(),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = RailTextPrimary
                        )
                        Text(
                            text = "AI Reasons for time update",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = RailTextSecondary
                        )
                    }
                }

                // Confidence badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = RailPurpleAI.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, RailPurpleAI.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                                fontSize = 11.sp
                            ),
                            color = RailTealAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ETA Comparison Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = RailNavy900,
                border = BorderStroke(1.dp, RailNavy600)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.earlierPrediction.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = RailTextTertiary
                        )
                        Text(
                            text = DateTimeUtil.formatPassengerTime(prevEta, language).split("•").last().trim(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = RailTextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "to",
                        tint = RailBlue400,
                        modifier = Modifier.size(18.dp)
                    )

                    Column {
                        Text(
                            text = strings.newPrediction.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = RailTextTertiary
                        )
                        Text(
                            text = DateTimeUtil.formatPassengerTime(curEta, language).split("•").last().trim(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black
                            ),
                            color = if (netImpact > 0) RailOrangeMod else RailGreenOnTime
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (netImpact > 0) RailOrangeMod.copy(alpha = 0.2f) else RailGreenOnTime.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if (netImpact > 0) RailOrangeMod else RailGreenOnTime)
                    ) {
                        Text(
                            text = if (netImpact >= 0) "+$netImpact min" else "$netImpact min",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (netImpact > 0) RailOrangeMod else RailGreenOnTime,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CONTRIBUTING FACTORS BREAKDOWN:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = RailTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Factor Items
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                factors.forEach { factor ->
                    val factorColor = when (factor.category) {
                        "CONGESTION" -> RailRedSevere
                        "SPEED_RESTRICTION" -> RailOrangeMod
                        "PRECEDING_TRAIN" -> RailYellowMinor
                        "WEATHER" -> RailBlue400
                        "RECOVERY" -> RailGreenOnTime
                        else -> RailTextSecondary
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(RailNavy700.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(factorColor)
                            )
                            Column {
                                Text(
                                    text = factor.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    ),
                                    color = RailTextPrimary
                                )
                                Text(
                                    text = factor.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp
                                    ),
                                    color = RailTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }

                        Text(
                            text = if (factor.impactMinutes > 0) "+${factor.impactMinutes} min" else "${factor.impactMinutes} min",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = factorColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Net Impact Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Net Dynamic Delay Impact",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = RailTextTertiary
                )
                Text(
                    text = if (netImpact >= 0) "+$netImpact minutes" else "$netImpact minutes",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (netImpact > 0) RailOrangeMod else RailGreenOnTime
                )
            }
        }
    }
}
