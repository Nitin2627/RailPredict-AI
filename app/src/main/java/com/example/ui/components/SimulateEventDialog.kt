package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

data class DelayEventOption(
    val id: String,
    val title: String,
    val description: String,
    val impact: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun SimulateEventDialog(
    trainNumber: String,
    onDismiss: () -> Unit,
    onSelectEvent: (String) -> Unit,
    testTag: String = "simulate_event_dialog"
) {
    val events = listOf(
        DelayEventOption(
            id = "HEAVY_CONGESTION",
            title = "Too Many Trains Ahead (94%)",
            description = "Track usage exceeds 90% near bottleneck junction",
            impact = "+12 min ETA",
            icon = Icons.Default.Traffic,
            color = RailRedSevere
        ),
        DelayEventOption(
            id = "SIGNAL_FAILURE",
            title = "Automatic Signal Failure",
            description = "Track circuit fault; train restricted to 15 km/h caution speed",
            impact = "+18 min ETA",
            icon = Icons.Default.Warning,
            color = RailRedSevere
        ),
        DelayEventOption(
            id = "SPEED_RESTRICTION",
            title = "Temporary Speed Restriction (TSR)",
            description = "30 km/h emergency limit on bridge maintenance section",
            impact = "+8 min ETA",
            icon = Icons.Default.Speed,
            color = RailOrangeMod
        ),
        DelayEventOption(
            id = "WEATHER_DISRUPTION",
            title = "Dense Fog Disruption",
            description = "Northern plains visibility drops below 100 metres",
            impact = "+10 min ETA",
            icon = Icons.Default.Cloud,
            color = RailBlue400
        ),
        DelayEventOption(
            id = "UNSCHEDULED_STOPPAGE",
            title = "Unscheduled Outer Signal Halt",
            description = "Train held at home signal awaiting platform clearance",
            impact = "+14 min ETA",
            icon = Icons.Default.PanTool,
            color = RailOrangeMod
        ),
        DelayEventOption(
            id = "GREEN_WAVE_RECOVERY",
            title = "Green Wave Priority Dispatch",
            description = "Bottleneck cleared; high-speed 120 km/h sprint enabled",
            impact = "-7 min Recovery",
            icon = Icons.Default.TrendingDown,
            color = RailGreenOnTime
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .testTag(testTag)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = RailNavy800,
            border = BorderStroke(1.dp, RailNavy600)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SIMULATE DELAY EVENT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = RailTextPrimary
                        )
                        Text(
                            text = "Target Train: #$trainNumber",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = RailTealAccent
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = RailTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Trigger a realistic operational event to observe how the AI dynamic prediction engine updates arrival forecast in real time.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = RailTextTertiary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    events.forEach { event ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onSelectEvent(event.id)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = RailNavy900,
                            border = BorderStroke(1.dp, event.color.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = event.color.copy(alpha = 0.15f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = event.icon,
                                                contentDescription = null,
                                                tint = event.color,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = event.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            ),
                                            color = RailTextPrimary
                                        )
                                        Text(
                                            text = event.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp
                                            ),
                                            color = RailTextSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = event.color.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, event.color)
                                ) {
                                    Text(
                                        text = event.impact,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = event.color,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
