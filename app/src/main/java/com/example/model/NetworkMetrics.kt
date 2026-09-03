package com.example.model

data class CongestedSection(
    val sectionName: String,
    val zone: String,
    val capacityUtilizationPercent: Int,
    val activeTrainsCount: Int,
    val averageSpeedKmh: Int,
    val avgSectionalDelayMin: Int,
    val status: String // "Critical Bottleneck", "Heavy Load", "Normal", "Clear"
)

data class HotspotStation(
    val stationCode: String,
    val stationName: String,
    val zone: String,
    val dailyTrafficTrains: Int,
    val avgDwellOverrunMin: Float,
    val platformOccupancyRate: Int,
    val primaryDelayCause: String
)

data class NetworkOverviewMetrics(
    val activeTrains: Int,
    val onTimeTrains: Int,
    val delayedTrains: Int,
    val criticalDelays: Int,
    val avgPredictionAccuracy: Float, // e.g. 92.4%
    val networkCongestionLevel: String, // e.g. "68% (Elevated)"
    val avgEtaErrorMinutes: Float, // e.g. 4.8 min
    val traditionalEtaErrorMinutes: Float, // e.g. 14.6 min
    val maeMinutes: Float, // e.g. 4.2 min
    val rmseMinutes: Float // e.g. 6.7 min
)

data class DelayTrendPoint(
    val timeSlot: String, // "06:00", "09:00", etc.
    val avgDelayMinutes: Float,
    val predictedDelayMinutes: Float,
    val activeTrains: Int
)
