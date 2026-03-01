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
class SleepRepository @Inject constructor() {

    private val mockSleepRecords = mutableListOf<SleepRecord>()
    private var nextId = 1L

    init {
        repeat(7) { i ->
            mockSleepRecords.add(
                SleepRecord(
                    id = nextId++,
                    userId = 1,
                    date = LocalDate.now().minusDays(i.toLong()).format(DateTimeFormatter.ISO_DATE),
                    durationHours = Random.nextDouble(5.0, 9.0),
                    quality = Random.nextInt(3, 10),
                    notes = "",
                    createdAt = LocalDateTime.now().minusDays(i.toLong()).format(DateTimeFormatter.ISO_DATE_TIME)
                )
            )
        }
    }

    suspend fun getSleepRecords(): Result<List<SleepRecord>> {
        delay(300)
        return Result.Success(mockSleepRecords.sortedByDescending { it.date })
    }

    suspend fun createSleepRecord(date: String, durationHours: Double, quality: Int?, notes: String): Result<SleepRecord> {
        delay(500)
        if (durationHours <= 0 || durationHours > 24) {
            return Result.Error("Некорректная продолжительность сна")
        }

        val newRecord = SleepRecord(
            id = nextId++,
            userId = 1,
            date = date,
            durationHours = durationHours,
            quality = quality,
            notes = notes,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
        mockSleepRecords.add(0, newRecord)
        return Result.Success(newRecord)
    }

    suspend fun deleteSleepRecord(id: Long): Result<Unit> {
        delay(300)
        mockSleepRecords.removeIf { it.id == id }
        return Result.Success(Unit)
    }

    suspend fun getStatistics(): Result<SleepStatistics> {
        delay(300)
        val records = mockSleepRecords
        if (records.isEmpty()) return Result.Success(SleepStatistics())

        return Result.Success(
            SleepStatistics(
                averageDuration = records.map { it.durationHours }.average(),
                averageQuality = records.mapNotNull { it.quality }.average(),
                totalRecords = records.size,
                dailyData = records.map { DailySleepData(it.date, it.durationHours, it.quality?.toDouble() ?: 0.0) }
            )
        )
    }
}
