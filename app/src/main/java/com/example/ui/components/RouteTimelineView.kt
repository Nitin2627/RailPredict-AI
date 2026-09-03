package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RouteStop
import com.example.model.StopStatus
import com.example.ui.theme.*
import com.example.util.DateTimeUtil
import com.example.util.Language
import com.example.util.LanguageManager
import com.example.util.stringResource
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun RouteTimelineView(
    route: List<RouteStop>,
    modifier: Modifier = Modifier,
    testTag: String = "route_timeline_view"
) {
    val strings = stringResource()
    val context = LocalContext.current
    val languageManager = remember { LanguageManager(context) }
    val currentLanguage by languageManager.languageFlow.collectAsState(initial = Language.ENGLISH)

    Card(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RailNavy800),
        border = BorderStroke(1.dp, RailNavy600)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = strings.route,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                color = RailTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                route.forEachIndexed { index, stop ->
                    RouteStopItem(
                        stop = stop,
                        language = currentLanguage,
                        isLast = index == route.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteStopItem(
    stop: RouteStop,
    language: Language,
    isLast: Boolean
) {
    val strings = stringResource()
    val statusColor = when {
        stop.delayMinutes <= 0 -> RailGreenOnTime
        stop.delayMinutes <= 20 -> RailYellowMinor
        else -> RailOrangeMod
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            val nodeColor = when (stop.status) {
                StopStatus.PASSED -> RailGreenOnTime
                StopStatus.CURRENT -> RailTealAccent
                StopStatus.UPCOMING -> RailNavy600
            }
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(nodeColor))
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).height(48.dp).background(RailNavy600.copy(alpha = 0.5f)))
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Row(modifier = Modifier.weight(1f).padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(
                    text = stop.station.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (stop.status == StopStatus.CURRENT) RailTealAccent else RailTextPrimary
                )
                val delayText = if (stop.delayMinutes <= 0) strings.onTime else "+${stop.delayMinutes}m ${strings.late}"
                Text(text = delayText, style = MaterialTheme.typography.labelSmall, color = statusColor)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val time = stop.actualArrival ?: stop.predictedArrival
                Text(
                    text = DateTimeUtil.formatPassengerTime(time, language),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                    color = RailTextPrimary
                )
                Text(
                    text = "PF ${stop.platformNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = RailBlue400
                )
            }
        }
    }
}
