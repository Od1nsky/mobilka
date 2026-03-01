package com.pacificapp.burnout.data.models

import java.time.LocalDateTime

data class StressLevel(
    val id: Long = 0,
    val userId: Long = 0,
    val level: Int = 0,
    val notes: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val stressCategory: StressCategory
        get() = when {
            level <= 30 -> StressCategory.LOW
            level <= 60 -> StressCategory.MODERATE
            level <= 80 -> StressCategory.HIGH
            else -> StressCategory.VERY_HIGH
        }
}

enum class StressCategory {
    LOW, MODERATE, HIGH, VERY_HIGH
}

data class StressStatistics(
    val averageLevel: Double = 0.0,
    val minLevel: Int = 0,
    val maxLevel: Int = 0,
    val totalRecords: Int = 0,
    val dailyData: List<DailyStressData> = emptyList()
)

data class DailyStressData(
    val date: String,
    val averageLevel: Double,
    val count: Int
)
