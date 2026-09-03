package com.example.model

data class PredictionBreakdown(
    val baseDelayMinutes: Int,
    val sectionCongestionDelay: Int,
    val speedRestrictionDelay: Int,
    val precedingTrainDelay: Int,
    val weatherDelay: Int,
    val dwellTimeVariationDelay: Int,
    val expectedRecoveryBuffer: Int, // negative number e.g. -4
    val predictedAdditionalDelay: Int,
    val finalPredictedDelay: Int,
    val confidenceScore: Int,
    val delayProbability: Float, // 0.0 to 1.0
    val predictedETA: String,
    val previousETA: String,
    val netEtaDeltaMinutes: Int,
    val explanationText: String,
    val factorsList: List<DelayReasonFactor>
)

data class DelayPropagationNode(
    val trainNumber: String,
    val trainName: String,
    val originalDelayMinutes: Int,
    val propagatedDelayMinutes: Int,
    val bottleneckStation: String,
    val sectionName: String,
    val riskLevel: String, // "HIGH", "MEDIUM", "LOW"
    val cause: String,
    val recommendedAction: String
)

data class AccuracyMetric(
    val label: String,
    val value: String,
    val benchmark: String,
    val improvement: String,
    val description: String
)
