package com.pacificapp.burnout.data.models

import java.time.LocalDate
import java.time.LocalDateTime

data class SleepRecord(
    val id: Long = 0,
    val userId: Long = 0,
    val date: LocalDate = LocalDate.now(),
    val durationHours: Double = 0.0,
    val quality: Int? = null,
    val notes: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val qualityCategory: SleepQualityCategory?
        get() = quality?.let {
            when {
                it <= 3 -> SleepQualityCategory.POOR
                it <= 5 -> SleepQualityCategory.FAIR
                it <= 7 -> SleepQualityCategory.GOOD
                else -> SleepQualityCategory.EXCELLENT
            }
        }

    val isSufficientSleep: Boolean
        get() = durationHours >= 7.0
}

enum class SleepQualityCategory {
    POOR, FAIR, GOOD, EXCELLENT
}

data class SleepStatistics(
    val averageDuration: Double = 0.0,
    val averageQuality: Double = 0.0,
    val totalRecords: Int = 0,
    val dailyData: List<DailySleepData> = emptyList()
)

data class DailySleepData(
    val date: String,
    val durationHours: Double,
    val quality: Double
)
