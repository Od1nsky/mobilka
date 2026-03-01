package com.pacificapp.burnout.data.models

import java.time.LocalDate
import java.time.LocalDateTime

data class WorkActivity(
    val id: Long = 0,
    val userId: Long = 0,
    val date: LocalDate = LocalDate.now(),
    val durationHours: Double = 0.0,
    val breaksCount: Int = 0,
    val breaksTotalMinutes: Int = 0,
    val productivity: Int? = null,
    val notes: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val isOvertime: Boolean
        get() = durationHours > 8.0

    val hasAdequateBreaks: Boolean
        get() = breaksCount >= 2 && breaksTotalMinutes >= 30

    val productivityCategory: ProductivityCategory?
        get() = productivity?.let {
            when {
                it <= 3 -> ProductivityCategory.LOW
                it <= 5 -> ProductivityCategory.MODERATE
                it <= 7 -> ProductivityCategory.GOOD
                else -> ProductivityCategory.EXCELLENT
            }
        }
}

enum class ProductivityCategory {
    LOW, MODERATE, GOOD, EXCELLENT
}

data class WorkStatistics(
    val averageDuration: Double = 0.0,
    val averageProductivity: Double = 0.0,
    val averageBreaksCount: Int = 0,
    val averageBreaksDuration: Int = 0,
    val totalRecords: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val dailyData: List<DailyWorkData> = emptyList()
)

data class DailyWorkData(
    val date: String,
    val durationHours: Double,
    val productivity: Double,
    val breaksCount: Int,
    val breaksDurationMinutes: Int
)
