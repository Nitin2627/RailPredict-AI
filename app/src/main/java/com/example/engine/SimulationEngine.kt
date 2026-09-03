package com.example.engine

import com.example.model.*
import com.example.util.DateTimeUtil
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object SimulationEngine {

    fun updateTrainTick(train: Train): Train {
        // Dynamic speed simulation with train type specific range
        val targetSpeedMax = when (train.trainType) {
            TrainType.VANDE_BHARAT -> 130
            TrainType.RAJDHANI -> 110
            TrainType.SHATABDI -> 105
            TrainType.DURONTO -> 95
            TrainType.SUPERFAST -> 90
            TrainType.EXPRESS -> 75
        }
        val speedVariation = Random.nextInt(-4, 5)
        val newSpeed = (train.speedKmh + speedVariation).coerceIn(45, targetSpeedMax)

        // Increment covered distance (2-4 km per tick for active live demonstration)
        var newCovered = train.coveredDistanceKm + Random.nextInt(2, 5)
        if (newCovered >= train.totalDistanceKm) {
            // Loop back for continuous live demo showcase
            newCovered = (train.totalDistanceKm * 0.15f).toInt()
        }

        // Determine current route segment and GPS position
        var currentStopIdx = 0
        for (i in 0 until train.route.size - 1) {
            if (newCovered >= train.route[i].distanceKmFromOrigin) {
                currentStopIdx = i
            }
        }
        val nextStopIdx = (currentStopIdx + 1).coerceAtMost(train.route.size - 1)
        val currentStop = train.route[currentStopIdx]
        val nextStop = train.route[nextStopIdx]

        val segmentDist = (nextStop.distanceKmFromOrigin - currentStop.distanceKmFromOrigin).coerceAtLeast(1)
        val distInSegment = (newCovered - currentStop.distanceKmFromOrigin).coerceIn(0, segmentDist)
        val segmentFraction = distInSegment.toFloat() / segmentDist.toFloat()

        // Interpolated real-time GPS coordinates
        val newLat = currentStop.station.latitude + (nextStop.station.latitude - currentStop.station.latitude) * segmentFraction
        val newLng = currentStop.station.longitude + (nextStop.station.longitude - currentStop.station.longitude) * segmentFraction

        val updatedRoute = train.route.mapIndexed { idx, stop ->
            when {
                idx < currentStopIdx -> stop.copy(status = StopStatus.PASSED)
                idx == currentStopIdx -> stop.copy(status = StopStatus.CURRENT)
                else -> stop.copy(status = StopStatus.UPCOMING)
            }
        }

        // Recalculate dynamic prediction
        val prediction = PredictionEngine.calculateDynamicPrediction(train)

        val updatedStatus = when {
            prediction.finalPredictedDelay <= 5 -> TrainStatus.ON_TIME
            prediction.finalPredictedDelay <= 20 -> TrainStatus.MINOR_DELAY
            prediction.finalPredictedDelay <= 45 -> TrainStatus.MODERATE_DELAY
            else -> TrainStatus.SEVERE_DELAY
        }

        return train.copy(
            speedKmh = newSpeed,
            coveredDistanceKm = newCovered,
            currentStationName = currentStop.station.name,
            nextStationName = nextStop.station.name,
            currentPlatform = currentStop.platformNumber,
            nextPlatform = nextStop.platformNumber,
            destinationPlatform = train.route.lastOrNull()?.platformNumber ?: train.destinationPlatform,
            latitude = newLat,
            longitude = newLng,
            currentDelayMinutes = train.currentDelayMinutes,
            predictedDestinationETA = prediction.predictedETA,
            confidenceScore = prediction.confidenceScore,
            status = updatedStatus,
            route = updatedRoute,
            delayFactors = prediction.factorsList
        )
    }

    fun applyDelayEvent(
        train: Train,
        eventType: String
    ): Pair<Train, RailAlert> {
        val prevEta = train.predictedDestinationETA
        val updatedTrain: Train
        val alert: RailAlert

        when (eventType) {
            "HEAVY_CONGESTION" -> {
                val newCongestion = 94
                val newDelay = train.currentDelayMinutes + 12
                val prediction = PredictionEngine.calculateDynamicPrediction(
                    train.copy(currentDelayMinutes = newDelay, congestionPercent = newCongestion),
                    customCongestion = newCongestion
                )
                updatedTrain = train.copy(
                    congestionPercent = newCongestion,
                    currentDelayMinutes = newDelay,
                    previousPredictedETA = prevEta,
                    predictedDestinationETA = prediction.predictedETA,
                    confidenceScore = prediction.confidenceScore,
                    status = TrainStatus.SEVERE_DELAY,
                    delayFactors = prediction.factorsList,
                    precedingTrainInfo = "High block density: 4 rakes queued in section"
                )
                alert = RailAlert(
                    id = "EVT-${System.currentTimeMillis() % 10000}",
                    trainNumber = train.trainNumber,
                    title = "🔴 Too Many Trains Ahead Alert",
                    message = "Too many trains ahead (94% track usage) detected near ${train.currentStationName}. Arrival time changed from ${DateTimeUtil.formatPassengerTime(prevEta)} to ${DateTimeUtil.formatPassengerTime(prediction.predictedETA)} (+12 min).",
                    timestamp = java.text.SimpleDateFormat("dd MMM yyyy • hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date()),
                    severity = AlertSeverity.CRITICAL,
                    category = AlertCategory.CONGESTION,
                    affectedSection = "${train.currentStationName} – ${train.nextStationName}",
                    actionSuggested = "AI suggests dynamic re-routing via loop 3"
                )
            }

            "SIGNAL_FAILURE" -> {
                val newDelay = train.currentDelayMinutes + 18
                val prediction = PredictionEngine.calculateDynamicPrediction(
                    train.copy(currentDelayMinutes = newDelay, speedKmh = 15)
                )
                updatedTrain = train.copy(
                    speedKmh = 15,
                    currentDelayMinutes = newDelay,
                    previousPredictedETA = prevEta,
                    predictedDestinationETA = prediction.predictedETA,
                    confidenceScore = 78,
                    status = TrainStatus.SEVERE_DELAY,
                    delayFactors = prediction.factorsList,
                    speedRestrictionNotice = "Automatic signaling failure: Paper line clear token active"
                )
                alert = RailAlert(
                    id = "EVT-${System.currentTimeMillis() % 10000}",
                    trainNumber = train.trainNumber,
                    title = "⚠️ Signal Failure Detected",
                    message = "Track circuit failure at block 22 ahead of ${train.currentStationName}. Train crawling at 15 km/h. Arrival time updated to ${DateTimeUtil.formatPassengerTime(prediction.predictedETA)} (+18 min).",
                    timestamp = java.text.SimpleDateFormat("dd MMM yyyy • hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date()),
                    severity = AlertSeverity.CRITICAL,
                    category = AlertCategory.OPERATIONAL,
                    affectedSection = "${train.currentStationName} Block Section 22",
                    actionSuggested = "Dispatch S&T repair crew to relay room"
                )
            }

            "SPEED_RESTRICTION" -> {
                val newDelay = train.currentDelayMinutes + 8
                val prediction = PredictionEngine.calculateDynamicPrediction(
                    train.copy(currentDelayMinutes = newDelay, speedKmh = 35),
                    customSpeedRestrictionMin = 8
                )
                updatedTrain = train.copy(
                    speedKmh = 35,
                    currentDelayMinutes = newDelay,
                    previousPredictedETA = prevEta,
                    predictedDestinationETA = prediction.predictedETA,
                    confidenceScore = 91,
                    status = TrainStatus.MODERATE_DELAY,
                    speedRestrictionNotice = "Emergency TSR 30 km/h on Bridge 118 (Track settlement)",
                    delayFactors = prediction.factorsList
                )
                alert = RailAlert(
                    id = "EVT-${System.currentTimeMillis() % 10000}",
                    trainNumber = train.trainNumber,
                    title = "⚠️ Speed Restriction Imposed",
                    message = "TSR 30 km/h active ahead. AI arrival time increased from ${DateTimeUtil.formatPassengerTime(prevEta)} to ${DateTimeUtil.formatPassengerTime(prediction.predictedETA)} (+8 min).",
                    timestamp = java.text.SimpleDateFormat("dd MMM yyyy • hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date()),
                    severity = AlertSeverity.WARNING,
                    category = AlertCategory.DELAY,
                    affectedSection = "Bridge 118 Kilometre 742",
                    actionSuggested = "Impose caution order on section loco pilot"
                )
            }

            "WEATHER_DISRUPTION" -> {
                val newDelay = train.currentDelayMinutes + 10
                val newWeather = "Dense Fog (Visibility < 100m)"
                val prediction = PredictionEngine.calculateDynamicPrediction(
                    train.copy(currentDelayMinutes = newDelay, weatherCondition = newWeather, speedKmh = 45),
                    customWeatherFactor = 10
                )
                updatedTrain = train.copy(
                    weatherCondition = newWeather,
                    speedKmh = 45,
                    currentDelayMinutes = newDelay,
                    previousPredictedETA = prevEta,
                    predictedDestinationETA = prediction.predictedETA,
                    confidenceScore = 84,
                    status = TrainStatus.MODERATE_DELAY,
                    delayFactors = prediction.factorsList
                )
                alert = RailAlert(
                    id = "EVT-${System.currentTimeMillis() % 10000}",
                    trainNumber = train.trainNumber,
                    title = "🌫️ Fog Advisory & Arrival Update",
                    message = "Dense fog warning. Train speed restricted for safety. Arrival time updated to ${DateTimeUtil.formatPassengerTime(prediction.predictedETA)} (+10 min).",
                    timestamp = java.text.SimpleDateFormat("dd MMM yyyy • hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date()),
                    severity = AlertSeverity.WARNING,
                    category = AlertCategory.DELAY,
                    affectedSection = "Northern Grid Plains",
                    actionSuggested = "Activate FOG-PASS GPS device in locomotive cab"
                )
            }

            "GREEN_WAVE_RECOVERY" -> {
                val newDelay = max(0, train.currentDelayMinutes - 7)
                val newCongestion = 25
                val prediction = PredictionEngine.calculateDynamicPrediction(
                    train.copy(currentDelayMinutes = newDelay, congestionPercent = newCongestion, speedKmh = 120),
                    customCongestion = newCongestion,
                    customSpeedRestrictionMin = 0
                )
                updatedTrain = train.copy(
                    congestionPercent = newCongestion,
                    currentDelayMinutes = newDelay,
                    speedKmh = 120,
                    speedRestrictionNotice = null,
                    precedingTrainInfo = "Clear automatic signal aspect",
                    previousPredictedETA = prevEta,
                    predictedDestinationETA = prediction.predictedETA,
                    confidenceScore = 97,
                    status = if (newDelay <= 5) TrainStatus.ON_TIME else TrainStatus.RECOVERING,
                    delayFactors = prediction.factorsList
                )
                alert = RailAlert(
                    id = "EVT-${System.currentTimeMillis() % 10000}",
                    trainNumber = train.trainNumber,
                    title = "🟢 Green Wave Recovery Active",
                    message = "Section cleared ahead. Train ${train.trainNumber} accelerating to 120 km/h. Arrival time improved to ${DateTimeUtil.formatPassengerTime(prediction.predictedETA)} (-7 min recovery).",
                    timestamp = java.text.SimpleDateFormat("dd MMM yyyy • hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date()),
                    severity = AlertSeverity.RECOVERY,
                    category = AlertCategory.OPERATIONAL,
                    affectedSection = "${train.currentStationName} Fast Corridor",
                    actionSuggested = "Hold freight rakes to preserve green aspect"
                )
            }

            else -> {
                // Default unscheduled stoppage
                val newDelay = train.currentDelayMinutes + 14
                val prediction = PredictionEngine.calculateDynamicPrediction(
                    train.copy(currentDelayMinutes = newDelay, speedKmh = 0)
                )
                updatedTrain = train.copy(
                    speedKmh = 0,
                    currentDelayMinutes = newDelay,
                    previousPredictedETA = prevEta,
                    predictedDestinationETA = prediction.predictedETA,
                    confidenceScore = 80,
                    status = TrainStatus.SEVERE_DELAY,
                    delayFactors = prediction.factorsList
                )
                alert = RailAlert(
                    id = "EVT-${System.currentTimeMillis() % 10000}",
                    trainNumber = train.trainNumber,
                    title = "🛑 Unscheduled Stoppage",
                    message = "Train ${train.trainNumber} halted at outer signal. Arrival time changed to ${DateTimeUtil.formatPassengerTime(prediction.predictedETA)} (+14 min).",
                    timestamp = java.text.SimpleDateFormat("dd MMM yyyy • hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date()),
                    severity = AlertSeverity.CRITICAL,
                    category = AlertCategory.DELAY,
                    affectedSection = "Outer Signal Junction",
                    actionSuggested = "Contact Section Controller for line clearance"
                )
            }
        }

        return Pair(updatedTrain, alert)
    }
}
