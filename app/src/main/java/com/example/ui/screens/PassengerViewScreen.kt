package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RouteStop
import com.example.model.StopStatus
import com.example.model.Train
import com.example.model.TrainStatus
import com.example.ui.theme.*
import com.example.util.DateTimeUtil
import com.example.util.Language
import com.example.util.LanguageManager
import com.example.util.stringResource
import com.example.viewmodel.RailPredictViewModel

@Composable
fun PassengerViewScreen(
    viewModel: RailPredictViewModel,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val selectedTrain by viewModel.selectedTrain.collectAsState()
    
    val context = LocalContext.current
    val languageManager = remember { LanguageManager(context) }
    val currentLanguage by languageManager.languageFlow.collectAsState(initial = Language.ENGLISH)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RailNavy900)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // 1. TRAIN DETAILS
        item { TrainInfoCard(selectedTrain) }

        // 2. CURRENT TRAIN STATUS
        item { CurrentStatusSection(selectedTrain) }

        // 3. CURRENT STATION DETAILS
        item {
            val currentStop = selectedTrain.route.find { it.status == StopStatus.CURRENT }
            if (currentStop != null) {
                CurrentStationSection(currentStop, currentLanguage)
            }
        }

        // 4. NEXT STATION DETAILS
        item {
            val nextStop = selectedTrain.route.find { it.status == StopStatus.UPCOMING }
            if (nextStop != null) {
                NextStationSection(nextStop, currentLanguage)
            }
        }

        // 5. JOURNEY PROGRESS
        item { JourneyProgressSection(selectedTrain, currentLanguage) }

        // 6. UPCOMING STATIONS
        item { UpcomingStationsSection(selectedTrain, currentLanguage) }

        // 7. DESTINATION ETA
        item { DestinationEtaSection(selectedTrain, currentLanguage) }

        // 8. AI REASON
        item { AiReasonSection(selectedTrain) }
    }
}

@Composable
fun TrainInfoCard(train: Train) {
    val strings = stringResource()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.DirectionsTransit, contentDescription = null, tint = RailTealAccent, modifier = Modifier.size(24.dp))
                    Column {
                        Text(
                            text = "🚆 ${train.trainNumber}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = RailTextPrimary
                        )
                        Text(
                            text = train.trainName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = RailBlue400
                        )
                    }
                }
            }

            Text(
                text = "${train.origin.name} → ${train.destination.name}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = RailTextPrimary
            )

            Divider(color = RailNavy600.copy(alpha = 0.3f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoItem(label = strings.trainType, value = train.trainType.displayName, modifier = Modifier.weight(1f))
                InfoItem(
                    label = strings.status, 
                    value = if (train.currentDelayMinutes > 0) "${strings.currentDelay} ${train.currentDelayMinutes} ${strings.min}" else strings.onTime,
                    valueColor = if (train.currentDelayMinutes > 0) RailOrangeMod else RailGreenOnTime,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CurrentStatusSection(train: Train) {
    val strings = stringResource()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(strings.status.uppercase())
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatusGridItem(
                        icon = Icons.Default.LocationOn,
                        label = strings.currentStation,
                        value = train.currentStationName,
                        modifier = Modifier.weight(1f)
                    )
                    StatusGridItem(
                        icon = Icons.Default.Speed,
                        label = strings.speed,
                        value = "${train.speedKmh} km/h",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatusGridItem(
                        icon = Icons.Default.Schedule,
                        label = strings.currentDelay,
                        value = "${train.currentDelayMinutes} ${strings.min}",
                        valueColor = if (train.currentDelayMinutes > 0) RailOrangeMod else RailGreenOnTime,
                        modifier = Modifier.weight(1f)
                    )
                    StatusGridItem(
                        icon = Icons.Default.AltRoute,
                        label = strings.distanceCovered,
                        value = "${train.coveredDistanceKm} km",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatusGridItem(
                        icon = Icons.Default.Straighten,
                        label = strings.distanceLeft,
                        value = "${train.totalDistanceKm - train.coveredDistanceKm} km",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CurrentStationSection(stop: RouteStop, language: Language) {
    val strings = stringResource()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(strings.current.uppercase() + " ${strings.status.uppercase()}")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stop.station.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = RailTealAccent
                )
                
                Divider(color = RailNavy600.copy(alpha = 0.3f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = strings.arrival, value = DateTimeUtil.formatPassengerTime(stop.scheduledArrival, language), modifier = Modifier.weight(1f))
                    if (stop.actualArrival != null) {
                        InfoItem(label = strings.actualArrival, value = DateTimeUtil.formatPassengerTime(stop.actualArrival, language), modifier = Modifier.weight(1f))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = strings.departure, value = DateTimeUtil.formatPassengerTime(stop.scheduledDeparture, language), modifier = Modifier.weight(1f))
                    InfoItem(label = strings.platform, value = stop.platformNumber, modifier = Modifier.weight(1f))
                }
                InfoItem(
                    label = strings.delay, 
                    value = "+${stop.delayMinutes} ${strings.min}", 
                    valueColor = if (stop.delayMinutes > 0) RailOrangeMod else RailGreenOnTime
                )
            }
        }
    }
}

@Composable
fun NextStationSection(stop: RouteStop, language: Language) {
    val strings = stringResource()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(strings.next.uppercase() + " ${strings.status.uppercase()}")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800.copy(alpha = 0.5f)),
            border = BorderStroke(1.5.dp, RailTealAccent.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stop.station.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = RailTealAccent
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = "Distance", value = "${stop.distanceKmFromOrigin} km", modifier = Modifier.weight(1f))
                    InfoItem(label = strings.platform, value = stop.platformNumber, modifier = Modifier.weight(1f))
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = strings.expectedArrival, value = DateTimeUtil.formatPassengerTime(stop.predictedArrival, language), modifier = Modifier.weight(1f))
                    InfoItem(label = strings.expectedDeparture, value = DateTimeUtil.formatPassengerTime(stop.predictedDeparture, language), modifier = Modifier.weight(1f))
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RailTealAccent.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, RailTealAccent.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = RailTealAccent, modifier = Modifier.size(20.dp))
                        Column {
                            Text(text = strings.timeRemaining, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                            Text(
                                text = DateTimeUtil.calculateTimeRemaining(stop.predictedArrival, strings),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = RailTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JourneyProgressSection(train: Train, language: Language) {
    val strings = stringResource()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(strings.journeyProgress.uppercase())
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "${(train.progressPercent * 100).toInt()}%", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = RailTealAccent)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "${train.coveredDistanceKm} km ${strings.departed.lowercase()}", style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                        Text(text = "${train.totalDistanceKm - train.coveredDistanceKm} km ${strings.distanceLeft.lowercase()}", style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Railway Timeline
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.CenterStart) {
                    // Track Line
                    Box(modifier = Modifier.padding(start = 12.dp).fillMaxHeight().width(2.dp).background(RailNavy600))
                    
                    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                        // Origin
                        TimelineNode(label = train.origin.name, subLabel = strings.departed, isCurrent = false, isCompleted = true)
                        
                        // Current
                        TimelineNode(label = train.currentStationName, subLabel = strings.current, isCurrent = true, isCompleted = true)
                        
                        // Next
                        TimelineNode(label = train.nextStationName, subLabel = strings.next, isCurrent = false, isCompleted = false)
                        
                        // Destination
                        TimelineNode(label = train.destination.name, subLabel = strings.destination, isCurrent = false, isCompleted = false)
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineNode(label: String, subLabel: String, isCurrent: Boolean, isCompleted: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(if (isCurrent) 24.dp else 12.dp)
                .clip(CircleShape)
                .background(if (isCurrent) RailTealAccent else if (isCompleted) RailGreenOnTime else RailNavy600)
                .border(if (isCurrent) BorderStroke(4.dp, Color.White) else BorderStroke(0.dp, Color.Transparent), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isCurrent) {
                Icon(Icons.Default.DirectionsTransit, contentDescription = null, tint = RailNavy900, modifier = Modifier.size(14.dp))
            }
        }
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = if (isCurrent) RailTealAccent else RailTextPrimary)
            Text(text = subLabel, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RailTextTertiary)
        }
    }
}

@Composable
fun UpcomingStationsSection(train: Train, language: Language) {
    val strings = stringResource()
    val upcomingStops = train.route.filter { it.status == StopStatus.UPCOMING }.take(4)
    
    if (upcomingStops.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle(strings.upcomingStations.uppercase())
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RailNavy800),
                border = BorderStroke(1.dp, RailNavy600)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    upcomingStops.forEach { stop ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = stop.station.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                                Text(text = "${strings.reachingAround}: ${DateTimeUtil.formatPassengerTime(stop.predictedArrival, language)}", style = MaterialTheme.typography.labelSmall, color = RailTextSecondary)
                            }
                            Text(
                                text = "+${stop.delayMinutes} min", 
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), 
                                color = if (stop.delayMinutes > 0) RailOrangeMod else RailGreenOnTime
                            )
                        }
                        if (stop != upcomingStops.last()) {
                            Divider(color = RailNavy600.copy(alpha = 0.2f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationEtaSection(train: Train, language: Language) {
    val strings = stringResource()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(strings.destination.uppercase())
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.5.dp, RailPurpleAI.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = train.destination.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = RailTextSecondary
                )
                
                Text(
                    text = strings.predictedArrival,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = RailTextTertiary
                )
                
                Text(
                    text = DateTimeUtil.formatPassengerTime(train.predictedDestinationETA, language),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, fontSize = 28.sp),
                    color = RailTealAccent,
                    textAlign = TextAlign.Center
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${train.currentDelayMinutes} min", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = RailOrangeMod)
                        Text(text = strings.currentDelay, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary, textAlign = TextAlign.Center)
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(RailNavy600))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Expected final delay range (mocked for demo)
                        val minDelay = (train.currentDelayMinutes - 4).coerceAtLeast(0)
                        val maxDelay = train.currentDelayMinutes + 2
                        Text(text = "$minDelay–$maxDelay min", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = RailOrangeMod)
                        Text(text = strings.expectedDelay, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary, textAlign = TextAlign.Center)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = RailPurpleAI.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, RailPurpleAI.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${strings.aiConfidence}: ${train.confidenceScore}%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = RailPurpleAI
                    )
                }
            }
        }
    }
}

@Composable
fun AiReasonSection(train: Train) {
    val strings = stringResource()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(strings.whyTrainLate)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailPurpleAI.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = RailOrangeMod, modifier = Modifier.size(20.dp))
                    Text(text = strings.trackAheadBusy, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                }
                
                AiReasonItem(text = strings.trainSpeedLower)
                AiReasonItem(text = strings.aiExpectsRecovery)
                
                val factor = train.delayFactors.firstOrNull()
                if (factor != null) {
                    AiReasonItem(text = "${strings.networkEffect}: ${factor.description}")
                }
            }
        }
    }
}

@Composable
fun AiReasonItem(text: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.padding(top = 6.dp).size(4.dp).clip(CircleShape).background(RailPurpleAI))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = RailTextSecondary)
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
        color = RailBlue400,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun InfoItem(label: String, value: String, valueColor: Color = RailTextPrimary, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = valueColor)
    }
}

@Composable
fun StatusGridItem(icon: ImageVector, label: String, value: String, valueColor: Color = RailTextPrimary, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            shape = CircleShape,
            color = RailNavy900,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = RailTealAccent, modifier = Modifier.size(16.dp))
            }
        }
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RailTextTertiary)
            Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = valueColor)
        }
    }
}
