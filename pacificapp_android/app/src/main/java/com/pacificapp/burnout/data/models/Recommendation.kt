package com.pacificapp.burnout.data.models

import java.time.LocalDateTime

data class Recommendation(
    val id: Long = 0,
    val typeId: Long = 0,
    val title: String = "",
    val description: String = "",
    val durationMinutes: Int = 0,
    val category: RecommendationCategory = RecommendationCategory.GENERAL,
    val priority: RecommendationPriority = RecommendationPriority.MEDIUM,
    val isQuick: Boolean = false,
    val type: Int = 0
)

enum class RecommendationCategory {
    SLEEP, STRESS, WORK, ACTIVITY, GENERAL
}

enum class RecommendationPriority {
    HIGH, MEDIUM, LOW
}

data class UserRecommendation(
    val id: String = "",
    val userId: Long = 0,
    val recommendationId: Long = 0,
    val recommendation: Recommendation? = null,
    val status: RecommendationStatus = RecommendationStatus.NEW,
    val reason: String = "",
    val userFeedback: String = "",
    val userRating: Int = 0,
    val completedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val isActive: Boolean
        get() = status == RecommendationStatus.NEW || status == RecommendationStatus.IN_PROGRESS
}

enum class RecommendationStatus {
    NEW, IN_PROGRESS, COMPLETED, SKIPPED
}

data class RecommendationStats(
    val totalAssigned: Int = 0,
    val completed: Int = 0,
    val skipped: Int = 0,
    val inProgress: Int = 0,
    val averageRating: Double = 0.0
)
