package com.pacificapp.burnout.data.repository

import com.pacificapp.burnout.data.model.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DashboardRepository @Inject constructor() {

    suspend fun getDashboardSummary(): Result<DashboardSummary> {
        return try {
            delay(500)

            val weeklyTrend = (6 downTo 0).map { daysAgo ->
                DailyRiskData(
                    date = LocalDate.now().minusDays(daysAgo.toLong())
                        .format(DateTimeFormatter.ISO_DATE),
                    riskLevel = Random.nextInt(20, 60)
                )
            }

            Result.Success(
                DashboardSummary(
                    currentStressLevel = Random.nextDouble(30.0, 70.0),
                    averageSleepHours = Random.nextDouble(5.5, 8.5),
                    averageWorkHours = Random.nextDouble(7.0, 10.0),
                    burnoutRiskLevel = Random.nextInt(25, 65),
                    activeRecommendations = Random.nextInt(2, 6),
                    completedRecommendations = Random.nextInt(5, 15),
                    weeklyTrend = weeklyTrend
                )
            )
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки: ${e.message}", e)
        }
    }

    suspend fun getBurnoutRisk(): Result<BurnoutRisk> {
        return try {
            delay(300)

            Result.Success(
                BurnoutRisk(
                    id = 1,
                    userId = 1,
                    date = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                    riskLevel = Random.nextInt(25, 65),
                    factors = mapOf(
                        "overtime_factor" to listOf("low", "moderate", "high").random(),
                        "stress_factor" to listOf("low", "moderate", "high").random(),
                        "sleep_quality_factor" to listOf("low", "moderate", "high").random(),
                        "sleep_deprivation_factor" to listOf("low", "moderate", "high").random(),
                        "workday_duration_factor" to listOf("low", "moderate", "high").random()
                    ),
                    recommendations = listOf(
                        "Делайте перерывы каждые 2 часа",
                        "Старайтесь спать не менее 7 часов",
                        "Практикуйте техники расслабления"
                    )
                )
            )
        } catch (e: Exception) {
            Result.Error("Ошибка: ${e.message}", e)
        }
    }

    suspend fun calculateBurnoutRisk(): Result<BurnoutRisk> {
        delay(1000)
        return getBurnoutRisk()
    }
}
