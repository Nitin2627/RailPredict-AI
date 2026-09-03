package com.example.engine

import com.example.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object PredictionEngine {

    /**
     * Dynamically calculates ETA prediction and explainability breakdown
     * using multi-factor ML-inspired analytical formulation.
     */
    fun calculateDynamicPrediction(
        train: Train,
        customCongestion: Int? = null,
        customWeatherFactor: Int? = null,
        customSpeedRestrictionMin: Int? = null,
        customPrecedingDelayMin: Int? = null
    ): PredictionBreakdown {
        val baseDelay = train.currentDelayMinutes

        // 1. Congestion Factor: Non-linear impact as track occupancy exceeds 60%
        val congestionPct = customCongestion ?: train.congestionPercent
        val congestionDelay = when {
            congestionPct > 85 -> ((congestionPct - 60) * 0.35f).roundToInt()
            congestionPct > 65 -> ((congestionPct - 50) * 0.22f).roundToInt()
            congestionPct > 40 -> ((congestionPct - 30) * 0.10f).roundToInt()
            else -> 0
        }

        // 2. Speed Restriction (TSR) Factor
        val speedRestrictionDelay = customSpeedRestrictionMin ?: if (train.speedRestrictionNotice != null) {
            when {
                train.speedRestrictionNotice.contains("30 km/h") -> 6
                train.speedRestrictionNotice.contains("45 km/h") -> 4
                train.speedRestrictionNotice.contains("60 km/h") -> 3
                else -> 2
            }
        } else {
            0
        }

        // 3. Preceding Train Delay Factor (Signaling headway constraints)
        val precedingDelay = customPrecedingDelayMin ?: if (train.precedingTrainInfo != null && !train.precedingTrainInfo.contains("Clear")) {
            3
        } else {
            0
        }

        // 4. Weather Impact Factor
        val weatherDelay = customWeatherFactor ?: when {
            train.weatherCondition.contains("Fog", ignoreCase = true) -> 8
            train.weatherCondition.contains("Rain", ignoreCase = true) -> 4
            train.weatherCondition.contains("Haze", ignoreCase = true) -> 2
            else -> 0
        }

        // 5. Station Dwell Time Variation (Platform crowding at upcoming stops)
        val remainingStopsCount = train.route.count { it.status == StopStatus.UPCOMING }
        val dwellVariation = (remainingStopsCount * 0.5f).roundToInt()

        // 6. Expected Driver Recovery Margin / Timetable Slack (Buffer)
        val remainingDist = train.totalDistanceKm - train.coveredDistanceKm
        val maxRecovery = when (train.trainType) {
            TrainType.VANDE_BHARAT -> min(8, (remainingDist / 80))
            TrainType.RAJDHANI, TrainType.SHATABDI -> min(6, (remainingDist / 120))
            TrainType.SUPERFAST, TrainType.DURONTO -> min(4, (remainingDist / 150))
            else -> min(2, (remainingDist / 200))
        }
        val expectedRecovery = -max(1, maxRecovery)

        // Net predicted additional delay
        val predictedAdditionalDelay = congestionDelay + speedRestrictionDelay + precedingDelay + weatherDelay + dwellVariation + expectedRecovery
        val finalPredictedDelay = max(0, baseDelay + predictedAdditionalDelay)

        // Dynamic Confidence Score based on variance factors
        val confidencePenalties = (congestionDelay * 1.5f) + (weatherDelay * 1.8f) + (speedRestrictionDelay * 1.2f)
        val confidenceScore = max(70, min(99, (98 - confidencePenalties).roundToInt()))

        // Delay Probability (0.0 to 1.0)
        val delayProbability = when {
            finalPredictedDelay <= 5 -> 0.05f
            finalPredictedDelay <= 15 -> 0.35f
            finalPredictedDelay <= 30 -> 0.72f
            else -> 0.95f
        }

        // Compute updated ETA string from scheduled ETA + final predicted delay
        val calculatedETA = addMinutesToTime(train.scheduledDestinationETA, finalPredictedDelay)
        val previousETA = train.previousPredictedETA
        val netDelta = finalPredictedDelay - (train.delayFactors.sumOf { it.impactMinutes } + baseDelay)

        val factorsList = mutableListOf<DelayReasonFactor>()
        if (congestionDelay > 0) {
            factorsList.add(DelayReasonFactor("Section Congestion", congestionDelay, "CONGESTION", "$congestionPct% section occupancy on upcoming route"))
        }
        if (speedRestrictionDelay > 0) {
            factorsList.add(DelayReasonFactor("Speed Restriction (TSR)", speedRestrictionDelay, "SPEED_RESTRICTION", "Active track caution advisory"))
        }
        if (precedingDelay > 0) {
            factorsList.add(DelayReasonFactor("Preceding Train Headway", precedingDelay, "PRECEDING_TRAIN", "Block signaling headway constraint"))
        }
        if (weatherDelay > 0) {
            factorsList.add(DelayReasonFactor("Weather Condition", weatherDelay, "WEATHER", train.weatherCondition))
        }
        if (dwellVariation > 0) {
            factorsList.add(DelayReasonFactor("Platform Dwell Variance", dwellVariation, "CONGESTION", "Station platform clearance at $remainingStopsCount upcoming stops"))
        }
        if (expectedRecovery < 0) {
            factorsList.add(DelayReasonFactor("Expected Recovery Buffer", expectedRecovery, "RECOVERY", "Scheduled slack time and clear track speed recovery"))
        }

        val explanationText = buildString {
            append("AI forecast predicts a net delay of ${finalPredictedDelay}m at ${train.destination.name}. ")
            if (congestionDelay > 0) append("Section density adds +${congestionDelay}m. ")
            if (speedRestrictionDelay > 0) append("Speed restrictions add +${speedRestrictionDelay}m. ")
            if (expectedRecovery < 0) append("Scheduled timetable slack recovers ${-expectedRecovery}m. ")
            append("Prediction confidence is $confidenceScore%.")
        }

        return PredictionBreakdown(
            baseDelayMinutes = baseDelay,
            sectionCongestionDelay = congestionDelay,
            speedRestrictionDelay = speedRestrictionDelay,
            precedingTrainDelay = precedingDelay,
            weatherDelay = weatherDelay,
            dwellTimeVariationDelay = dwellVariation,
            expectedRecoveryBuffer = expectedRecovery,
            predictedAdditionalDelay = predictedAdditionalDelay,
            finalPredictedDelay = finalPredictedDelay,
            confidenceScore = confidenceScore,
            delayProbability = delayProbability,
            predictedETA = calculatedETA,
            previousETA = previousETA,
            netEtaDeltaMinutes = finalPredictedDelay - baseDelay,
            explanationText = explanationText,
            factorsList = factorsList
        )
    }

    fun addMinutesToTime(timeStr: String, minutesToAdd: Int): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = sdf.parse(timeStr) ?: return timeStr
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.MINUTE, minutesToAdd)
            sdf.format(cal.time)
        } catch (e: Exception) {
            timeStr
        }
    }

    fun calculateEtaDifferenceMinutes(timeA: String, timeB: String): Int {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateA = sdf.parse(timeA) ?: return 0
            val dateB = sdf.parse(timeB) ?: return 0
            val diffMs = dateB.time - dateA.time
            (diffMs / (1000 * 60)).toInt()
        } catch (e: Exception) {
            0
        }
    }
}
