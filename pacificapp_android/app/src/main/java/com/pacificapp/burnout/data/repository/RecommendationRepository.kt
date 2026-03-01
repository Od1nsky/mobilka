package com.pacificapp.burnout.data.repository

import com.pacificapp.burnout.data.model.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor() {

    private val mockRecommendations = mutableListOf(
        UserRecommendation(
            id = UUID.randomUUID().toString(),
            userId = 1,
            title = "Сделайте перерыв",
            description = "Вы работаете уже 2 часа без перерыва. Встаньте и разомнитесь.",
            category = RecommendationCategory.WORK,
            priority = RecommendationPriority.HIGH,
            durationMinutes = 10,
            isQuick = true,
            status = RecommendationStatus.NEW,
            reason = "Длительная работа повышает риск выгорания"
        ),
        UserRecommendation(
            id = UUID.randomUUID().toString(),
            userId = 1,
            title = "Дыхательные упражнения",
            description = "Вдох на 4 счёта, задержка на 4, выдох на 4.",
            category = RecommendationCategory.STRESS,
            priority = RecommendationPriority.MEDIUM,
            durationMinutes = 5,
            isQuick = true,
            status = RecommendationStatus.NEW,
            reason = "Повышенный уровень стресса"
        ),
        UserRecommendation(
            id = UUID.randomUUID().toString(),
            userId = 1,
            title = "Улучшите качество сна",
            description = "Ложитесь спать в одно время. Избегайте экранов за час до сна.",
            category = RecommendationCategory.SLEEP,
            priority = RecommendationPriority.HIGH,
            durationMinutes = 0,
            isQuick = false,
            status = RecommendationStatus.IN_PROGRESS,
            reason = "Недостаточный сон"
        ),
        UserRecommendation(
            id = UUID.randomUUID().toString(),
            userId = 1,
            title = "Короткая прогулка",
            description = "Выйдите на свежий воздух на 15-20 минут.",
            category = RecommendationCategory.ACTIVITY,
            priority = RecommendationPriority.LOW,
            durationMinutes = 20,
            isQuick = false,
            status = RecommendationStatus.NEW,
            reason = "Низкая физическая активность"
        )
    )

    suspend fun getRecommendations(): Result<List<UserRecommendation>> {
        delay(300)
        return Result.Success(mockRecommendations.sortedByDescending { it.priority })
    }

    suspend fun getActiveRecommendations(): Result<List<UserRecommendation>> {
        delay(300)
        val active = mockRecommendations.filter {
            it.status == RecommendationStatus.NEW || it.status == RecommendationStatus.IN_PROGRESS
        }
        return Result.Success(active)
    }

    suspend fun updateStatus(id: String, status: RecommendationStatus): Result<UserRecommendation> {
        delay(300)
        val index = mockRecommendations.indexOfFirst { it.id == id }
        if (index >= 0) {
            val updated = mockRecommendations[index].copy(
                status = status,
                completedAt = if (status == RecommendationStatus.COMPLETED)
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) else null
            )
            mockRecommendations[index] = updated
            return Result.Success(updated)
        }
        return Result.Error("Не найдено")
    }

    suspend fun getStats(): Result<RecommendationStats> {
        delay(200)
        return Result.Success(
            RecommendationStats(
                totalRecommendations = mockRecommendations.size,
                newCount = mockRecommendations.count { it.status == RecommendationStatus.NEW },
                inProgressCount = mockRecommendations.count { it.status == RecommendationStatus.IN_PROGRESS },
                completedCount = mockRecommendations.count { it.status == RecommendationStatus.COMPLETED },
                skippedCount = mockRecommendations.count { it.status == RecommendationStatus.SKIPPED }
            )
        )
    }
}
