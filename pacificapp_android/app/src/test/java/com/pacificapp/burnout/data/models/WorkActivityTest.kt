package com.pacificapp.burnout.data.models

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class WorkActivityTest {

    private fun createWorkActivity(
        productivity: Int? = 5,
        durationHours: Double = 8.0,
        breaksCount: Int = 2,
        breaksTotalMinutes: Int = 30
    ): WorkActivity {
        return WorkActivity(
            id = 1L,
            userId = 1L,
            date = LocalDate.now(),
            durationHours = durationHours,
            breaksCount = breaksCount,
            breaksTotalMinutes = breaksTotalMinutes,
            productivity = productivity,
            notes = "Test notes",
            createdAt = LocalDateTime.now()
        )
    }

    @Test
    fun `productivityCategory is LOW when productivity is 3 or lower`() {
        assertEquals(ProductivityCategory.LOW, createWorkActivity(1).productivityCategory)
        assertEquals(ProductivityCategory.LOW, createWorkActivity(2).productivityCategory)
        assertEquals(ProductivityCategory.LOW, createWorkActivity(3).productivityCategory)
    }

    @Test
    fun `productivityCategory is MODERATE when productivity is between 4 and 5`() {
        assertEquals(ProductivityCategory.MODERATE, createWorkActivity(4).productivityCategory)
        assertEquals(ProductivityCategory.MODERATE, createWorkActivity(5).productivityCategory)
    }

    @Test
    fun `productivityCategory is GOOD when productivity is between 6 and 7`() {
        assertEquals(ProductivityCategory.GOOD, createWorkActivity(6).productivityCategory)
        assertEquals(ProductivityCategory.GOOD, createWorkActivity(7).productivityCategory)
    }

    @Test
    fun `productivityCategory is EXCELLENT when productivity is 8 or higher`() {
        assertEquals(ProductivityCategory.EXCELLENT, createWorkActivity(8).productivityCategory)
        assertEquals(ProductivityCategory.EXCELLENT, createWorkActivity(9).productivityCategory)
        assertEquals(ProductivityCategory.EXCELLENT, createWorkActivity(10).productivityCategory)
    }

    @Test
    fun `productivityCategory is null when productivity is null`() {
        val activity = createWorkActivity(null)
        assertNull(activity.productivityCategory)
    }

    @Test
    fun `isOvertime is true when durationHours is greater than 8`() {
        assertTrue(createWorkActivity(durationHours = 8.1).isOvertime)
        assertTrue(createWorkActivity(durationHours = 9.0).isOvertime)
        assertTrue(createWorkActivity(durationHours = 10.0).isOvertime)
    }

    @Test
    fun `isOvertime is false when durationHours is 8 or less`() {
        assertFalse(createWorkActivity(durationHours = 8.0).isOvertime)
        assertFalse(createWorkActivity(durationHours = 7.0).isOvertime)
        assertFalse(createWorkActivity(durationHours = 6.0).isOvertime)
    }

    @Test
    fun `hasAdequateBreaks is true when breaksCount is at least 2 and breaksTotalMinutes is at least 30`() {
        assertTrue(createWorkActivity(breaksCount = 2, breaksTotalMinutes = 30).hasAdequateBreaks)
        assertTrue(createWorkActivity(breaksCount = 3, breaksTotalMinutes = 45).hasAdequateBreaks)
        assertTrue(createWorkActivity(breaksCount = 5, breaksTotalMinutes = 60).hasAdequateBreaks)
    }

    @Test
    fun `hasAdequateBreaks is false when breaksCount is less than 2`() {
        assertFalse(createWorkActivity(breaksCount = 1, breaksTotalMinutes = 30).hasAdequateBreaks)
        assertFalse(createWorkActivity(breaksCount = 0, breaksTotalMinutes = 60).hasAdequateBreaks)
    }

    @Test
    fun `hasAdequateBreaks is false when breaksTotalMinutes is less than 30`() {
        assertFalse(createWorkActivity(breaksCount = 2, breaksTotalMinutes = 29).hasAdequateBreaks)
        assertFalse(createWorkActivity(breaksCount = 5, breaksTotalMinutes = 10).hasAdequateBreaks)
    }

    @Test
    fun `work activity correctly stores all values`() {
        val date = LocalDate.of(2024, 6, 15)
        val createdAt = LocalDateTime.of(2024, 6, 15, 18, 0)
        val activity = WorkActivity(
            id = 42L,
            userId = 5L,
            date = date,
            durationHours = 9.5,
            breaksCount = 3,
            breaksTotalMinutes = 45,
            productivity = 8,
            notes = "Productive day",
            createdAt = createdAt
        )

        assertEquals(42L, activity.id)
        assertEquals(5L, activity.userId)
        assertEquals(date, activity.date)
        assertEquals(9.5, activity.durationHours, 0.01)
        assertEquals(3, activity.breaksCount)
        assertEquals(45, activity.breaksTotalMinutes)
        assertEquals(8, activity.productivity)
        assertEquals("Productive day", activity.notes)
        assertEquals(createdAt, activity.createdAt)
    }

    @Test
    fun `work activity with default values`() {
        val activity = WorkActivity()

        assertEquals(0L, activity.id)
        assertEquals(0L, activity.userId)
        assertEquals(0.0, activity.durationHours, 0.01)
        assertEquals(0, activity.breaksCount)
        assertEquals(0, activity.breaksTotalMinutes)
        assertNull(activity.productivity)
        assertEquals("", activity.notes)
    }

    @Test
    fun `work activity with empty notes`() {
        val activity = createWorkActivity().copy(notes = "")
        assertEquals("", activity.notes)
    }

    @Test
    fun `work statistics correctly stores all values`() {
        val dailyData = listOf(
            DailyWorkData(
                date = "2024-06-15",
                durationHours = 8.0,
                productivity = 7.0,
                breaksCount = 2,
                breaksDurationMinutes = 30
            ),
            DailyWorkData(
                date = "2024-06-14",
                durationHours = 9.0,
                productivity = 6.0,
                breaksCount = 3,
                breaksDurationMinutes = 45
            )
        )
        val statistics = WorkStatistics(
            averageDuration = 8.5,
            averageProductivity = 6.5,
            averageBreaksCount = 2,
            averageBreaksDuration = 37,
            totalRecords = 50,
            startDate = "2024-06-01",
            endDate = "2024-06-15",
            dailyData = dailyData
        )

        assertEquals(8.5, statistics.averageDuration, 0.01)
        assertEquals(6.5, statistics.averageProductivity, 0.01)
        assertEquals(2, statistics.averageBreaksCount)
        assertEquals(37, statistics.averageBreaksDuration)
        assertEquals(50, statistics.totalRecords)
        assertEquals("2024-06-01", statistics.startDate)
        assertEquals("2024-06-15", statistics.endDate)
        assertEquals(2, statistics.dailyData.size)
    }

    @Test
    fun `work statistics with default values`() {
        val statistics = WorkStatistics()

        assertEquals(0.0, statistics.averageDuration, 0.01)
        assertEquals(0.0, statistics.averageProductivity, 0.01)
        assertEquals(0, statistics.averageBreaksCount)
        assertEquals(0, statistics.averageBreaksDuration)
        assertEquals(0, statistics.totalRecords)
        assertEquals("", statistics.startDate)
        assertEquals("", statistics.endDate)
        assertTrue(statistics.dailyData.isEmpty())
    }

    @Test
    fun `daily work data correctly stores all values`() {
        val data = DailyWorkData(
            date = "2024-06-15",
            durationHours = 8.5,
            productivity = 7.5,
            breaksCount = 3,
            breaksDurationMinutes = 45
        )

        assertEquals("2024-06-15", data.date)
        assertEquals(8.5, data.durationHours, 0.01)
        assertEquals(7.5, data.productivity, 0.01)
        assertEquals(3, data.breaksCount)
        assertEquals(45, data.breaksDurationMinutes)
    }

    @Test
    fun `productivity category enum values exist`() {
        assertEquals(4, ProductivityCategory.values().size)
        assertTrue(ProductivityCategory.values().contains(ProductivityCategory.LOW))
        assertTrue(ProductivityCategory.values().contains(ProductivityCategory.MODERATE))
        assertTrue(ProductivityCategory.values().contains(ProductivityCategory.GOOD))
        assertTrue(ProductivityCategory.values().contains(ProductivityCategory.EXCELLENT))
    }

    @Test
    fun `productivity boundary at level 3-4`() {
        assertEquals(ProductivityCategory.LOW, createWorkActivity(3).productivityCategory)
        assertEquals(ProductivityCategory.MODERATE, createWorkActivity(4).productivityCategory)
    }

    @Test
    fun `productivity boundary at level 5-6`() {
        assertEquals(ProductivityCategory.MODERATE, createWorkActivity(5).productivityCategory)
        assertEquals(ProductivityCategory.GOOD, createWorkActivity(6).productivityCategory)
    }

    @Test
    fun `productivity boundary at level 7-8`() {
        assertEquals(ProductivityCategory.GOOD, createWorkActivity(7).productivityCategory)
        assertEquals(ProductivityCategory.EXCELLENT, createWorkActivity(8).productivityCategory)
    }
}
