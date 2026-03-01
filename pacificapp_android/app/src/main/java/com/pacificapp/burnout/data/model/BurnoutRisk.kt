package com.pacificapp.burnout.data.model

import com.google.gson.annotations.SerializedName

data class BurnoutRisk(
    val id: Long = 0,
    @SerializedName("user_id")
    val userId: Long = 0,
    val date: String = "",
    @SerializedName("risk_level")
    val riskLevel: Int = 0,
    val factors: Map<String, String> = emptyMap(),
    val recommendations: List<String> = emptyList(),
    @SerializedName("created_at")
    val createdAt: String = ""
) {
    val category: BurnoutCategory
        get() = when {
            riskLevel <= 25 -> BurnoutCategory.LOW
            riskLevel <= 50 -> BurnoutCategory.MODERATE
            riskLevel <= 75 -> BurnoutCategory.HIGH
            else -> BurnoutCategory.CRITICAL
        }

    val riskPercent: Float
        get() = riskLevel / 100f

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

enum class BurnoutCategory(
    val label: String,
    val description: String,
    val color: Long
) {
    LOW(
        "Низкий",
        "Ваш уровень риска выгорания в норме. Продолжайте поддерживать баланс.",
        0xFF4CAF50
    ),
    MODERATE(
        "Умеренный",
        "Есть признаки усталости. Рекомендуется обратить внимание на отдых.",
        0xFFFFC107
    ),
    HIGH(
        "Высокий",
        "Высокий риск выгорания. Необходимо принять меры для восстановления.",
        0xFFFF9800
    ),
    CRITICAL(
        "Критический",
        "Критический уровень! Срочно требуется отдых и, возможно, консультация специалиста.",
        0xFFF44336
    )
}

data class DashboardSummary(
    @SerializedName("current_stress_level")
    val currentStressLevel: Double = 0.0,
    @SerializedName("average_sleep_hours")
    val averageSleepHours: Double = 0.0,
    @SerializedName("average_work_hours")
    val averageWorkHours: Double = 0.0,
    @SerializedName("burnout_risk_level")
    val burnoutRiskLevel: Int = 0,
    @SerializedName("active_recommendations")
    val activeRecommendations: Int = 0,
    @SerializedName("completed_recommendations")
    val completedRecommendations: Int = 0,
    @SerializedName("weekly_trend")
    val weeklyTrend: List<DailyRiskData> = emptyList()
) {
    val burnoutCategory: BurnoutCategory
        get() = when {
            burnoutRiskLevel <= 25 -> BurnoutCategory.LOW
            burnoutRiskLevel <= 50 -> BurnoutCategory.MODERATE
            burnoutRiskLevel <= 75 -> BurnoutCategory.HIGH
            else -> BurnoutCategory.CRITICAL
        }

    val sleepStatus: String
        get() = when {
            averageSleepHours >= 7.5 -> "Отлично"
            averageSleepHours >= 6.5 -> "Нормально"
            averageSleepHours >= 5.5 -> "Недостаточно"
            else -> "Критично"
        }

    val workloadStatus: String
        get() = when {
            averageWorkHours <= 8.0 -> "Нормально"
            averageWorkHours <= 9.0 -> "Умеренно"
            averageWorkHours <= 10.0 -> "Высоко"
            else -> "Избыточно"
        }
}

data class DailyRiskData(
    val date: String,
    @SerializedName("risk_level")
    val riskLevel: Int
)
