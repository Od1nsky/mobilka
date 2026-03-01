package com.pacificapp.burnout.data.models

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class StressLevelTest {

    private fun createStressLevel(level: Int): StressLevel {
        return StressLevel(
            id = 1L,
            userId = 1L,
            level = level,
            notes = "Test notes",
            createdAt = LocalDateTime.now()
        )
    }

    @Test
    fun `stressCategory is LOW when level is 30 or lower`() {
        assertEquals(StressCategory.LOW, createStressLevel(10).stressCategory)
        assertEquals(StressCategory.LOW, createStressLevel(20).stressCategory)
        assertEquals(StressCategory.LOW, createStressLevel(30).stressCategory)
    }

    @Test
    fun `stressCategory is MODERATE when level is between 31 and 60`() {
        assertEquals(StressCategory.MODERATE, createStressLevel(31).stressCategory)
        assertEquals(StressCategory.MODERATE, createStressLevel(45).stressCategory)
        assertEquals(StressCategory.MODERATE, createStressLevel(60).stressCategory)
    }

    @Test
    fun `stressCategory is HIGH when level is between 61 and 80`() {
        assertEquals(StressCategory.HIGH, createStressLevel(61).stressCategory)
        assertEquals(StressCategory.HIGH, createStressLevel(70).stressCategory)
        assertEquals(StressCategory.HIGH, createStressLevel(80).stressCategory)
    }

    @Test
    fun `stressCategory is VERY_HIGH when level is above 80`() {
        assertEquals(StressCategory.VERY_HIGH, createStressLevel(81).stressCategory)
        assertEquals(StressCategory.VERY_HIGH, createStressLevel(90).stressCategory)
        assertEquals(StressCategory.VERY_HIGH, createStressLevel(100).stressCategory)
    }

    @Test
    fun `stress level correctly stores all values`() {
        val createdAt = LocalDateTime.of(2024, 6, 15, 10, 30)
        val stressLevel = StressLevel(
            id = 42L,
            userId = 5L,
            level = 55,
            notes = "Stressful meeting",
            createdAt = createdAt
        )

        assertEquals(42L, stressLevel.id)
        assertEquals(5L, stressLevel.userId)
        assertEquals(55, stressLevel.level)
        assertEquals("Stressful meeting", stressLevel.notes)
        assertEquals(createdAt, stressLevel.createdAt)
    }

    @Test
    fun `stress level with default values`() {
        val stressLevel = StressLevel()

        assertEquals(0L, stressLevel.id)
        assertEquals(0L, stressLevel.userId)
        assertEquals(0, stressLevel.level)
        assertEquals("", stressLevel.notes)
    }

    @Test
    fun `stress level with empty notes`() {
        val stressLevel = createStressLevel(50).copy(notes = "")
        assertEquals("", stressLevel.notes)
    }

    @Test
    fun `stress statistics correctly stores all values`() {
        val dailyData = listOf(
            DailyStressData(date = "2024-06-15", averageLevel = 55.0, count = 3),
            DailyStressData(date = "2024-06-14", averageLevel = 45.0, count = 2)
        )
        val statistics = StressStatistics(
            averageLevel = 50.0,
            minLevel = 30,
            maxLevel = 70,
            totalRecords = 25,
            dailyData = dailyData
        )

        assertEquals(50.0, statistics.averageLevel, 0.01)
        assertEquals(30, statistics.minLevel)
        assertEquals(70, statistics.maxLevel)
        assertEquals(25, statistics.totalRecords)
        assertEquals(2, statistics.dailyData.size)
    }

    @Test
    fun `stress statistics with default values`() {
        val statistics = StressStatistics()

        assertEquals(0.0, statistics.averageLevel, 0.01)
        assertEquals(0, statistics.minLevel)
        assertEquals(0, statistics.maxLevel)
        assertEquals(0, statistics.totalRecords)
        assertTrue(statistics.dailyData.isEmpty())
    }

    @Test
    fun `daily stress data correctly stores all values`() {
        val data = DailyStressData(
            date = "2024-06-15",
            averageLevel = 55.5,
            count = 5
        )

        assertEquals("2024-06-15", data.date)
        assertEquals(55.5, data.averageLevel, 0.01)
        assertEquals(5, data.count)
    }

    @Test
    fun `stress category enum values exist`() {
        assertEquals(4, StressCategory.values().size)
        assertTrue(StressCategory.values().contains(StressCategory.LOW))
        assertTrue(StressCategory.values().contains(StressCategory.MODERATE))
        assertTrue(StressCategory.values().contains(StressCategory.HIGH))
        assertTrue(StressCategory.values().contains(StressCategory.VERY_HIGH))
    }

    @Test
    fun `stress level boundary at level 30-31`() {
        assertEquals(StressCategory.LOW, createStressLevel(30).stressCategory)
        assertEquals(StressCategory.MODERATE, createStressLevel(31).stressCategory)
    }

    @Test
    fun `stress level boundary at level 60-61`() {
        assertEquals(StressCategory.MODERATE, createStressLevel(60).stressCategory)
        assertEquals(StressCategory.HIGH, createStressLevel(61).stressCategory)
    }

    @Test
    fun `stress level boundary at level 80-81`() {
        assertEquals(StressCategory.HIGH, createStressLevel(80).stressCategory)
        assertEquals(StressCategory.VERY_HIGH, createStressLevel(81).stressCategory)
    }
}
