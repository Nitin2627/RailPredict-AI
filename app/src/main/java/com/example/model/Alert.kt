package com.example.model

enum class AlertSeverity {
    CRITICAL,
    WARNING,
    INFO,
    RECOVERY
}

enum class AlertCategory(val displayName: String) {
    ALL("All"),
    CRITICAL("Critical"),
    DELAY("Delay"),
    CONGESTION("Too Many Trains Ahead"),
    OPERATIONAL("Operational")
}

data class RailAlert(
    val id: String,
    val trainNumber: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val severity: AlertSeverity,
    val category: AlertCategory,
    val affectedSection: String? = null,
    val actionSuggested: String? = null
)
