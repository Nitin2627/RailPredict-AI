package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.Train
import com.example.ui.theme.*
import com.example.util.DateTimeUtil
import com.example.util.Language
import com.example.util.LanguageManager
import com.example.util.stringResource
import com.example.viewmodel.RailPredictViewModel
import androidx.compose.ui.platform.LocalContext

data class ChatMessage(
    val id: String,
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: String,
    val isActionable: Boolean = false,
    val category: String = "INFO"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantSheet(
    viewModel: RailPredictViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTrain by viewModel.selectedTrain.collectAsState()
    val trains by viewModel.trains.collectAsState()
    val congestedSections by viewModel.congestedSections.collectAsState()
    
    val context = LocalContext.current
    val languageManager = remember { LanguageManager(context) }
    val currentLanguage by languageManager.languageFlow.collectAsState(initial = Language.ENGLISH)

    var inputText by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "1",
                    sender = "AI",
                    text = "Namaste! I am the RailPredict AI Railway Intelligence Assistant. How can I assist you with train ETA predictions, delay causes, connection risks, or network bottleneck insights today?",
                    timestamp = "Just now",
                    category = "WELCOME"
                )
            )
        )
    }

    val quickQueries = listOf(
        "Why is Train ${selectedTrain.trainNumber} late?",
        "When will ${selectedTrain.trainNumber} reach ${selectedTrain.destination.name}?",
        "Check connecting train risk",
        "Which railway sections are at highest risk?",
        "Explain delay ripple effects",
        "What is the prediction confidence?"
    )

    fun handleQuery(query: String) {
        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "USER",
            text = query,
            timestamp = "Now"
        )
        
        val aiResponseText = when {
            query.contains("Why", ignoreCase = true) || query.contains("late", ignoreCase = true) || query.contains("delay", ignoreCase = true) -> {
                val factor = selectedTrain.delayFactors.firstOrNull()
                if (selectedTrain.currentDelayMinutes <= 0) {
                    "Train ${selectedTrain.trainNumber} (${selectedTrain.trainName}) is currently running ON TIME at ${selectedTrain.speedKmh} km/h with clear block signaling."
                } else {
                    "Train ${selectedTrain.trainNumber} is currently running +${selectedTrain.currentDelayMinutes} min behind schedule. Top contributing factors:\n" +
                    "1. ${factor?.label ?: "Track congestion"}: +${factor?.impactMinutes ?: 6} min (${factor?.description ?: "Block occupancy"})\n" +
                    "2. Track capacity utilization: ${selectedTrain.congestionPercent}%\n" +
                    "3. Weather impact: ${selectedTrain.weatherCondition}\n" +
                    "AI Forecast: Expected arrival at ${selectedTrain.destination.name} by ${DateTimeUtil.formatPassengerTime(selectedTrain.predictedDestinationETA, currentLanguage)}."
                }
            }
            query.contains("reach", ignoreCase = true) || query.contains("When", ignoreCase = true) || query.contains("ETA", ignoreCase = true) -> {
                "Predicted arrival for Train ${selectedTrain.trainNumber} at ${selectedTrain.destination.name} is ${DateTimeUtil.formatPassengerTime(selectedTrain.predictedDestinationETA, currentLanguage)} (Likely Range: ${DateTimeUtil.formatPassengerTime(selectedTrain.scheduledDestinationETA, currentLanguage)} – ${DateTimeUtil.formatPassengerTime(selectedTrain.predictedDestinationETA, currentLanguage)}, Confidence: ${selectedTrain.confidenceScore}%)."
            }
            query.contains("connecting", ignoreCase = true) || query.contains("connection", ignoreCase = true) -> {
                "For Train ${selectedTrain.trainNumber} arriving at ${selectedTrain.destination.name} at ${DateTimeUtil.formatPassengerTime(selectedTrain.predictedDestinationETA, currentLanguage)}:\n" +
                "• Recommended minimum connection buffer: 25 minutes.\n" +
                "• Current Delay: +${selectedTrain.currentDelayMinutes} min.\n" +
                "• Connection Risk Assessment: ${if (selectedTrain.currentDelayMinutes > 20) "HIGH RISK - Buffer may be compromised" else "LOW TO MODERATE RISK - Maintain minimum 20 min transfer window"}.\n" +
                "(Note: Predictions are decision-support estimates and not guaranteed.)"
            }
            query.contains("sections", ignoreCase = true) || query.contains("highest risk", ignoreCase = true) || query.contains("bottleneck", ignoreCase = true) -> {
                val worstSection = congestedSections.maxByOrNull { it.capacityUtilizationPercent }
                "Highest risk track section currently is ${worstSection?.sectionName ?: "Bhopal - Itarsi"} at ${worstSection?.capacityUtilizationPercent ?: 88}% capacity utilization with ${worstSection?.activeTrainsCount ?: 9} active rakes and +${worstSection?.avgSectionalDelayMin ?: 14} min average delay impact."
            }
            query.contains("ripple", ignoreCase = true) || query.contains("domino", ignoreCase = true) -> {
                "Delay Ripple Analysis: Primary delay on Train ${selectedTrain.trainNumber} (+${selectedTrain.currentDelayMinutes}m) is projected to affect up to 4 following rakes on the section with an estimated +5 to +18 minutes cascading impact. Dispatchers should review platform holding priorities."
            }
            query.contains("confidence", ignoreCase = true) || query.contains("reliability", ignoreCase = true) -> {
                "Current Prediction Reliability is ${selectedTrain.confidenceScore}% (High). Telemetry inputs: Live GPS Feed (Active), Track Sensors (Active), Weather radar (${selectedTrain.weatherCondition}), Section Historical Performance (Covered)."
            }
            else -> {
                "Based on real-time telemetry for Train ${selectedTrain.trainNumber} (${selectedTrain.trainName}): Running at ${selectedTrain.speedKmh} km/h near ${selectedTrain.currentStationName}, Delay: +${selectedTrain.currentDelayMinutes} min, Predicted Destination ETA: ${DateTimeUtil.formatPassengerTime(selectedTrain.predictedDestinationETA, currentLanguage)} with ${selectedTrain.confidenceScore}% confidence."
            }
        }

        val aiMsg = ChatMessage(
            id = (System.currentTimeMillis() + 1).toString(),
            sender = "AI",
            text = aiResponseText,
            timestamp = "Just now",
            category = "RESPONSE"
        )

        messages = messages + userMsg + aiMsg
        inputText = ""
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RailNavy800,
        contentColor = RailTextPrimary,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier.testTag("ai_assistant_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 400.dp, max = 650.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = RailPurpleAI.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, RailPurpleAI),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = RailPurpleAI,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "RAILWAY INTELLIGENCE ASSISTANT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = RailTextPrimary
                        )
                        Text(
                            text = "AI Decision Support & Passenger Foresight Engine",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = RailTealAccent
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = RailTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Divider(color = RailNavy600, modifier = Modifier.padding(vertical = 8.dp))

            // Quick Prompt Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(quickQueries) { q ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = RailNavy900,
                        border = BorderStroke(1.5.dp, RailNavy600),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 36.dp)
                            .clickable { handleQuery(q) }
                    ) {
                        Text(
                            text = q,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                            color = RailBlue400,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Message History
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    val isAi = msg.sender == "AI"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isAi) 2.dp else 12.dp,
                                bottomEnd = if (isAi) 12.dp else 2.dp
                            ),
                            color = if (isAi) RailNavy900 else RailBlue500,
                            border = BorderStroke(1.dp, if (isAi) RailPurpleAI.copy(alpha = 0.4f) else RailBlue400),
                            modifier = Modifier.widthIn(max = 320.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (isAi) "RailPredict AI" else "You",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = if (isAi) RailTealAccent else Color.White
                                    )
                                    Text(
                                        text = "• ${msg.timestamp}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = if (isAi) RailTextTertiary else Color.White.copy(alpha = 0.7f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    ),
                                    color = if (isAi) RailTextPrimary else Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask anything about train delays or operations...", fontSize = 12.sp, color = RailTextTertiary) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_assistant_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = RailNavy900,
                        unfocusedContainerColor = RailNavy900,
                        focusedBorderColor = RailTealAccent,
                        unfocusedBorderColor = RailNavy600,
                        focusedTextColor = RailTextPrimary,
                        unfocusedTextColor = RailTextPrimary
                    ),
                    maxLines = 2
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            handleQuery(inputText)
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(RailTealAccent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = RailNavy900,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
