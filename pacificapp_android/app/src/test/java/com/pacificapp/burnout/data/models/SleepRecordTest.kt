package com.pacificapp.burnout.data.models

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class SleepRecordTest {

    private fun createSleepRecord(quality: Int?, durationHours: Double = 7.0): SleepRecord {
        return SleepRecord(
            id = 1L,
            userId = 1L,
            date = LocalDate.now(),
            durationHours = durationHours,
            quality = quality,
            notes = "Test notes",
            createdAt = LocalDateTime.now()
        )
    }

    @Test
    fun `qualityCategory is POOR when quality is 3 or lower`() {
        assertEquals(SleepQualityCategory.POOR, createSleepRecord(1).qualityCategory)
        assertEquals(SleepQualityCategory.POOR, createSleepRecord(2).qualityCategory)
        assertEquals(SleepQualityCategory.POOR, createSleepRecord(3).qualityCategory)
    }

    @Test
    fun `qualityCategory is FAIR when quality is between 4 and 5`() {
        assertEquals(SleepQualityCategory.FAIR, createSleepRecord(4).qualityCategory)
        assertEquals(SleepQualityCategory.FAIR, createSleepRecord(5).qualityCategory)
    }

    @Test
    fun `qualityCategory is GOOD when quality is between 6 and 7`() {
        assertEquals(SleepQualityCategory.GOOD, createSleepRecord(6).qualityCategory)
        assertEquals(SleepQualityCategory.GOOD, createSleepRecord(7).qualityCategory)
    }

    @Test
    fun `qualityCategory is EXCELLENT when quality is 8 or higher`() {
        assertEquals(SleepQualityCategory.EXCELLENT, createSleepRecord(8).qualityCategory)
        assertEquals(SleepQualityCategory.EXCELLENT, createSleepRecord(9).qualityCategory)
        assertEquals(SleepQualityCategory.EXCELLENT, createSleepRecord(10).qualityCategory)
    }

    @Test
    fun `qualityCategory is null when quality is null`() {
        val record = createSleepRecord(null)
        assertNull(record.qualityCategory)
    }

    @Test
    fun `isSufficientSleep is true when durationHours is 7 or more`() {
        assertTrue(createSleepRecord(5, 7.0).isSufficientSleep)
        assertTrue(createSleepRecord(5, 8.0).isSufficientSleep)
        assertTrue(createSleepRecord(5, 9.0).isSufficientSleep)
    }

    @Test
    fun `isSufficientSleep is false when durationHours is less than 7`() {
        assertFalse(createSleepRecord(5, 6.9).isSufficientSleep)
        assertFalse(createSleepRecord(5, 6.0).isSufficientSleep)
        assertFalse(createSleepRecord(5, 5.0).isSufficientSleep)
    }

    @Test
    fun `sleep record correctly stores all values`() {
        val date = LocalDate.of(2024, 6, 15)
        val createdAt = LocalDateTime.of(2024, 6, 15, 8, 0)
        val record = SleepRecord(
            id = 42L,
            userId = 5L,
            date = date,
            durationHours = 7.5,
            quality = 8,
            notes = "Good night sleep",
            createdAt = createdAt
        )

        assertEquals(42L, record.id)
        assertEquals(5L, record.userId)
        assertEquals(date, record.date)
        assertEquals(7.5, record.durationHours, 0.01)
        assertEquals(8, record.quality)
        assertEquals("Good night sleep", record.notes)
        assertEquals(createdAt, record.createdAt)
    }

    @Test
    fun `sleep record with default values`() {
        val record = SleepRecord()

        assertEquals(0L, record.id)
        assertEquals(0L, record.userId)
        assertEquals(0.0, record.durationHours, 0.01)
        assertNull(record.quality)
        assertEquals("", record.notes)
    }

    @Test
    fun `sleep record with empty notes`() {
        val record = createSleepRecord(5).copy(notes = "")
        assertEquals("", record.notes)
    }

    @Test
    fun `sleep statistics correctly stores all values`() {
        val dailyData = listOf(
            DailySleepData(date = "2024-06-15", durationHours = 7.5, quality = 8.0),
            DailySleepData(date = "2024-06-14", durationHours = 6.5, quality = 6.0)
        )
        val statistics = SleepStatistics(
            averageDuration = 7.0,
            averageQuality = 7.0,
            totalRecords = 30,
            dailyData = dailyData
        )

        assertEquals(7.0, statistics.averageDuration, 0.01)
        assertEquals(7.0, statistics.averageQuality, 0.01)
        assertEquals(30, statistics.totalRecords)
        assertEquals(2, statistics.dailyData.size)
    }

    @Test
    fun `sleep statistics with default values`() {
        val statistics = SleepStatistics()

        assertEquals(0.0, statistics.averageDuration, 0.01)
        assertEquals(0.0, statistics.averageQuality, 0.01)
        assertEquals(0, statistics.totalRecords)
        assertTrue(statistics.dailyData.isEmpty())
    }

    @Test
    fun `daily sleep data correctly stores all values`() {
        val data = DailySleepData(
            date = "2024-06-15",
            durationHours = 7.5,
            quality = 8.0
        )

        assertEquals("2024-06-15", data.date)
        assertEquals(7.5, data.durationHours, 0.01)
        assertEquals(8.0, data.quality, 0.01)
    }

    @Test
    fun `sleep quality category enum values exist`() {
        assertEquals(4, SleepQualityCategory.values().size)
        assertTrue(SleepQualityCategory.values().contains(SleepQualityCategory.POOR))
        assertTrue(SleepQualityCategory.values().contains(SleepQualityCategory.FAIR))
        assertTrue(SleepQualityCategory.values().contains(SleepQualityCategory.GOOD))
        assertTrue(SleepQualityCategory.values().contains(SleepQualityCategory.EXCELLENT))
    }

    @Test
    fun `quality boundary at level 3-4`() {
        assertEquals(SleepQualityCategory.POOR, createSleepRecord(3).qualityCategory)
        assertEquals(SleepQualityCategory.FAIR, createSleepRecord(4).qualityCategory)
    }

    @Test
    fun `quality boundary at level 5-6`() {
        assertEquals(SleepQualityCategory.FAIR, createSleepRecord(5).qualityCategory)
        assertEquals(SleepQualityCategory.GOOD, createSleepRecord(6).qualityCategory)
    }

    @Test
    fun `quality boundary at level 7-8`() {
        assertEquals(SleepQualityCategory.GOOD, createSleepRecord(7).qualityCategory)
        assertEquals(SleepQualityCategory.EXCELLENT, createSleepRecord(8).qualityCategory)
    }
}
