package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CongestedSection
import com.example.model.HotspotStation
import com.example.ui.components.AccuracyComparisonCard
import com.example.ui.components.DelayTrendChart
import com.example.ui.theme.*
import com.example.viewmodel.RailPredictViewModel

@Composable
fun NetworkIntelligenceScreen(
    viewModel: RailPredictViewModel,
    modifier: Modifier = Modifier
) {
    val congestedSections by viewModel.congestedSections.collectAsState()
    val hotspotStations by viewModel.hotspotStations.collectAsState()
    val networkKpis by viewModel.networkOverview.collectAsState()
    val scrollState = rememberScrollState()

    var selectedHorizon by remember { mutableStateOf("+30 min") }
    val forecastHorizons = listOf("+15 min", "+30 min", "+45 min", "+60 min")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RailNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = RailTealAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "NETWORK OPERATIONS INTELLIGENCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = RailTextSecondary
                    )
                }
                Text(
                    text = "Section Capacity & Bottleneck Analytics",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = RailTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RailNavy800,
                border = BorderStroke(1.dp, RailNavy600)
            ) {
                Text(
                    text = "IR Network Grid",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = RailBlue400,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // NEXT 60 MINUTES FUTURE RISK MAP / FORECAST (Requirement #7)
        Card(
            modifier = Modifier
                .testTag("future_risk_map_card")
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailPurpleAI.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = RailPurpleAI, modifier = Modifier.size(18.dp))
                        Text(
                            text = "NEXT 60 MINUTES FUTURE RISK MAP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = RailTextPrimary
                        )
                    }

                    Text(
                        text = "Dynamic Predictive Layer",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = RailTealAccent
                    )
                }

                // Time Horizon Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    forecastHorizons.forEach { horizon ->
                        val isSelected = horizon == selectedHorizon
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) RailPurpleAI else RailNavy900,
                            border = BorderStroke(1.5.dp, if (isSelected) RailPurpleAI else RailNavy600),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 44.dp)
                                .clickable { selectedHorizon = horizon }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = horizon,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) Color.White else RailTextSecondary,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Dynamic Horizon Forecast Output
                val horizonData = when (selectedHorizon) {
                    "+15 min" -> Triple("72%", "+6 to +10 min", "Raipur → Bhatapara entry block")
                    "+30 min" -> Triple("78%", "+10 to +16 min", "Bhopal → Itarsi & Bilaspur junction")
                    "+45 min" -> Triple("85%", "+14 to +22 min", "Nagpur bottleneck junction holding lines")
                    else -> Triple("91%", "+18 to +28 min", "Central Corridor multi-train cascade zone")
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RailNavy900,
                    border = BorderStroke(1.dp, RailNavy600)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("TOO MANY TRAINS AHEAD RISK", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RailTextTertiary)
                            Text(horizonData.first, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = RailOrangeMod)
                        }
                        Column {
                            Text("EXPECTED DELAY IMPACT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RailTextTertiary)
                            Text(horizonData.second, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = RailRedSevere)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("FORECAST CONFIDENCE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RailTextTertiary)
                            Text("89% (High)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = RailTealAccent)
                        }
                    }
                }
            }
        }

        // AI RECOMMENDATION ENGINE & SAFETY MANDATE (Requirements #9 & #30)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailTealAccent.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Recommend, contentDescription = null, tint = RailTealAccent, modifier = Modifier.size(18.dp))
                        Text(
                            text = "AI DECISION SUPPORT RECOMMENDATIONS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = RailTextPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = RailYellowMinor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, RailYellowMinor)
                    ) {
                        Text(
                            text = "Advisory Only",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = RailYellowMinor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Safety Compliance Notice Banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RailNavy900,
                    border = BorderStroke(1.dp, RailNavy600)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = RailTealAccent, modifier = Modifier.size(14.dp))
                        Text(
                            text = "AI Decision Support — Human Approval Required (No Direct Signal/Interlock Control)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = RailTealAccent
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "• Preemptively review platform allocation at Itarsi Junction for Train 12860.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = RailTextSecondary
                    )
                    Text(
                        text = "• Hold Freight rake G-889 in Budni loop siding to allow Rajdhani precedence.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = RailTextSecondary
                    )
                    Text(
                        text = "• Estimated potential network delay reduction: approximately 9 to 14 minutes.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                        color = RailGreenOnTime
                    )
                }
            }
        }

        // 24 Hour Delay Trend Chart
        DelayTrendChart(modifier = Modifier.testTag("net_intel_delay_chart"))

        // Accuracy & Error Benchmark
        AccuracyComparisonCard(modifier = Modifier.testTag("net_intel_accuracy_card"))

        // Congested Track Sections
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "SECTION PERFORMANCE INTELLIGENCE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                ),
                color = RailTextSecondary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                congestedSections.forEach { section ->
                    CongestedSectionCard(section = section)
                }
            }
        }

        // Delay Hotspot Junctions
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "CRITICAL BOTTLENECK JUNCTIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                ),
                color = RailTextSecondary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                hotspotStations.forEach { hotspot ->
                    HotspotJunctionCard(hotspot = hotspot)
                }
            }
        }

        // TECHNICAL ML VIEW (Requirement #35)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TECHNICAL ML ARCHITECTURE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = RailPurpleAI
                    )
                    Text(
                        text = "RailPredict ETA v1.4",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = RailTealAccent
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Feature Set", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RailTextTertiary)
                        Text("18 Telemetry Weights", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = RailTextPrimary)
                    }
                    Column {
                        Text("Inference Latency", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RailTextTertiary)
                        Text("42ms (Edge/JVM)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = RailGreenOnTime)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Data Freshness", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RailTextTertiary)
                        Text("5 seconds (Stream)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = RailBlue400)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CongestedSectionCard(section: CongestedSection) {
    val barRatio = (section.capacityUtilizationPercent / 100f).coerceIn(0f, 1f)
    val barColor = when {
        section.capacityUtilizationPercent > 85 -> RailRedSevere
        section.capacityUtilizationPercent > 70 -> RailOrangeMod
        else -> RailYellowMinor
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = RailNavy800,
        border = BorderStroke(1.dp, RailNavy600),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = section.sectionName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = RailTextPrimary
                    )
                    Text(
                        text = "Zone: ${section.zone} • ${section.activeTrainsCount} active rakes",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = RailTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = barColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, barColor)
                ) {
                    Text(
                        text = "${section.capacityUtilizationPercent}% LOAD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = barColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(RailNavy900)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(barRatio)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(barColor)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Avg Speed: ${section.averageSpeedKmh} km/h • Peak: 6 PM - 9 PM",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = RailTextTertiary
                )
                Text(
                    text = "Delay Impact: +${section.avgSectionalDelayMin} min",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = barColor
                )
            }
        }
    }
}

@Composable
private fun HotspotJunctionCard(hotspot: HotspotStation) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = RailNavy800,
        border = BorderStroke(1.dp, RailNavy600),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${hotspot.stationName} (${hotspot.stationCode})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = RailTextPrimary
                    )
                    Text(
                        text = "Zone: ${hotspot.zone} • ${hotspot.dailyTrafficTrains} Trains/day • ${hotspot.platformOccupancyRate}% Platform Load",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = RailTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = RailOrangeMod.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, RailOrangeMod)
                ) {
                    Text(
                        text = "+${hotspot.avgDwellOverrunMin}m Dwell",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = RailOrangeMod,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "Primary Bottleneck Factor: ${hotspot.primaryDelayCause}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = RailTextTertiary
            )
        }
    }
}
