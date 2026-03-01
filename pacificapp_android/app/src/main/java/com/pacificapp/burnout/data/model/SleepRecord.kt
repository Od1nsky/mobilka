package com.pacificapp.burnout.data.model

import com.google.gson.annotations.SerializedName

data class SleepRecord(
    val id: Long = 0,
    @SerializedName("user_id")
    val userId: Long = 0,
    val date: String = "",
    @SerializedName("duration_hours")
    val durationHours: Double = 0.0,
    val quality: Int? = null,
    val notes: String = "",
    @SerializedName("created_at")
    val createdAt: String = ""
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

    val durationFormatted: String
        get() {
            val hours = durationHours.toInt()
            val minutes = ((durationHours - hours) * 60).toInt()
            return "${hours}ч ${minutes}м"
        }
}

enum class SleepQualityCategory(val label: String, val color: Long) {
    POOR("Плохое", 0xFFF44336),
    FAIR("Удовлетворительное", 0xFFFF9800),
    GOOD("Хорошее", 0xFF4CAF50),
    EXCELLENT("Отличное", 0xFF2196F3)
}

data class CreateSleepRequest(
    val date: String,
    @SerializedName("duration_hours")
    val durationHours: Double,
    val quality: Int?,
    val notes: String = ""
)

data class SleepStatistics(
    @SerializedName("average_duration")
    val averageDuration: Double = 0.0,
    @SerializedName("average_quality")
    val averageQuality: Double = 0.0,
    @SerializedName("total_records")
    val totalRecords: Int = 0,
    @SerializedName("daily_data")
    val dailyData: List<DailySleepData> = emptyList()
)

data class DailySleepData(
    val date: String,
    @SerializedName("duration_hours")
    val durationHours: Double,
    val quality: Double
)
