package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
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
fun LandingScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToPassenger: () -> Unit,
    onNavigateToDemo: () -> Unit,
    viewModel: RailPredictViewModel,
    modifier: Modifier = Modifier
) {
    val train by viewModel.selectedTrain.collectAsState()
    val scrollState = rememberScrollState()
    val strings = stringResource()
    
    val context = LocalContext.current
    val languageManager = remember { LanguageManager(context) }
    val currentLanguage by languageManager.languageFlow.collectAsState(initial = Language.ENGLISH)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RailNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. SELECT YOUR TRAIN (Priority)
        TrainSelectionSection(viewModel)

        // 2. LIVE TRAIN STATUS (Compact Card)
        LiveTrainStatusCard(train, currentLanguage)

        // 3. NEXT STATION & PREDICTED ARRIVAL (Main Focus)
        PrimaryEtaSection(train, currentLanguage)

        // 4. AI UPDATE (Simple Insight)
        SimpleAiInsightCard(train)

        // 5. ROUTE (Simple Timeline)
        SimpleRouteTimeline(train, currentLanguage)

        // Quick Actions (Moved to bottom)
        QuickActionsSection(
            onNavigateToDashboard = onNavigateToDashboard,
            onNavigateToTracking = onNavigateToTracking,
            onNavigateToDemo = onNavigateToDemo
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainSelectionSection(viewModel: RailPredictViewModel) {
    val strings = stringResource()
    val searchNumber by viewModel.searchTrainNumber.collectAsState()
    val fromStation by viewModel.searchFromStation.collectAsState()
    val toStation by viewModel.searchToStation.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchError by viewModel.searchError.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    if (searchResults.isNotEmpty()) {
        SearchResultsDialog(
            trains = searchResults,
            onDismiss = { viewModel.clearSearchResults() },
            onSelect = { train ->
                viewModel.selectTrain(train.trainNumber)
                viewModel.clearSearchResults()
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = strings.selectYourTrain,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = RailTealAccent
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search by Number
            OutlinedTextField(
                value = searchNumber,
                onValueChange = { 
                    viewModel.updateSearchTrainNumber(it)
                    if (searchError != null) viewModel.clearSearchError()
                },
                label = { Text(strings.enterTrainNumber) },
                placeholder = { Text("e.g. 12860") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !isSearching,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RailTealAccent,
                    unfocusedBorderColor = RailNavy600,
                    focusedLabelColor = RailTealAccent,
                    cursorColor = RailTealAccent
                ),
                trailingIcon = {
                    if (isSearching && searchNumber.isNotEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = RailTealAccent)
                    } else {
                        IconButton(onClick = { viewModel.findTrainByNumber() }) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = RailTealAccent)
                        }
                    }
                },
                isError = searchError == "NOT_FOUND" || searchError == "INVALID_NUMBER"
            )

            if (searchError == "NOT_FOUND") {
                Text(
                    text = strings.trainNotFound,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            } else if (searchError == "INVALID_NUMBER") {
                Text(
                    text = strings.invalidNumber,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.findTrainByNumber() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSearching && searchNumber.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RailTealAccent, contentColor = RailNavy900)
            ) {
                if (isSearching && searchNumber.isNotEmpty()) {
                    Text(strings.searching, fontWeight = FontWeight.Bold)
                } else {
                    Text(strings.findTrain, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Divider(modifier = Modifier.weight(1f), color = RailNavy600.copy(alpha = 0.5f))
                Text(
                    text = " ${strings.or} ",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = RailTextTertiary
                )
                Divider(modifier = Modifier.weight(1f), color = RailNavy600.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search by Route
            Text(
                text = strings.searchByRoute,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = RailTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fromStation,
                    onValueChange = { 
                        viewModel.updateSearchFromStation(it)
                        if (searchError != null) viewModel.clearSearchError()
                    },
                    label = { Text(strings.from) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isSearching,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RailBlue400,
                        unfocusedBorderColor = RailNavy600
                    ),
                    isError = searchError == "ROUTE_NOT_FOUND"
                )
                OutlinedTextField(
                    value = toStation,
                    onValueChange = { 
                        viewModel.updateSearchToStation(it)
                        if (searchError != null) viewModel.clearSearchError()
                    },
                    label = { Text(strings.to) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isSearching,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RailBlue400,
                        unfocusedBorderColor = RailNavy600
                    ),
                    isError = searchError == "ROUTE_NOT_FOUND"
                )
            }

            if (searchError == "ROUTE_NOT_FOUND") {
                Text(
                    text = strings.noTrainRoute,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.findTrainByRoute() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSearching,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RailBlue500)
            ) {
                if (isSearching) {
                    Text(strings.searching, fontWeight = FontWeight.Bold)
                } else {
                    Text(strings.findTrain, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SearchResultsDialog(
    trains: List<Train>,
    onDismiss: () -> Unit,
    onSelect: (Train) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Train",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = RailTealAccent
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    trains.forEach { train ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().clickable { onSelect(train) },
                            shape = RoundedCornerShape(8.dp),
                            color = RailNavy700,
                            border = BorderStroke(0.5.dp, RailNavy600)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "🚆 ${train.trainNumber}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                                Text(text = "${train.origin.name} → ${train.destination.name}", style = MaterialTheme.typography.bodySmall, color = RailBlue400)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel", color = RailTextSecondary)
                }
            }
        }
    }
}

@Composable
fun LiveTrainStatusCard(train: Train, language: Language) {
    val strings = stringResource()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🚆 ${train.trainNumber}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = RailTextPrimary
                    )
                    Text(
                        text = "${train.origin.name} → ${train.destination.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = RailBlue400
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "01 Sep 2026",
                        style = MaterialTheme.typography.labelSmall,
                        color = RailTextSecondary
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = RailTealAccent.copy(alpha = 0.1f),
                        border = BorderStroke(0.5.dp, RailTealAccent.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = train.status.label,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            color = RailTealAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = RailNavy600.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                InfoColumn(label = strings.currentStation, value = train.currentStationName, modifier = Modifier.weight(1.5f))
                InfoColumn(
                    label = strings.currentDelay,
                    value = "${train.currentDelayMinutes} min ${strings.late}",
                    valueColor = if (train.currentDelayMinutes > 0) RailOrangeMod else RailGreenOnTime,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                InfoColumn(label = strings.nextStation, value = train.nextStationName, modifier = Modifier.weight(1.5f))
                InfoColumn(label = strings.speed, value = "${train.speedKmh} km/h", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = RailTextPrimary) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = valueColor)
    }
}

@Composable
fun PrimaryEtaSection(train: Train, language: Language) {
    val strings = stringResource()
    val nextStop = train.route.find { it.status == StopStatus.CURRENT || it.status == StopStatus.UPCOMING }
    val eta24h = nextStop?.predictedArrival ?: train.predictedDestinationETA
    val formattedEta = DateTimeUtil.formatPassengerTime(eta24h, language)
    val nextStationName = nextStop?.station?.name ?: train.nextStationName

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, RailTealAccent.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = nextStationName.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                color = RailBlue400
            )
            
            Text(
                text = strings.nextStation,
                style = MaterialTheme.typography.labelSmall,
                color = RailTextTertiary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = strings.expectedArrival,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                color = RailTextSecondary
            )

            AnimatedContent(
                targetState = formattedEta,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "eta_anim"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black, 
                        fontSize = 28.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = RailTealAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val delayText = if (train.currentDelayMinutes > 0) "${train.currentDelayMinutes} min ${strings.late}" else strings.onTime
                    Text(
                        text = delayText, 
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), 
                        color = if (train.currentDelayMinutes > 0) RailOrangeMod else RailGreenOnTime
                    )
                    Text(text = strings.delay, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                }
                
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(RailNavy600))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${train.confidenceScore}%", 
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), 
                        color = RailPurpleAI
                    )
                    Text(text = strings.aiConfidence, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                }
            }
        }
    }
}

@Composable
fun SimpleAiInsightCard(train: Train) {
    val strings = stringResource()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailPurpleAI.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = RailPurpleAI, modifier = Modifier.size(20.dp))
                Text(text = strings.aiUpdate, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = RailPurpleAI)
            }

            Spacer(modifier = Modifier.height(12.dp))

            val insightText = when {
                train.congestionPercent > 70 -> "⚠️ ${strings.simpleWords.busyTrack}"
                train.speedKmh < 50 -> "🚆 ${strings.trainMovingSlower}"
                else -> "🟢 ${strings.smoothFlowPredicted}"
            }

            Text(text = insightText, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = strings.why, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
            
            Column(modifier = Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (train.congestionPercent > 50) {
                    BulletPoint(strings.simpleWords.moreTrains)
                }
                if (train.speedKmh < 60) {
                    BulletPoint(strings.trainMovingSlower)
                }
                // Mock rain factor for demo
                if (train.trainNumber == "12860") {
                    BulletPoint(strings.simpleWords.rain)
                }
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(RailTextSecondary))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = RailTextSecondary)
    }
}

@Composable
fun SimpleRouteTimeline(train: Train, language: Language) {
    val strings = stringResource()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = strings.route, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RailTextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            
            val displayStops = train.route.filter { it.status != StopStatus.PASSED }.take(5)
            displayStops.forEachIndexed { index, stop ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.dp)) {
                        val color = if (stop.delayMinutes <= 5) RailGreenOnTime else if (stop.delayMinutes <= 20) RailYellowMinor else RailOrangeMod
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                        if (index < displayStops.size - 1) {
                            Box(modifier = Modifier.width(2.dp).height(40.dp).background(RailNavy600))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(modifier = Modifier.weight(1f).padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = stop.station.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                            val delayStatus = if (stop.delayMinutes <= 0) strings.onTime else "+${stop.delayMinutes}m ${strings.late}"
                            Text(text = delayStatus, style = MaterialTheme.typography.labelSmall, color = RailTextSecondary)
                        }
                        Text(
                            text = DateTimeUtil.formatPassengerTime(stop.predictedArrival, language),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = RailTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onNavigateToDashboard: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToDemo: () -> Unit
) {
    val strings = stringResource()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickActionButton(label = strings.liveTrains, icon = Icons.Default.LocationOn, color = RailTealAccent, onClick = onNavigateToTracking, modifier = Modifier.weight(1f))
        QuickActionButton(label = strings.dashboard, icon = Icons.Default.Dashboard, color = RailBlue400, onClick = onNavigateToDashboard, modifier = Modifier.weight(1f))
        QuickActionButton(label = "AI Demo", icon = Icons.Default.PlayCircle, color = RailPurpleAI, onClick = onNavigateToDemo, modifier = Modifier.weight(1f))
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(64.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = RailNavy800,
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary, textAlign = TextAlign.Center)
        }
    }
}
