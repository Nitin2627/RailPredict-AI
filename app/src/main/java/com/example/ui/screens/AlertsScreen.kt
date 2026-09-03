package com.example.ui.screens

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
import com.example.model.AlertCategory
import com.example.model.AlertSeverity
import com.example.model.RailAlert
import com.example.ui.components.SimulateEventDialog
import com.example.ui.theme.*
import com.example.viewmodel.RailPredictViewModel

@Composable
fun AlertsScreen(
    viewModel: RailPredictViewModel,
    modifier: Modifier = Modifier
) {
    val alerts by viewModel.alerts.collectAsState()
    val selectedCategory by viewModel.alertCategoryFilter.collectAsState()
    var showSimDialog by remember { mutableStateOf(false) }

    val filteredAlerts = remember(alerts, selectedCategory) {
        if (selectedCategory == AlertCategory.ALL) {
            alerts
        } else {
            alerts.filter { it.category == selectedCategory }
        }
    }

    if (showSimDialog) {
        SimulateEventDialog(
            trainNumber = "12345",
            onDismiss = { showSimDialog = false },
            onSelectEvent = { eventType ->
                viewModel.triggerSimulationEvent(eventType)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RailNavy900)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = RailOrangeMod,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "OPERATIONAL & DELAY ALERT SYSTEM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = RailTextSecondary
                    )
                }
                Text(
                    text = "${filteredAlerts.size} Active System Notifications",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = RailTextPrimary
                )
            }

            Button(
                onClick = { showSimDialog = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RailOrangeMod),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .testTag("btn_trigger_alert_sim")
                    .defaultMinSize(minHeight = 44.dp)
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simulate", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
            }
        }

        // Category Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(AlertCategory.values()) { cat ->
                val isSel = selectedCategory == cat
                FilterChip(
                    selected = isSel,
                    onClick = { viewModel.setAlertCategoryFilter(cat) },
                    label = {
                        Text(
                            text = cat.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = RailNavy800,
                        selectedContainerColor = RailNavy700,
                        labelColor = RailTextSecondary,
                        selectedLabelColor = RailTealAccent
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = RailNavy600,
                        selectedBorderColor = RailTealAccent,
                        enabled = true,
                        selected = isSel
                    )
                )
            }
        }

        // Alerts List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredAlerts, key = { it.id }) { alert ->
                AlertItemCard(
                    alert = alert,
                    onDismiss = { viewModel.dismissAlert(alert.id) }
                )
            }
        }
    }
}

@Composable
private fun AlertItemCard(
    alert: RailAlert,
    onDismiss: () -> Unit
) {
    val (severityColor, severityLabel) = when (alert.severity) {
        AlertSeverity.CRITICAL -> RailRedSevere to "CRITICAL"
        AlertSeverity.WARNING -> RailOrangeMod to "WARNING"
        AlertSeverity.RECOVERY -> RailGreenOnTime to "RECOVERY"
        AlertSeverity.INFO -> RailBlue400 to "INFO"
    }

    Card(
        modifier = Modifier
            .testTag("alert_card_${alert.id}")
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, severityColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        shape = RoundedCornerShape(4.dp),
                        color = severityColor.copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, severityColor)
                    ) {
                        Text(
                            text = severityLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = severityColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = RailTextPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = alert.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = RailTextTertiary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_dismiss_alert_${alert.id}")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = RailTextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                ),
                color = RailTextSecondary
            )

            if (alert.affectedSection != null) {
                Text(
                    text = "Affected Section: ${alert.affectedSection}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = RailTealAccent
                )
            }

            if (alert.actionSuggested != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RailNavy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = RailTealAccent, modifier = Modifier.size(14.dp))
                        Text(
                            text = "Suggested Action: ${alert.actionSuggested}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = RailTextPrimary
                        )
                    }
                }
            }
        }
    }
}
