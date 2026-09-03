package com.example.model

enum class TrainStatus(val label: String) {
    ON_TIME("On Time"),
    MINOR_DELAY("Minor Delay"),
    MODERATE_DELAY("Moderate Delay"),
    SEVERE_DELAY("Severe Delay"),
    RECOVERING("Recovering Delay")
}

enum class TrainType(val code: String, val displayName: String) {
    RAJDHANI("RAJ", "Rajdhani Express"),
    VANDE_BHARAT("VB", "Vande Bharat Express"),
    SHATABDI("SHT", "Shatabdi Express"),
    DURONTO("DUR", "Duronto Express"),
    SUPERFAST("SF", "Superfast Express"),
    EXPRESS("EXP", "Mail / Express")
}

data class Station(
    val code: String,
    val name: String,
    val state: String,
    val latitude: Double,
    val longitude: Double,
    val platformCount: Int = 8,
    val junctionType: String = "Major Junction"
)

enum class StopStatus {
    PASSED,
    CURRENT,
    UPCOMING
}

data class RouteStop(
    val station: Station,
    val stopSequence: Int,
    val scheduledArrival: String, // HH:mm
    val scheduledDeparture: String,
    val predictedArrival: String, // Dynamic ETA predicted by AI
    val predictedDeparture: String,
    val actualArrival: String? = null,
    val distanceKmFromOrigin: Int,
    val delayMinutes: Int = 0,
    val dwellTimeMinutes: Int = 2,
    val status: StopStatus = StopStatus.UPCOMING,
    val confidenceScore: Int = 92,
    val platformNumber: String = "1",
    val platformStatus: String = "Confirmed" // "Confirmed", "Assigned", "Changed", "Expected"
)

data class DelayReasonFactor(
    val label: String,
    val impactMinutes: Int, // e.g. +7 or -3
    val category: String, // "CONGESTION", "SPEED_RESTRICTION", "PRECEDING_TRAIN", "WEATHER", "RECOVERY"
    val description: String
)

data class Train(
    val trainNumber: String,
    val trainName: String,
    val trainType: TrainType,
    val zone: String, // "NR", "CR", "WR", "SR", "NCR", "WCR", etc.
    val origin: Station,
    val destination: Station,
    val currentStationName: String,
    val nextStationName: String,
    val currentPlatform: String = "1",
    val nextPlatform: String = "2",
    val destinationPlatform: String = "1",
    val platformChangeNotice: String? = null,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Int,
    val maxSpeedKmh: Int = 130,
    val currentDelayMinutes: Int,
    val scheduledDestinationETA: String,
    val predictedDestinationETA: String,
    val previousPredictedETA: String, // For "Why did ETA change" comparison
    val confidenceScore: Int, // 0-100%
    val congestionPercent: Int, // Section congestion level 0-100%
    val weatherCondition: String, // "Clear", "Fog (Visibility < 200m)", "Heavy Rain", "Thunderstorm"
    val status: TrainStatus,
    val route: List<RouteStop>,
    val delayFactors: List<DelayReasonFactor>,
    val totalDistanceKm: Int,
    val coveredDistanceKm: Int,
    val precedingTrainInfo: String? = null,
    val speedRestrictionNotice: String? = null
) {
    val progressPercent: Float
        get() = if (totalDistanceKm > 0) (coveredDistanceKm.toFloat() / totalDistanceKm.toFloat()).coerceIn(0f, 1f) else 0f
}
