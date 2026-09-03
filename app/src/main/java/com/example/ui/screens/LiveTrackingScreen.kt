package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Train
import com.example.model.TrainStatus
import com.example.ui.theme.*
import com.example.util.DateTimeUtil
import com.example.util.Language
import com.example.util.LanguageManager
import com.example.util.stringResource
import com.example.viewmodel.RailPredictViewModel

@Composable
fun LiveTrackingScreen(
    viewModel: RailPredictViewModel,
    onNavigateToTrainDetails: (String) -> Unit,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val filteredTrains by viewModel.filteredTrains.collectAsState()
    
    val context = LocalContext.current
    val languageManager = remember { LanguageManager(context) }
    val currentLanguage by languageManager.languageFlow.collectAsState(initial = Language.ENGLISH)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RailNavy900)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Text(
            text = strings.liveTrains.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
            color = RailTealAccent
        )

        // Train List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredTrains, key = { it.trainNumber }) { train ->
                SimpleTrainCard(
                    train = train,
                    language = currentLanguage,
                    onClick = { onNavigateToTrainDetails(train.trainNumber) }
                )
            }
        }
    }
}

@Composable
private fun SimpleTrainCard(
    train: Train,
    language: Language,
    onClick: () -> Unit
) {
    val strings = stringResource()
    val statusColor = if (train.currentDelayMinutes > 0) RailOrangeMod else RailGreenOnTime

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = train.trainNumber,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = RailTextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.1f),
                    border = BorderStroke(0.5.dp, statusColor)
                ) {
                    Text(
                        text = if (train.currentDelayMinutes > 0) "${train.currentDelayMinutes} min ${strings.late}" else strings.onTime,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Text(
                text = "${train.origin.name} → ${train.destination.name}",
                style = MaterialTheme.typography.bodySmall,
                color = RailBlue400
            )

            Divider(color = RailNavy600.copy(alpha = 0.3f))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = strings.currentStation, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                    Text(text = train.currentStationName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = strings.nextStation, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                    Text(text = train.nextStationName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
                }
            }

            Column {
                Text(text = strings.predictedArrival, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
                Text(
                    text = DateTimeUtil.formatPassengerTime(train.predictedDestinationETA, language),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                    color = RailTealAccent
                )
            }
        }
    }
}
