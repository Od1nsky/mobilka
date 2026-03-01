package com.pacificapp.burnout.data.model

import com.google.gson.annotations.SerializedName

data class WorkActivity(
    val id: Long = 0,
    @SerializedName("user_id")
    val userId: Long = 0,
    val date: String = "",
    @SerializedName("duration_hours")
    val durationHours: Double = 0.0,
    @SerializedName("breaks_count")
    val breaksCount: Int = 0,
    @SerializedName("breaks_total_minutes")
    val breaksTotalMinutes: Int = 0,
    val productivity: Int? = null,
    val notes: String = "",
    @SerializedName("created_at")
    val createdAt: String = ""
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

    val durationFormatted: String
        get() {
            val hours = durationHours.toInt()
            val minutes = ((durationHours - hours) * 60).toInt()
            return "${hours}ч ${minutes}м"
        }
}

enum class ProductivityCategory(val label: String, val color: Long) {
    LOW("Низкая", 0xFFF44336),
    MODERATE("Умеренная", 0xFFFF9800),
    GOOD("Хорошая", 0xFF4CAF50),
    EXCELLENT("Отличная", 0xFF2196F3)
}

data class CreateWorkRequest(
    val date: String,
    @SerializedName("duration_hours")
    val durationHours: Double,
    @SerializedName("breaks_count")
    val breaksCount: Int = 0,
    @SerializedName("breaks_total_minutes")
    val breaksTotalMinutes: Int = 0,
    val productivity: Int? = null,
    val notes: String = ""
)

data class WorkStatistics(
    @SerializedName("average_duration")
    val averageDuration: Double = 0.0,
    @SerializedName("average_productivity")
    val averageProductivity: Double = 0.0,
    @SerializedName("average_breaks_count")
    val averageBreaksCount: Int = 0,
    @SerializedName("total_records")
    val totalRecords: Int = 0,
    @SerializedName("daily_data")
    val dailyData: List<DailyWorkData> = emptyList()
)

data class DailyWorkData(
    val date: String,
    @SerializedName("duration_hours")
    val durationHours: Double,
    val productivity: Double,
    @SerializedName("breaks_count")
    val breaksCount: Int
)
