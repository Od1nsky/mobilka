package com.pacificapp.burnout.data.repository

import com.pacificapp.burnout.data.model.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class StressRepository @Inject constructor() {

    private val mockStressLevels = mutableListOf<StressLevel>()
    private var nextId = 1L

    init {
        repeat(7) { i ->
            mockStressLevels.add(
                StressLevel(
                    id = nextId++,
                    userId = 1,
                    level = Random.nextInt(20, 80),
                    notes = if (i % 2 == 0) "Рабочий день" else "",
                    createdAt = LocalDateTime.now().minusDays(i.toLong())
                        .format(DateTimeFormatter.ISO_DATE_TIME)
                )
            )
        }
    }

    suspend fun getStressLevels(): Result<List<StressLevel>> {
        delay(300)
        return Result.Success(mockStressLevels.sortedByDescending { it.createdAt })
    }

    suspend fun createStressLevel(level: Int, notes: String): Result<StressLevel> {
        delay(500)
        if (level !in 1..100) {
            return Result.Error("Уровень должен быть от 1 до 100")
        }

        val newLevel = StressLevel(
            id = nextId++,
            userId = 1,
            level = level,
            notes = notes,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
        mockStressLevels.add(0, newLevel)
        return Result.Success(newLevel)
    }

    suspend fun deleteStressLevel(id: Long): Result<Unit> {
        delay(300)
        mockStressLevels.removeIf { it.id == id }
        return Result.Success(Unit)
    }

    suspend fun getStatistics(): Result<StressStatistics> {
        delay(300)
        val levels = mockStressLevels
        if (levels.isEmpty()) return Result.Success(StressStatistics())

        val dailyData = levels.groupBy { it.createdAt.substringBefore("T") }
            .map { (date, items) ->
                DailyStressData(date, items.map { it.level }.average(), items.size)
            }.sortedByDescending { it.date }

        return Result.Success(
            StressStatistics(
                averageLevel = levels.map { it.level }.average(),
                minLevel = levels.minOf { it.level },
                maxLevel = levels.maxOf { it.level },
                totalRecords = levels.size,
                dailyData = dailyData
            )
        )
    }
}
