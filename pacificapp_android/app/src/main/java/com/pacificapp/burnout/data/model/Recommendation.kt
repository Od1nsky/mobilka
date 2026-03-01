package com.pacificapp.burnout.data.model

import com.google.gson.annotations.SerializedName

data class Recommendation(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val category: RecommendationCategory = RecommendationCategory.ACTIVITY,
    val priority: RecommendationPriority = RecommendationPriority.MEDIUM,
    @SerializedName("duration_minutes")
    val durationMinutes: Int = 0,
    @SerializedName("is_quick")
    val isQuick: Boolean = false
)

data class UserRecommendation(
    val id: String = "",
    @SerializedName("user_id")
    val userId: Long = 0,
    @SerializedName("recommendation_id")
    val recommendationId: Long = 0,
    val title: String = "",
    val description: String = "",
    val category: RecommendationCategory = RecommendationCategory.ACTIVITY,
    val priority: RecommendationPriority = RecommendationPriority.MEDIUM,
    @SerializedName("duration_minutes")
    val durationMinutes: Int = 0,
    @SerializedName("is_quick")
    val isQuick: Boolean = false,
    val status: RecommendationStatus = RecommendationStatus.NEW,
    val reason: String = "",
    @SerializedName("user_feedback")
    val userFeedback: String = "",
    @SerializedName("user_rating")
    val userRating: Int = 0,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("completed_at")
    val completedAt: String? = null
) {
    val durationFormatted: String
        get() = when {
            durationMinutes >= 60 -> "${durationMinutes / 60}ч ${durationMinutes % 60}м"
            else -> "${durationMinutes}м"
        }
}

enum class RecommendationCategory(val label: String, val icon: String) {
    SLEEP("Сон", "🌙"),
    STRESS("Стресс", "🧘"),
    WORK("Работа", "💼"),
    ACTIVITY("Активность", "🏃")
}

enum class RecommendationPriority(val label: String, val color: Long) {
    HIGH("Высокий", 0xFFF44336),
    MEDIUM("Средний", 0xFFFFC107),
    LOW("Низкий", 0xFF4CAF50)
}

enum class RecommendationStatus(val label: String) {
    NEW("Новая"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Выполнена"),
    SKIPPED("Пропущена")
}

data class RecommendationStats(
    @SerializedName("total_recommendations")
    val totalRecommendations: Int = 0,
    @SerializedName("new_count")
    val newCount: Int = 0,
    @SerializedName("in_progress_count")
    val inProgressCount: Int = 0,
    @SerializedName("completed_count")
    val completedCount: Int = 0,
    @SerializedName("skipped_count")
    val skippedCount: Int = 0,
    @SerializedName("average_rating")
    val averageRating: Double = 0.0
)
