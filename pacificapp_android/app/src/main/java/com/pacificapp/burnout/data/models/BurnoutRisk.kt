package com.pacificapp.burnout.data.models

import java.time.LocalDate
import java.time.LocalDateTime

data class BurnoutRisk(
    val id: Long = 0,
    val userId: Long = 0,
    val date: LocalDate = LocalDate.now(),
    val riskLevel: Int = 0,
    val factors: Map<String, String> = emptyMap(),
    val recommendations: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val riskCategory: BurnoutRiskCategory
        get() = when {
            riskLevel <= 25 -> BurnoutRiskCategory.LOW
            riskLevel <= 50 -> BurnoutRiskCategory.MODERATE
            riskLevel <= 75 -> BurnoutRiskCategory.HIGH
            else -> BurnoutRiskCategory.CRITICAL
        }

    val overtimeFactor: String
        get() = factors["overtime_factor"] ?: "unknown"

    val stressFactor: String
        get() = factors["stress_factor"] ?: "unknown"

    val sleepQualityFactor: String
        get() = factors["sleep_quality_factor"] ?: "unknown"

    val sleepDeprivationFactor: String
        get() = factors["sleep_deprivation_factor"] ?: "unknown"

    val workdayDurationFactor: String
        get() = factors["workday_duration_factor"] ?: "unknown"
}

enum class BurnoutRiskCategory {
    LOW, MODERATE, HIGH, CRITICAL
}

data class FactorData(
    val overtimeFactor: String = "",
    val workdayDurationFactor: String = "",
    val stressFactor: String = "",
    val sleepQualityFactor: String = "",
    val sleepDeprivationFactor: String = "",
    val factorsData: Map<String, String> = emptyMap(),
    val recommendations: List<String> = emptyList()
)

data class WeeklyData(
    val dailyData: List<DailyRiskData> = emptyList()
)

data class DailyRiskData(
    val date: String,
    val riskLevel: Int
)
