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
import com.example.ui.components.WhyEtaChangedCard
import com.example.ui.theme.*
import com.example.util.Language
import com.example.util.LanguageManager
import com.example.viewmodel.RailPredictViewModel

@Composable
fun DemoModeScreen(
    viewModel: RailPredictViewModel,
    modifier: Modifier = Modifier
) {
    val demoStep by viewModel.demoStep.collectAsState()
    val isDemoActive by viewModel.isDemoModeActive.collectAsState()
    val selectedTrain by viewModel.selectedTrain.collectAsState()
    val scrollState = rememberScrollState()
    
    val context = LocalContext.current
    val languageManager = remember { LanguageManager(context) }
    val currentLanguage by languageManager.languageFlow.collectAsState(initial = Language.ENGLISH)

    LaunchedEffect(Unit) {
        if (!isDemoActive) {
            viewModel.setDemoModeActive(true)
        }
    }

    val stepTitles = listOf(
        "Normal Scheduled Baseline",
        "Approaching Dense Corridor",
        "Bottleneck & Speed Caution Detected",
        "AI Dynamic Multi-Factor Recalculation",
        "ETA Extends (+13 min) with Explainability",
        "Bottleneck Cleared & Speed Recovery",
        "ETA Stabilizes & Recovers Automatically"
    )

    val stepDescriptions = listOf(
        "Train #12345 (Vande Bharat) is cruising at 105 km/h on clear track with a mild 10-minute origin departure delay. AI model predicts destination arrival at 22:25.",
        "Train enters Bhopal-Itarsi section. Track capacity utilization surges to 75%. Freight rake G-889 enters the block 4 ahead.",
        "Track circuit load hits 88%. Emergency TSR 45 km/h is imposed on Bridge 118. Train speed drops to 58 km/h.",
        "RailPredict AI ingests real-time speed, TSR caution, and preceding freight headway to update the analytical ETA vector.",
        "Destination ETA dynamically adjusts from 22:38 to 22:51 (+13 min). Explainability module breaks down +7m congestion, +4m TSR, +3m headway, and -1m recovery buffer.",
        "Train passes Hoshangabad, clears freight passing loop, and enters high-speed 130 km/h corridor. Continuous green aspect granted.",
        "Loco pilot accelerates to 128 km/h. AI model dynamically reduces predicted delay by 8 minutes! ETA adjusts forward to 22:34 with 96% confidence."
    )

    val stepTitle = stepTitles.getOrElse(demoStep - 1) { "Step $demoStep" }
    val stepDesc = stepDescriptions.getOrElse(demoStep - 1) { "" }

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
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = RailPurpleAI,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "HACKATHON EVALUATION DEMO MODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = RailPurpleAI
                    )
                }
                Text(
                    text = "Guided 7-Step Dynamic ETA Walkthrough",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = RailTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RailPurpleAI.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, RailPurpleAI)
            ) {
                Text(
                    text = "Step $demoStep of 7",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = RailTealAccent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Step Progress Indicator Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..7) {
                val isCompleted = i <= demoStep
                val isCurrent = i == demoStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp) // Generous touch target for tapping step bars
                        .clip(RoundedCornerShape(6.dp))
                        .background(RailNavy800)
                        .clickable { viewModel.setDemoStep(i) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isCurrent) RailTealAccent
                                else if (isCompleted) RailPurpleAI
                                else RailNavy600
                            )
                    )
                }
            }
        }

        // Demo Step Card with Explanation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.5.dp, RailPurpleAI.copy(alpha = 0.6f))
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
                    Text(
                        text = "STEP $demoStep: ${stepTitle.uppercase()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        ),
                        color = RailTealAccent
                    )
                }

                Text(
                    text = stepDesc,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    ),
                    color = RailTextPrimary
                )

                // Judge / Evaluator Note Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RailNavy900,
                    border = BorderStroke(1.dp, RailBlue400.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = RailBlue400,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = when (demoStep) {
                                1 -> "Demonstrates baseline ML inference under nominal track conditions."
                                2 -> "Shows proactive section density detection before physical train stoppage occurs."
                                3 -> "Simulates multi-source data ingestion: signaling circuits + TSR cautionary orders."
                                4 -> "Highlights real-time non-linear dynamic formula recalculation."
                                5 -> "Core Hackathon Requirement: Transparent explainability ('Why Did ETA Change?')."
                                6 -> "Proves two-way agility: ETA dynamically shortens when green wave recovery is achieved."
                                7 -> "Final arrival forecast stabilizes automatically with high confidence score."
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = RailTextSecondary
                        )
                    }
                }
            }
        }

        // Dynamic Why ETA Changed Component
        WhyEtaChangedCard(
            train = selectedTrain,
            language = currentLanguage,
            modifier = Modifier.testTag("demo_why_eta_card")
        )

        // Step Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.prevDemoStep() },
                enabled = demoStep > 1,
                modifier = Modifier
                    .testTag("btn_demo_prev")
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, if (demoStep > 1) RailBlue400 else RailNavy600),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Previous",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
                )
            }

            OutlinedButton(
                onClick = { viewModel.setDemoStep(1) },
                modifier = Modifier
                    .testTag("btn_demo_reset")
                    .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, RailNavy600),
                contentPadding = PaddingValues(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
            }

            Button(
                onClick = { viewModel.nextDemoStep() },
                enabled = demoStep < 7,
                modifier = Modifier
                    .testTag("btn_demo_next")
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RailPurpleAI),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    if (demoStep < 7) "Next Step" else "Complete",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
