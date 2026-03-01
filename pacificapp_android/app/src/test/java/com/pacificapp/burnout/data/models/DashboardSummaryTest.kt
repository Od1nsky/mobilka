package com.pacificapp.burnout.data.models

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class DashboardSummaryTest {

    @Test
    fun `dashboard summary correctly stores all values`() {
        val lastUpdated = LocalDateTime.of(2024, 6, 15, 12, 0)
        val summary = DashboardSummary(
            currentStressLevel = 55.0,
            averageSleepHours = 7.0,
            averageWorkHours = 9.0,
            burnoutRiskLevel = 45,
            activeRecommendations = 3,
            completedRecommendations = 5,
            lastUpdated = lastUpdated
        )

        assertEquals(55.0, summary.currentStressLevel, 0.01)
        assertEquals(7.0, summary.averageSleepHours, 0.01)
        assertEquals(9.0, summary.averageWorkHours, 0.01)
        assertEquals(45, summary.burnoutRiskLevel)
        assertEquals(3, summary.activeRecommendations)
        assertEquals(5, summary.completedRecommendations)
        assertEquals(lastUpdated, summary.lastUpdated)
    }

    @Test
    fun `dashboard summary with default values`() {
        val summary = DashboardSummary()

        assertEquals(0.0, summary.currentStressLevel, 0.01)
        assertEquals(0.0, summary.averageSleepHours, 0.01)
        assertEquals(0.0, summary.averageWorkHours, 0.01)
        assertEquals(0, summary.burnoutRiskLevel)
        assertEquals(0, summary.activeRecommendations)
        assertEquals(0, summary.completedRecommendations)
    }

    // Stress Category Tests
    @Test
    fun `stressCategory is LOW when currentStressLevel is 30 or lower`() {
        val summary = DashboardSummary(currentStressLevel = 30.0)
        assertEquals(StressCategory.LOW, summary.stressCategory)
    }

    @Test
    fun `stressCategory is MODERATE when currentStressLevel is between 31 and 60`() {
        val summary31 = DashboardSummary(currentStressLevel = 31.0)
        assertEquals(StressCategory.MODERATE, summary31.stressCategory)

        val summary60 = DashboardSummary(currentStressLevel = 60.0)
        assertEquals(StressCategory.MODERATE, summary60.stressCategory)
    }

    @Test
    fun `stressCategory is HIGH when currentStressLevel is between 61 and 80`() {
        val summary61 = DashboardSummary(currentStressLevel = 61.0)
        assertEquals(StressCategory.HIGH, summary61.stressCategory)

        val summary80 = DashboardSummary(currentStressLevel = 80.0)
        assertEquals(StressCategory.HIGH, summary80.stressCategory)
    }

    @Test
    fun `stressCategory is VERY_HIGH when currentStressLevel is above 80`() {
        val summary = DashboardSummary(currentStressLevel = 81.0)
        assertEquals(StressCategory.VERY_HIGH, summary.stressCategory)
    }

    // Burnout Risk Category Tests
    @Test
    fun `burnoutRiskCategory is LOW when burnoutRiskLevel is 25 or lower`() {
        val summary = DashboardSummary(burnoutRiskLevel = 25)
        assertEquals(BurnoutRiskCategory.LOW, summary.burnoutRiskCategory)
    }

    @Test
    fun `burnoutRiskCategory is MODERATE when burnoutRiskLevel is between 26 and 50`() {
        val summary26 = DashboardSummary(burnoutRiskLevel = 26)
        assertEquals(BurnoutRiskCategory.MODERATE, summary26.burnoutRiskCategory)

        val summary50 = DashboardSummary(burnoutRiskLevel = 50)
        assertEquals(BurnoutRiskCategory.MODERATE, summary50.burnoutRiskCategory)
    }

    @Test
    fun `burnoutRiskCategory is HIGH when burnoutRiskLevel is between 51 and 75`() {
        val summary51 = DashboardSummary(burnoutRiskLevel = 51)
        assertEquals(BurnoutRiskCategory.HIGH, summary51.burnoutRiskCategory)

        val summary75 = DashboardSummary(burnoutRiskLevel = 75)
        assertEquals(BurnoutRiskCategory.HIGH, summary75.burnoutRiskCategory)
    }

    @Test
    fun `burnoutRiskCategory is CRITICAL when burnoutRiskLevel is above 75`() {
        val summary = DashboardSummary(burnoutRiskLevel = 76)
        assertEquals(BurnoutRiskCategory.CRITICAL, summary.burnoutRiskCategory)
    }

    // Sleep Status Tests
    @Test
    fun `sleepStatus is OPTIMAL when averageSleepHours is 7_5 or higher`() {
        val summary = DashboardSummary(averageSleepHours = 7.5)
        assertEquals(SleepStatus.OPTIMAL, summary.sleepStatus)

        val summary8 = DashboardSummary(averageSleepHours = 8.0)
        assertEquals(SleepStatus.OPTIMAL, summary8.sleepStatus)
    }

    @Test
    fun `sleepStatus is ADEQUATE when averageSleepHours is between 6_5 and 7_5`() {
        val summary = DashboardSummary(averageSleepHours = 6.5)
        assertEquals(SleepStatus.ADEQUATE, summary.sleepStatus)

        val summary7 = DashboardSummary(averageSleepHours = 7.0)
        assertEquals(SleepStatus.ADEQUATE, summary7.sleepStatus)
    }

    @Test
    fun `sleepStatus is INSUFFICIENT when averageSleepHours is between 5_5 and 6_5`() {
        val summary = DashboardSummary(averageSleepHours = 5.5)
        assertEquals(SleepStatus.INSUFFICIENT, summary.sleepStatus)

        val summary6 = DashboardSummary(averageSleepHours = 6.0)
        assertEquals(SleepStatus.INSUFFICIENT, summary6.sleepStatus)
    }

    @Test
    fun `sleepStatus is CRITICAL when averageSleepHours is below 5_5`() {
        val summary = DashboardSummary(averageSleepHours = 5.0)
        assertEquals(SleepStatus.CRITICAL, summary.sleepStatus)
    }

    // Workload Status Tests
    @Test
    fun `workloadStatus is NORMAL when averageWorkHours is 8 or less`() {
        val summary = DashboardSummary(averageWorkHours = 8.0)
        assertEquals(WorkloadStatus.NORMAL, summary.workloadStatus)

        val summary7 = DashboardSummary(averageWorkHours = 7.0)
        assertEquals(WorkloadStatus.NORMAL, summary7.workloadStatus)
    }

    @Test
    fun `workloadStatus is MODERATE when averageWorkHours is between 8 and 9`() {
        val summary = DashboardSummary(averageWorkHours = 8.5)
        assertEquals(WorkloadStatus.MODERATE, summary.workloadStatus)

        val summary9 = DashboardSummary(averageWorkHours = 9.0)
        assertEquals(WorkloadStatus.MODERATE, summary9.workloadStatus)
    }

    @Test
    fun `workloadStatus is HIGH when averageWorkHours is between 9 and 10`() {
        val summary = DashboardSummary(averageWorkHours = 9.5)
        assertEquals(WorkloadStatus.HIGH, summary.workloadStatus)

        val summary10 = DashboardSummary(averageWorkHours = 10.0)
        assertEquals(WorkloadStatus.HIGH, summary10.workloadStatus)
    }

    @Test
    fun `workloadStatus is EXCESSIVE when averageWorkHours is above 10`() {
        val summary = DashboardSummary(averageWorkHours = 10.5)
        assertEquals(WorkloadStatus.EXCESSIVE, summary.workloadStatus)
    }

    // Enum Tests
    @Test
    fun `sleep status enum values exist`() {
        assertEquals(4, SleepStatus.values().size)
        assertTrue(SleepStatus.values().contains(SleepStatus.OPTIMAL))
        assertTrue(SleepStatus.values().contains(SleepStatus.ADEQUATE))
        assertTrue(SleepStatus.values().contains(SleepStatus.INSUFFICIENT))
        assertTrue(SleepStatus.values().contains(SleepStatus.CRITICAL))
    }

    @Test
    fun `workload status enum values exist`() {
        assertEquals(4, WorkloadStatus.values().size)
        assertTrue(WorkloadStatus.values().contains(WorkloadStatus.NORMAL))
        assertTrue(WorkloadStatus.values().contains(WorkloadStatus.MODERATE))
        assertTrue(WorkloadStatus.values().contains(WorkloadStatus.HIGH))
        assertTrue(WorkloadStatus.values().contains(WorkloadStatus.EXCESSIVE))
    }
}
