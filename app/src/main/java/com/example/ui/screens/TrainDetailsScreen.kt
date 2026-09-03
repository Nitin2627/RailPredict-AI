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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.PredictionEngine
import com.example.model.TrainStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.DateTimeUtil
import com.example.util.Language
import com.example.util.LanguageManager
import com.example.util.stringResource
import com.example.viewmodel.RailPredictViewModel

@Composable
fun TrainDetailsScreen(
    viewModel: RailPredictViewModel,
    trainNumber: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val trains by viewModel.trains.collectAsState()
    val train = trains.find { it.trainNumber == trainNumber } ?: trains.first()
    val scrollState = rememberScrollState()

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
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = RailTextPrimary)
            }
            Text(
                text = "${train.trainNumber} - ${train.trainName}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = RailTextPrimary
            )
        }

        // Compact Train Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${train.origin.name} → ${train.destination.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = RailBlue400
                )
                
                Divider(color = RailNavy600.copy(alpha = 0.3f))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = strings.currentStation, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                        Text(text = train.currentStationName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        val statusColor = if (train.currentDelayMinutes > 0) RailOrangeMod else RailGreenOnTime
                        Text(text = strings.currentDelay, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                        Text(
                            text = "${train.currentDelayMinutes} min ${strings.late}", 
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), 
                            color = statusColor
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = strings.nextStation, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                        Text(text = train.nextStationName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = strings.speed, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                        Text(text = "${train.speedKmh} km/h", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                    }
                }
            }
        }

        // Prominent Predicted Arrival
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.5.dp, RailTealAccent.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = strings.predictedArrival.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = RailTextSecondary
                )
                Text(
                    text = DateTimeUtil.formatPassengerTime(train.predictedDestinationETA, currentLanguage),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, fontSize = 28.sp),
                    color = RailTealAccent,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "${strings.aiConfidence}: ${train.confidenceScore}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = RailPurpleAI
                )
            }
        }

        // Full Route
        WhyEtaChangedCard(train = train, language = currentLanguage)

        RouteTimelineView(route = train.route)

        Spacer(modifier = Modifier.height(16.dp))
    }
}
