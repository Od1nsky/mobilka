package com.pacificapp.burnout.data.model

import com.google.gson.annotations.SerializedName

data class StressLevel(
    val id: Long = 0,
    @SerializedName("user_id")
    val userId: Long = 0,
    val level: Int = 0,
    val notes: String = "",
    @SerializedName("created_at")
    val createdAt: String = ""
) {
    val category: StressCategory
        get() = when {
            level <= 30 -> StressCategory.LOW
            level <= 60 -> StressCategory.MODERATE
            level <= 80 -> StressCategory.HIGH
            else -> StressCategory.VERY_HIGH
        }

    val levelPercent: Float
        get() = level / 100f
}

enum class StressCategory(val label: String, val color: Long) {
    LOW("Низкий", 0xFF4CAF50),
    MODERATE("Умеренный", 0xFFFFC107),
    HIGH("Высокий", 0xFFFF9800),
    VERY_HIGH("Очень высокий", 0xFFF44336)
}

data class CreateStressRequest(
    val level: Int,
    val notes: String = ""
)

data class StressStatistics(
    @SerializedName("average_level")
    val averageLevel: Double = 0.0,
    @SerializedName("min_level")
    val minLevel: Int = 0,
    @SerializedName("max_level")
    val maxLevel: Int = 0,
    @SerializedName("total_records")
    val totalRecords: Int = 0,
    @SerializedName("daily_data")
    val dailyData: List<DailyStressData> = emptyList()
)

data class DailyStressData(
    val date: String,
    @SerializedName("average_level")
    val averageLevel: Double,
    val count: Int
)
