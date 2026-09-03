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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StopStatus
import com.example.model.Train
import com.example.model.TrainStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.DateTimeUtil
import com.example.util.Language
import com.example.util.LanguageManager
import com.example.util.stringResource
import com.example.viewmodel.RailPredictViewModel

enum class DashboardMetric { SPEED, DELAY, NEXT_STATION, ETA }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: RailPredictViewModel,
    onNavigateToTrainDetails: (String) -> Unit,
    onNavigateToAiPrediction: () -> Unit,
    onNavigateToTracking: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trains by viewModel.trains.collectAsState()
    val selectedTrain by viewModel.selectedTrain.collectAsState()
    val isSimRunning by viewModel.isSimulationRunning.collectAsState()
    val networkKpi by viewModel.networkOverview.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    var showSimEventDialog by remember { mutableStateOf(false) }
    var selectedMetricDetail by remember { mutableStateOf<DashboardMetric?>(null) }
    val scrollState = rememberScrollState()
    
    val context = LocalContext.current
    val languageManager = remember { LanguageManager(context) }
    val currentLanguage by languageManager.languageFlow.collectAsState(initial = Language.ENGLISH)
    val strings = stringResource()

    if (showSimEventDialog) {
        SimulateEventDialog(
            trainNumber = selectedTrain.trainNumber,
            onDismiss = { showSimEventDialog = false },
            onSelectEvent = { eventType ->
                viewModel.triggerSimulationEvent(eventType, selectedTrain.trainNumber)
            }
        )
    }

    if (selectedMetricDetail != null) {
        MetricDetailBottomSheet(
            metric = selectedMetricDetail!!,
            train = selectedTrain,
            language = currentLanguage,
            onDismiss = { selectedMetricDetail = null }
        )
    }

    val trainsAtRiskCount = remember(trains) {
        trains.count { it.status == TrainStatus.SEVERE_DELAY || it.status == TrainStatus.MODERATE_DELAY }
    }

    val avgNetworkDelay = remember(trains) {
        if (trains.isEmpty()) 0 else trains.sumOf { it.currentDelayMinutes } / trains.size
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RailNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Control Center Header
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = RailNavy800,
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = RailTealAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = RailTealAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = "OPERATIONS CONTROL ROOM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp
                            ),
                            color = RailTealAccent
                        )
                    }
                    Text(
                        text = "Network Live Overview",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = RailTextPrimary
                    )
                }

                // Controls: Live Sim Toggle & Simulate Event CTA
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSimRunning) RailGreenOnTime.copy(alpha = 0.15f) else RailYellowMinor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, if (isSimRunning) RailGreenOnTime else RailYellowMinor),
                        modifier = Modifier
                            .testTag("btn_toggle_sim")
                            .defaultMinSize(minHeight = 40.dp)
                            .clickable { viewModel.toggleSimulation() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSimRunning) RailGreenOnTime else RailYellowMinor)
                            )
                            Text(
                                text = if (isSimRunning) "LIVE" else "PAUSED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (isSimRunning) RailGreenOnTime else RailYellowMinor
                            )
                        }
                    }

                    Button(
                        onClick = { showSimEventDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RailOrangeMod),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier
                            .testTag("btn_simulate_event")
                            .defaultMinSize(minHeight = 40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Simulate",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4 PRIMARY KPI CARDS (Summary Boxes)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiCard(
                title = strings.speed,
                value = "${selectedTrain.speedKmh} km/h",
                subtitle = "Active Telemetry",
                icon = Icons.Default.Speed,
                accentColor = RailTealAccent,
                onClick = { selectedMetricDetail = DashboardMetric.SPEED },
                modifier = Modifier.weight(1f),
                testTag = "kpi_speed"
            )
            KpiCard(
                title = strings.currentDelay,
                value = "+${selectedTrain.currentDelayMinutes}m",
                subtitle = strings.late.uppercase(),
                icon = Icons.Default.Schedule,
                accentColor = RailOrangeMod,
                onClick = { selectedMetricDetail = DashboardMetric.DELAY },
                modifier = Modifier.weight(1f),
                testTag = "kpi_delay"
            )
            KpiCard(
                title = strings.nextStation,
                value = selectedTrain.nextStationName.take(12),
                subtitle = "PF ${selectedTrain.nextPlatform}",
                icon = Icons.Default.LocationOn,
                accentColor = RailBlue400,
                onClick = { selectedMetricDetail = DashboardMetric.NEXT_STATION },
                modifier = Modifier.weight(1f),
                testTag = "kpi_next_station"
            )
            KpiCard(
                title = "Arrival",
                value = selectedTrain.predictedDestinationETA,
                subtitle = "${selectedTrain.confidenceScore}% CONF",
                icon = Icons.Default.AutoAwesome,
                accentColor = RailPurpleAI,
                onClick = { selectedMetricDetail = DashboardMetric.ETA },
                modifier = Modifier.weight(1f),
                testTag = "kpi_eta"
            )
        }

        // ==========================================
        // SECTION 1: WHAT IS HAPPENING?
        // ==========================================
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = RailTealAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "WHAT IS HAPPENING?",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        color = RailTextPrimary
                    )
                }

                Text(
                    text = "Live Fleet Map & Active Trains",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = RailTextSecondary
                )
            }

            // Live GIS Map
            LiveRailwayMap(
                trains = trains,
                selectedTrain = selectedTrain,
                onSelectTrain = { num -> viewModel.selectTrain(num) },
                isFullHeight = false
            )

            // Monitored Trains Horizontal Strip
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(trains) { train ->
                    val isSelected = train.trainNumber == selectedTrain.trainNumber
                    val statusColor = when (train.status) {
                        TrainStatus.ON_TIME -> RailGreenOnTime
                        TrainStatus.MINOR_DELAY -> RailYellowMinor
                        TrainStatus.MODERATE_DELAY -> RailOrangeMod
                        TrainStatus.SEVERE_DELAY -> RailRedSevere
                        TrainStatus.RECOVERING -> RailTealAccent
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) RailNavy700 else RailNavy800,
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) RailTealAccent else RailNavy600),
                        modifier = Modifier
                            .testTag("carousel_train_${train.trainNumber}")
                            .width(200.dp)
                            .clickable { viewModel.selectTrain(train.trainNumber) }
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = train.trainNumber,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    ),
                                    color = if (isSelected) RailTealAccent else RailTextPrimary
                                )

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = statusColor.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (train.currentDelayMinutes <= 0) "ON TIME" else "+${train.currentDelayMinutes}m",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        ),
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = train.trainName,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = RailTextSecondary,
                                maxLines = 1
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "PF ${train.currentPlatform}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = RailBlue400
                                )
                                Text(
                                    text = "ETA ${train.predictedDestinationETA}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = RailPurpleAI
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 2: WHAT WILL HAPPEN?
        // ==========================================
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = RailPurpleAI,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "WHAT WILL HAPPEN?",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    ),
                    color = RailTextPrimary
                )
            }

            // Arrival Time Change 3D
            ArrivalTimeChange3D(
                earlierEta = selectedTrain.predictedDestinationETA, // Simplified for demo
                newEta = selectedTrain.predictedDestinationETA,
                change = if (selectedTrain.currentDelayMinutes > 0) "+${selectedTrain.currentDelayMinutes} min" else "On Time"
            )

            // Redesigned Network Impact 3D
            NetworkImpact3D()
        }

        // ==========================================
        // SECTION 3: WHAT NEEDS ATTENTION?
        // ==========================================
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = RailTealAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "WHAT NEEDS ATTENTION?",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    ),
                    color = RailTextPrimary
                )
            }

            // AI Decision Support Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RailNavy800),
                border = BorderStroke(1.5.dp, RailTealAccent.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    RailTealAccent.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = RailTealAccent.copy(alpha = 0.2f),
                                modifier = Modifier.size(30.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = RailTealAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "AI DISPATCH RECOMMENDATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = RailTextPrimary
                                )
                                Text(
                                    text = "Automated Bottleneck Remediation",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                    color = RailTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = RailTealAccent.copy(alpha = 0.2f),
                            border = BorderStroke(0.5.dp, RailTealAccent)
                        ) {
                            Text(
                                text = "HIGH CONFIDENCE (94%)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = RailTealAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Actionable Recommendation Text
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = RailNavy900,
                        border = BorderStroke(1.dp, RailNavy600),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "• Precedence Priority: Prioritize 12860 Gitanjali Express on Loop Line 1 ahead of Freight #58219 at Bhatapara.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 16.sp
                                ),
                                color = RailTextPrimary
                            )
                            Text(
                                text = "• Recovery Impact: Prevents +14m cascading delay across 3 following express services.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = RailGreenOnTime
                                )
                            )
                        }
                    }

                    // MANDATORY SAFETY SAFEGUARD NOTICE
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RailNavy900.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, RailTextTertiary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = RailTextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "AI Decision Support • Human Operator Approval Required. AI never autonomously changes railway signaling.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    color = RailTextTertiary
                                )
                            )
                        }
                    }

                    // Primary Action Button
                    Button(
                        onClick = onNavigateToAiPrediction,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RailBlue500),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_review_ai_impact")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Review AI What-If Analysis & Impact",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricDetailBottomSheet(
    metric: DashboardMetric,
    train: Train,
    language: Language,
    onDismiss: () -> Unit
) {
    val strings = stringResource()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RailNavy800,
        contentColor = RailTextPrimary,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val title = when(metric) {
                DashboardMetric.SPEED -> strings.speed
                DashboardMetric.DELAY -> strings.currentDelay
                DashboardMetric.NEXT_STATION -> strings.nextStation
                DashboardMetric.ETA -> strings.predictedArrival
            }
            
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                color = RailTealAccent
            )

            Divider(color = RailNavy600.copy(alpha = 0.5f))

            when(metric) {
                DashboardMetric.SPEED -> {
                    Text(text = "${train.speedKmh} km/h", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black), color = RailTextPrimary)
                    
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val status = when {
                            train.speedKmh < train.maxSpeedKmh * 0.5 -> strings.belowNormal
                            train.speedKmh > train.maxSpeedKmh * 0.9 -> strings.aboveNormal
                            else -> strings.normal
                        }
                        DetailRow(label = strings.status, value = status)
                        DetailRow(label = strings.usual, value = "${(train.maxSpeedKmh * 0.85).toInt()} km/h")
                        val diff = (train.maxSpeedKmh * 0.85).toInt() - train.speedKmh
                        val diffText = if (diff > 0) "$diff km/h slower" else "${-diff} km/h faster"
                        DetailRow(label = strings.difference, value = diffText)
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RailNavy900,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val msg = when {
                            train.speedKmh < train.maxSpeedKmh * 0.5 -> strings.speedSlower
                            train.speedKmh > train.maxSpeedKmh * 0.9 -> strings.speedFaster
                            else -> strings.speedNormal
                        }
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                            color = RailTextSecondary
                        )
                    }
                }
                DashboardMetric.DELAY -> {
                    val currentStop = train.route.find { it.status == com.example.model.StopStatus.CURRENT }
                    Text(text = "${train.currentDelayMinutes} min ${strings.late}", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black), color = RailOrangeMod)
                    
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow(label = strings.scheduled, value = DateTimeUtil.formatPassengerTime(currentStop?.scheduledArrival ?: "20:00", language))
                        DetailRow(label = strings.actualExpected, value = DateTimeUtil.formatPassengerTime(currentStop?.predictedArrival ?: "20:24", language))
                        DetailRow(label = strings.delay, value = "+${train.currentDelayMinutes} min")
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RailNavy900,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = strings.why.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RailTealAccent)
                            Text(text = strings.whyTrainLateReason, style = MaterialTheme.typography.bodyMedium, color = RailTextSecondary)
                        }
                    }
                }
                DashboardMetric.NEXT_STATION -> {
                    val nextStop = train.route.find { it.status == com.example.model.StopStatus.UPCOMING }
                    Text(text = train.nextStationName, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black), color = RailTextPrimary)
                    
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow(label = strings.distanceLeft, value = "${nextStop?.distanceKmFromOrigin?.minus(train.coveredDistanceKm)?.coerceAtLeast(0) ?: 365} km")
                        DetailRow(label = strings.expectedArrival, value = DateTimeUtil.formatPassengerTime(nextStop?.predictedArrival ?: "22:51", language))
                        DetailRow(label = strings.expectedDeparture, value = DateTimeUtil.formatPassengerTime(nextStop?.predictedDeparture ?: "23:00", language))
                        DetailRow(label = strings.platform, value = train.nextPlatform)
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RailNavy900,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = strings.timeRemaining.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RailTealAccent)
                            val remaining = if (nextStop != null) DateTimeUtil.calculateTimeRemaining(nextStop.predictedArrival, strings) else "--"
                            Text(text = remaining, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                        }
                    }
                }
                DashboardMetric.ETA -> {
                    Text(text = DateTimeUtil.formatPassengerTime(train.predictedDestinationETA, language), style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black, fontSize = 24.sp), color = RailPurpleAI)
                    
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow(label = strings.target, value = train.destination.name)
                        DetailRow(label = strings.scheduled, value = DateTimeUtil.formatPassengerTime(train.scheduledDestinationETA, language))
                        DetailRow(label = strings.aiConfidence, value = "${train.confidenceScore}%")
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RailNavy900,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = strings.whyThisTime.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RailTealAccent)
                            val factors = listOf(
                                strings.trainSpeedFactor, 
                                strings.currentDelayFactor, 
                                strings.distanceRemainingFactor, 
                                strings.trainTrafficFactor, 
                                strings.weatherFactor
                            )
                            factors.forEach { factor ->
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(RailPurpleAI))
                                    Text(text = factor, style = MaterialTheme.typography.bodySmall, color = RailTextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RailNavy700)
            ) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = RailTextTertiary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
    }
}
