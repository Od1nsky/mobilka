package com.pacificapp.burnout.data.models

import java.time.LocalDateTime

data class DashboardSummary(
    val currentStressLevel: Double = 0.0,
    val averageSleepHours: Double = 0.0,
    val averageWorkHours: Double = 0.0,
    val burnoutRiskLevel: Int = 0,
    val activeRecommendations: Int = 0,
    val completedRecommendations: Int = 0,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
) {
    val stressCategory: StressCategory
        get() = when {
            currentStressLevel <= 30 -> StressCategory.LOW
            currentStressLevel <= 60 -> StressCategory.MODERATE
            currentStressLevel <= 80 -> StressCategory.HIGH
            else -> StressCategory.VERY_HIGH
        }

    val burnoutRiskCategory: BurnoutRiskCategory
        get() = when {
            burnoutRiskLevel <= 25 -> BurnoutRiskCategory.LOW
            burnoutRiskLevel <= 50 -> BurnoutRiskCategory.MODERATE
            burnoutRiskLevel <= 75 -> BurnoutRiskCategory.HIGH
            else -> BurnoutRiskCategory.CRITICAL
        }

    val sleepStatus: SleepStatus
        get() = when {
            averageSleepHours >= 7.5 -> SleepStatus.OPTIMAL
            averageSleepHours >= 6.5 -> SleepStatus.ADEQUATE
            averageSleepHours >= 5.5 -> SleepStatus.INSUFFICIENT
            else -> SleepStatus.CRITICAL
        }

    val workloadStatus: WorkloadStatus
        get() = when {
            averageWorkHours <= 8.0 -> WorkloadStatus.NORMAL
            averageWorkHours <= 9.0 -> WorkloadStatus.MODERATE
            averageWorkHours <= 10.0 -> WorkloadStatus.HIGH
            else -> WorkloadStatus.EXCESSIVE
        }
}

enum class SleepStatus {
    OPTIMAL, ADEQUATE, INSUFFICIENT, CRITICAL
}

enum class WorkloadStatus {
    NORMAL, MODERATE, HIGH, EXCESSIVE
}
