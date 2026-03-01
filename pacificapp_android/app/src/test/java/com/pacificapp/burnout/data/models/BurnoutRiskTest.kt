package com.pacificapp.burnout.data.models

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class BurnoutRiskTest {

    private fun createBurnoutRisk(riskLevel: Int): BurnoutRisk {
        return BurnoutRisk(
            id = 1L,
            userId = 1L,
            date = LocalDate.now(),
            riskLevel = riskLevel,
            factors = mapOf(
                "overtime_factor" to "high",
                "stress_factor" to "moderate",
                "sleep_quality_factor" to "low",
                "sleep_deprivation_factor" to "high",
                "workday_duration_factor" to "moderate"
            ),
            recommendations = listOf("Take a break", "Improve sleep"),
            createdAt = LocalDateTime.now()
        )
    }

    @Test
    fun `riskCategory is LOW when riskLevel is 25 or lower`() {
        assertEquals(BurnoutRiskCategory.LOW, createBurnoutRisk(10).riskCategory)
        assertEquals(BurnoutRiskCategory.LOW, createBurnoutRisk(20).riskCategory)
        assertEquals(BurnoutRiskCategory.LOW, createBurnoutRisk(25).riskCategory)
    }

    @Test
    fun `riskCategory is MODERATE when riskLevel is between 26 and 50`() {
        assertEquals(BurnoutRiskCategory.MODERATE, createBurnoutRisk(26).riskCategory)
        assertEquals(BurnoutRiskCategory.MODERATE, createBurnoutRisk(40).riskCategory)
        assertEquals(BurnoutRiskCategory.MODERATE, createBurnoutRisk(50).riskCategory)
    }

    @Test
    fun `riskCategory is HIGH when riskLevel is between 51 and 75`() {
        assertEquals(BurnoutRiskCategory.HIGH, createBurnoutRisk(51).riskCategory)
        assertEquals(BurnoutRiskCategory.HIGH, createBurnoutRisk(60).riskCategory)
        assertEquals(BurnoutRiskCategory.HIGH, createBurnoutRisk(75).riskCategory)
    }

    @Test
    fun `riskCategory is CRITICAL when riskLevel is above 75`() {
        assertEquals(BurnoutRiskCategory.CRITICAL, createBurnoutRisk(76).riskCategory)
        assertEquals(BurnoutRiskCategory.CRITICAL, createBurnoutRisk(90).riskCategory)
        assertEquals(BurnoutRiskCategory.CRITICAL, createBurnoutRisk(100).riskCategory)
    }

    @Test
    fun `burnout risk correctly stores all values`() {
        val date = LocalDate.of(2024, 6, 15)
        val createdAt = LocalDateTime.of(2024, 6, 15, 12, 0)
        val factors = mapOf(
            "overtime_factor" to "high",
            "stress_factor" to "moderate"
        )
        val recommendations = listOf("Take a break", "Improve sleep")

        val risk = BurnoutRisk(
            id = 42L,
            userId = 5L,
            date = date,
            riskLevel = 65,
            factors = factors,
            recommendations = recommendations,
            createdAt = createdAt
        )

        assertEquals(42L, risk.id)
        assertEquals(5L, risk.userId)
        assertEquals(date, risk.date)
        assertEquals(65, risk.riskLevel)
        assertEquals(factors, risk.factors)
        assertEquals(recommendations, risk.recommendations)
        assertEquals(createdAt, risk.createdAt)
    }

    @Test
    fun `burnout risk with default values`() {
        val risk = BurnoutRisk()

        assertEquals(0L, risk.id)
        assertEquals(0L, risk.userId)
        assertEquals(0, risk.riskLevel)
        assertTrue(risk.factors.isEmpty())
        assertTrue(risk.recommendations.isEmpty())
    }

    @Test
    fun `overtimeFactor returns correct value from factors map`() {
        val risk = createBurnoutRisk(50)
        assertEquals("high", risk.overtimeFactor)
    }

    @Test
    fun `overtimeFactor returns unknown when not in map`() {
        val risk = BurnoutRisk(riskLevel = 50, factors = emptyMap())
        assertEquals("unknown", risk.overtimeFactor)
    }

    @Test
    fun `stressFactor returns correct value from factors map`() {
        val risk = createBurnoutRisk(50)
        assertEquals("moderate", risk.stressFactor)
    }

    @Test
    fun `stressFactor returns unknown when not in map`() {
        val risk = BurnoutRisk(riskLevel = 50, factors = emptyMap())
        assertEquals("unknown", risk.stressFactor)
    }

    @Test
    fun `sleepQualityFactor returns correct value from factors map`() {
        val risk = createBurnoutRisk(50)
        assertEquals("low", risk.sleepQualityFactor)
    }

    @Test
    fun `sleepQualityFactor returns unknown when not in map`() {
        val risk = BurnoutRisk(riskLevel = 50, factors = emptyMap())
        assertEquals("unknown", risk.sleepQualityFactor)
    }

    @Test
    fun `sleepDeprivationFactor returns correct value from factors map`() {
        val risk = createBurnoutRisk(50)
        assertEquals("high", risk.sleepDeprivationFactor)
    }

    @Test
    fun `sleepDeprivationFactor returns unknown when not in map`() {
        val risk = BurnoutRisk(riskLevel = 50, factors = emptyMap())
        assertEquals("unknown", risk.sleepDeprivationFactor)
    }

    @Test
    fun `workdayDurationFactor returns correct value from factors map`() {
        val risk = createBurnoutRisk(50)
        assertEquals("moderate", risk.workdayDurationFactor)
    }

    @Test
    fun `workdayDurationFactor returns unknown when not in map`() {
        val risk = BurnoutRisk(riskLevel = 50, factors = emptyMap())
        assertEquals("unknown", risk.workdayDurationFactor)
    }

    @Test
    fun `burnout risk with empty recommendations`() {
        val risk = createBurnoutRisk(50).copy(recommendations = emptyList())
        assertTrue(risk.recommendations.isEmpty())
    }

    @Test
    fun `burnout risk with multiple recommendations`() {
        val recommendations = listOf(
            "Take regular breaks",
            "Improve sleep schedule",
            "Exercise more"
        )
        val risk = createBurnoutRisk(50).copy(recommendations = recommendations)
        assertEquals(3, risk.recommendations.size)
        assertEquals("Take regular breaks", risk.recommendations[0])
    }

    @Test
    fun `burnout risk category enum values exist`() {
        assertEquals(4, BurnoutRiskCategory.values().size)
        assertTrue(BurnoutRiskCategory.values().contains(BurnoutRiskCategory.LOW))
        assertTrue(BurnoutRiskCategory.values().contains(BurnoutRiskCategory.MODERATE))
        assertTrue(BurnoutRiskCategory.values().contains(BurnoutRiskCategory.HIGH))
        assertTrue(BurnoutRiskCategory.values().contains(BurnoutRiskCategory.CRITICAL))
    }

    @Test
    fun `risk level boundary at 25-26`() {
        assertEquals(BurnoutRiskCategory.LOW, createBurnoutRisk(25).riskCategory)
        assertEquals(BurnoutRiskCategory.MODERATE, createBurnoutRisk(26).riskCategory)
    }

    @Test
    fun `risk level boundary at 50-51`() {
        assertEquals(BurnoutRiskCategory.MODERATE, createBurnoutRisk(50).riskCategory)
        assertEquals(BurnoutRiskCategory.HIGH, createBurnoutRisk(51).riskCategory)
    }

    @Test
    fun `risk level boundary at 75-76`() {
        assertEquals(BurnoutRiskCategory.HIGH, createBurnoutRisk(75).riskCategory)
        assertEquals(BurnoutRiskCategory.CRITICAL, createBurnoutRisk(76).riskCategory)
    }

    // FactorData Tests
    @Test
    fun `factor data correctly stores all values`() {
        val factorData = FactorData(
            overtimeFactor = "high",
            workdayDurationFactor = "moderate",
            stressFactor = "low",
            sleepQualityFactor = "high",
            sleepDeprivationFactor = "moderate",
            factorsData = mapOf("custom" to "value"),
            recommendations = listOf("Recommendation 1")
        )

        assertEquals("high", factorData.overtimeFactor)
        assertEquals("moderate", factorData.workdayDurationFactor)
        assertEquals("low", factorData.stressFactor)
        assertEquals("high", factorData.sleepQualityFactor)
        assertEquals("moderate", factorData.sleepDeprivationFactor)
        assertEquals(1, factorData.factorsData.size)
        assertEquals(1, factorData.recommendations.size)
    }

    @Test
    fun `factor data with default values`() {
        val factorData = FactorData()

        assertEquals("", factorData.overtimeFactor)
        assertEquals("", factorData.workdayDurationFactor)
        assertEquals("", factorData.stressFactor)
        assertEquals("", factorData.sleepQualityFactor)
        assertEquals("", factorData.sleepDeprivationFactor)
        assertTrue(factorData.factorsData.isEmpty())
        assertTrue(factorData.recommendations.isEmpty())
    }

    // WeeklyData Tests
    @Test
    fun `weekly data correctly stores daily data`() {
        val dailyData = listOf(
            DailyRiskData(date = "2024-06-15", riskLevel = 45),
            DailyRiskData(date = "2024-06-14", riskLevel = 40)
        )
        val weeklyData = WeeklyData(dailyData = dailyData)

        assertEquals(2, weeklyData.dailyData.size)
    }

    @Test
    fun `weekly data with default values`() {
        val weeklyData = WeeklyData()
        assertTrue(weeklyData.dailyData.isEmpty())
    }

    // DailyRiskData Tests
    @Test
    fun `daily risk data correctly stores all values`() {
        val data = DailyRiskData(
            date = "2024-06-15",
            riskLevel = 55
        )

        assertEquals("2024-06-15", data.date)
        assertEquals(55, data.riskLevel)
    }
}
