package com.example.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.RailPredictViewModel

@Composable
fun ApiIntegrationScreen(
    viewModel: RailPredictViewModel,
    modifier: Modifier = Modifier
) {
    val apiMode by viewModel.apiDataFeedMode.collectAsState()
    val scrollState = rememberScrollState()

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
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = RailBlue400,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "LIVE DATA INTEGRATION ARCHITECTURE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = RailTextSecondary
                    )
                }
                Text(
                    text = "Indian Railways Enterprise Feeds",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = RailTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RailTealAccent.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, RailTealAccent)
            ) {
                Text(
                    text = if (apiMode == "SIMULATED") "SIMULATION ACTIVE" else "CONNECTED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = RailTealAccent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Active Feed Mode Selector
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = RailNavy800,
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "DATA INGESTION SOURCE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = RailTextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeedModeCard(
                        title = "Simulated Radar Telemetry",
                        subtitle = "Autonomous event injection for hackathon evaluation",
                        isActive = apiMode == "SIMULATED",
                        onClick = { viewModel.setApiDataFeedMode("SIMULATED") },
                        modifier = Modifier.weight(1f)
                    )
                    FeedModeCard(
                        title = "Production IR Live API",
                        subtitle = "CRIS / NTES / RTIS Satcom enterprise endpoints",
                        isActive = apiMode == "LIVE_IR_INTEGRATION",
                        onClick = { viewModel.setApiDataFeedMode("LIVE_IR_INTEGRATION") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5 Core IR Live Feeds
        Text(
            text = "ENTERPRISE DATA SOURCES INTEGRATION ROADMAP",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            ),
            color = RailTextSecondary
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ApiSourceCard(
                name = "RTIS (Real-Time Train Information System)",
                provider = "ISRO & CRIS Satcom Locomotives",
                frequency = "30-second continuous GPS & speed vector",
                status = "CONNECTED (Simulated Stream)",
                statusColor = RailGreenOnTime,
                icon = Icons.Default.SatelliteAlt
            )
            ApiSourceCard(
                name = "NTES (National Train Enquiry System)",
                provider = "Centre for Railway Information Systems (CRIS)",
                frequency = "Station arrival/departure logs & schedule master",
                status = "READY FOR AUTH TOKEN",
                statusColor = RailTealAccent,
                icon = Icons.Default.Storage
            )
            ApiSourceCard(
                name = "COA (Control Office Application)",
                provider = "Indian Railways Section Controllers",
                frequency = "Block occupancy, precedence orders & TSR caution notices",
                status = "MAPPED TO ML PIPELINE",
                statusColor = RailPurpleAI,
                icon = Icons.Default.Traffic
            )
            ApiSourceCard(
                name = "IMD Weather Radar API",
                provider = "India Meteorological Department",
                frequency = "Fog visibility index (<100m) & monsoon cloud radar",
                status = "ACTIVE",
                statusColor = RailBlue400,
                icon = Icons.Default.Cloud
            )
        }

        // Disclaimer Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = RailNavy800,
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = RailTealAccent, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Hackathon Production Deployment Notice",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                        color = RailTextPrimary
                    )
                }
                Text(
                    text = "This prototype demonstrates the complete AI prediction and explainability engine using simulated railway telemetry calibrated to real Indian Railways route timetables. In production, secure REST/WebSocket connectors ingest live CRIS RTIS feeds to run inference in sub-second latency.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                    color = RailTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FeedModeCard(
    title: String,
    subtitle: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isActive) RailNavy700 else RailNavy900,
        border = BorderStroke(1.dp, if (isActive) RailTealAccent else RailNavy600)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isActive) RailTealAccent else RailTextTertiary)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = if (isActive) RailTealAccent else RailTextPrimary
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = RailTextSecondary
            )
        }
    }
}

@Composable
private fun ApiSourceCard(
    name: String,
    provider: String,
    frequency: String,
    status: String,
    statusColor: Color,
    icon: ImageVector
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = RailNavy800,
        border = BorderStroke(1.dp, RailNavy600),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RailNavy900,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = RailBlue400, modifier = Modifier.size(20.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = RailTextPrimary
                    )
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = statusColor
                    )
                }
                Text(
                    text = provider,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = RailTextSecondary
                )
                Text(
                    text = frequency,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = RailTextTertiary
                )
            }
        }
    }
}
