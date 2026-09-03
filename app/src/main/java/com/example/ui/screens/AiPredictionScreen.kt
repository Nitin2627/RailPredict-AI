package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.PredictionEngine
import com.example.model.Train
import com.example.ui.components.AccuracyComparisonCard
import com.example.ui.components.WhyEtaChangedCard
import com.example.ui.theme.*
import com.example.util.Language
import com.example.util.LanguageManager
import com.example.util.stringResource
import com.example.viewmodel.RailPredictViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun AiPredictionScreen(
    viewModel: RailPredictViewModel,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val selectedTrain by viewModel.selectedTrain.collectAsState()
    val scrollState = rememberScrollState()
    var showTechnicalDetails by remember { mutableStateOf(false) }
    
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
        // Section 1: Simple Explanation
        Text(
            text = strings.simpleExplanation.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
            color = RailTealAccent
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = strings.whyEtaChanged,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = RailTextPrimary
                )

                AiExplanationItem(icon = Icons.Default.Speed, text = strings.trainMovingSlower)
                AiExplanationItem(icon = Icons.Default.Traffic, text = strings.simpleWords.moreTrains)
                AiExplanationItem(icon = Icons.Default.Cloud, text = strings.simpleWords.rain)
                AiExplanationItem(icon = Icons.Default.Engineering, text = strings.slowSectionAhead)
            }
        }

        // Section 2: Technical Details (Collapsible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTechnicalDetails = !showTechnicalDetails }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = strings.technicalDetails.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                color = RailTextTertiary
            )
            Icon(
                imageVector = if (showTechnicalDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = RailTextTertiary
            )
        }

        AnimatedVisibility(visible = showTechnicalDetails) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Why ETA Changed Card (Technical)
                WhyEtaChangedCard(train = selectedTrain, language = currentLanguage)

                // Model Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RailNavy800),
                    border = BorderStroke(1.dp, RailNavy600)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TechnicalRow(label = strings.model, value = "XGBoost + LSTM Ensemble")
                        TechnicalRow(label = strings.prediction, value = "Remaining Travel Time (RTT)")
                        TechnicalRow(label = strings.features, value = "Speed, Delay, Weather, Traffic, Historical")
                    }
                }

                AccuracyComparisonCard()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AiExplanationItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = CircleShape,
            color = RailTealAccent.copy(alpha = 0.1f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = RailTealAccent, modifier = Modifier.size(16.dp))
            }
        }
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = RailTextPrimary)
    }
}

@Composable
fun TechnicalRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = RailTextTertiary)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = RailTextPrimary)
    }
}
