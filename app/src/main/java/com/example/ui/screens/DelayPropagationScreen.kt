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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DelayRippleChainView
import com.example.ui.components.NetworkImpact3D
import com.example.ui.theme.*
import com.example.viewmodel.RailPredictViewModel

@Composable
fun DelayPropagationScreen(
    viewModel: RailPredictViewModel,
    modifier: Modifier = Modifier
) {
    val chain by viewModel.delayPropagationNodes.collectAsState()
    val scrollState = rememberScrollState()
    var showMitigationDetails by remember { mutableStateOf(false) }

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
                Text(
                    text = "NETWORK OPERATIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    ),
                    color = RailOrangeMod
                )
                Text(
                    text = "How the Delay May Change",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    ),
                    color = RailTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RailOrangeMod.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, RailOrangeMod)
            ) {
                Text(
                    text = "ACTIVE RIPPLE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = RailOrangeMod,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // LEVEL 1: High-Level Plain English Explanation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = RailOrangeMod.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = RailOrangeMod,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Why Do Following Trains Get Delayed?",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = RailTextPrimary
                    )
                    Text(
                        text = "A primary disruption on one train cascades through shared signals, block sections, and platform berths to affect following services.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        ),
                        color = RailTextSecondary
                    )
                }
            }
        }

        // LEVEL 2: Signature Delay Ripple Visual Story (Train A -> Delay -> Junction -> Train B -> Train C)
        DelayRippleChainView()

        // LEVEL 2: Signature Network Impact Radar (Effect of other trains)
        NetworkImpact3D()

        // LEVEL 3: Smart Progressive Disclosure - Operator Mitigation Strategy
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = RailNavy800),
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = RailTealAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Recommended Ripple Mitigation",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = RailTextPrimary
                        )
                    }

                    TextButton(
                        onClick = { showMitigationDetails = !showMitigationDetails },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (showMitigationDetails) "Hide" else "View Details",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = RailBlue400
                        )
                    }
                }

                Text(
                    text = "Reroute trailing freight rakes to Raipur Goods Yard Loop Line 3 to clear main corridor for Express #12860.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = RailTealAccent
                )

                AnimatedVisibility(
                    visible = showMitigationDetails,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Divider(color = RailNavy600)

                        Text(
                            text = "ACTION CHECKLIST (OPERATOR APPROVAL REQUIRED):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = RailTextTertiary
                        )

                        Text(
                            text = "1. Transmit line clearance order to DDU Section Controller.\n2. Hold freight train #58219 at Bhatapara outer starter signal.\n3. Grant green aspect signal to Gitanjali Express on Main Line 1.\n4. Estimated recovery: 14 minutes reclaimed across 3 trains.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            color = RailTextSecondary
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RailNavy900,
                            border = BorderStroke(0.5.dp, RailTextTertiary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Safety Safeguard: RailPredict AI is a decision support tool. Manual verification by Section Controller is required prior to signal actuation.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    color = RailTextTertiary
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
