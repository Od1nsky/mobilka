package com.pacificapp.burnout.data.repository

import com.pacificapp.burnout.data.model.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class WorkRepository @Inject constructor() {

    private val mockWorkActivities = mutableListOf<WorkActivity>()
    private var nextId = 1L

    init {
        repeat(5) { i ->
            mockWorkActivities.add(
                WorkActivity(
                    id = nextId++,
                    userId = 1,
                    date = LocalDate.now().minusDays(i.toLong()).format(DateTimeFormatter.ISO_DATE),
                    durationHours = Random.nextDouble(7.0, 10.0),
                    breaksCount = Random.nextInt(1, 5),
                    breaksTotalMinutes = Random.nextInt(15, 60),
                    productivity = Random.nextInt(4, 10),
                    notes = "",
                    createdAt = LocalDateTime.now().minusDays(i.toLong()).format(DateTimeFormatter.ISO_DATE_TIME)
                )
            )
        }
    }

    suspend fun getWorkActivities(): Result<List<WorkActivity>> {
        delay(300)
        return Result.Success(mockWorkActivities.sortedByDescending { it.date })
    }

    suspend fun createWorkActivity(
        date: String,
        durationHours: Double,
        breaksCount: Int,
        breaksTotalMinutes: Int,
        productivity: Int?,
        notes: String
    ): Result<WorkActivity> {
        delay(500)
        if (durationHours <= 0 || durationHours > 24) {
            return Result.Error("Некорректная продолжительность")
        }

        val newActivity = WorkActivity(
            id = nextId++,
            userId = 1,
            date = date,
            durationHours = durationHours,
            breaksCount = breaksCount,
            breaksTotalMinutes = breaksTotalMinutes,
            productivity = productivity,
            notes = notes,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
        mockWorkActivities.add(0, newActivity)
        return Result.Success(newActivity)
    }

    suspend fun deleteWorkActivity(id: Long): Result<Unit> {
        delay(300)
        mockWorkActivities.removeIf { it.id == id }
        return Result.Success(Unit)
    }

    suspend fun getStatistics(): Result<WorkStatistics> {
        delay(300)
        val activities = mockWorkActivities
        if (activities.isEmpty()) return Result.Success(WorkStatistics())

        return Result.Success(
            WorkStatistics(
                averageDuration = activities.map { it.durationHours }.average(),
                averageProductivity = activities.mapNotNull { it.productivity }.average(),
                averageBreaksCount = activities.map { it.breaksCount }.average().toInt(),
                totalRecords = activities.size,
                dailyData = activities.map {
                    DailyWorkData(it.date, it.durationHours, it.productivity?.toDouble() ?: 0.0, it.breaksCount)
                }
            )
        )
    }
}
