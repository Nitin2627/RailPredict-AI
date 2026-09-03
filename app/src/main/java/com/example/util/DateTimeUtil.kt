package com.example.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    private const val DEFAULT_DATE = "01 Sep 2026"

    fun formatPassengerTime(time24h: String, language: Language = Language.ENGLISH): String {
        return try {
            val parts = time24h.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val calendar = Calendar.getInstance()
            calendar.set(2026, Calendar.SEPTEMBER, 1, hour, minute)
            val date = calendar.time

            if (language == Language.HINDI) {
                val timeFormat = SimpleDateFormat("hh:mm", Locale("hi", "IN"))
                val timeStr = timeFormat.format(date)
                
                val hindiAmPm = when(calendar.get(Calendar.AM_PM)) {
                    Calendar.AM -> if (hour < 12) "सुबह" else "रात"
                    Calendar.PM -> if (hour < 16) "दोपहर" else if (hour < 19) "शाम" else "रात"
                    else -> ""
                }
                
                "01 सितम्बर 2026 • $hindiAmPm $timeStr बजे"
            } else {
                val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.ENGLISH)
                sdf.format(date).replace("AM", "AM").replace("PM", "PM")
            }
        } catch (e: Exception) {
            "$DEFAULT_DATE • $time24h"
        }
    }

    fun calculateTimeRemaining(targetTime24h: String, strings: AppStrings): String {
        return try {
            val targetParts = targetTime24h.split(":")
            val targetHour = targetParts[0].toInt()
            val targetMinute = targetParts[1].toInt()

            val now = Calendar.getInstance()
            val currentHour = now.get(Calendar.HOUR_OF_DAY)
            val currentMinute = now.get(Calendar.MINUTE)

            var diffMinutes = (targetHour * 60 + targetMinute) - (currentHour * 60 + currentMinute)
            if (diffMinutes < 0) diffMinutes += 24 * 60 // Assume next day if target is earlier

            val hours = diffMinutes / 60
            val minutes = diffMinutes % 60

            val hourStr = if (hours > 0) "$hours ${strings.hr} " else ""
            val minStr = "$minutes ${strings.min}"
            
            "${strings.approximately} $hourStr$minStr"
        } catch (e: Exception) {
            "--"
        }
    }
}
