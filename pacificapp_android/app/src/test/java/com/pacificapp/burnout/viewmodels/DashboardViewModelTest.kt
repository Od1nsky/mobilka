package com.pacificapp.burnout.viewmodels

import com.pacificapp.burnout.data.models.*
import com.pacificapp.burnout.data.repository.DashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var dashboardRepository: DashboardRepository

    private lateinit var viewModel: DashboardViewModel

    private val testFactorData = FactorData(
        overtimeHours = 5.0f,
        avgWorkdayLength = 9.0f,
        avgStressLevel = 6.0f,
        avgSleepQuality = 3.0f,
        avgSleepHours = 6.5f
    )

    private val testWeeklyData = listOf(
        WeeklyData(
            date = LocalDate.now().minusDays(6),
            riskLevel = 30,
            stressLevel = 5.0f,
            sleepHours = 7.0f,
            workHours = 8.0f
        ),
        WeeklyData(
            date = LocalDate.now().minusDays(5),
            riskLevel = 35,
            stressLevel = 6.0f,
            sleepHours = 6.5f,
            workHours = 9.0f
        ),
        WeeklyData(
            date = LocalDate.now().minusDays(4),
            riskLevel = 40,
            stressLevel = 7.0f,
            sleepHours = 6.0f,
            workHours = 10.0f
        )
    )

    private val testBurnoutRisk = BurnoutRisk(
        id = 1L,
        userId = 1L,
        riskLevel = 45,
        overtimeScore = 60,
        workdayScore = 50,
        stressScore = 70,
        sleepQualityScore = 40,
        sleepDeprivationScore = 30,
        factorData = testFactorData,
        weeklyData = testWeeklyData,
        recommendations = listOf("Take a break", "Improve sleep schedule"),
        calculatedAt = LocalDateTime.now()
    )

    private val testStressStatistics = StressStatistics(
        totalEntries = 10,
        averageLevel = 5.5f,
        maxLevel = 8,
        minLevel = 3
    )

    private val testSleepStatistics = SleepStatistics(
        totalRecords = 7,
        averageQuality = 3.5f,
        averageDurationHours = 6.8f,
        averageDurationMinutes = 408
    )

    private val testWorkStatistics = WorkStatistics(
        totalActivities = 5,
        averageProductivity = 7.0f,
        totalDurationMinutes = 2400,
        averageDurationMinutes = 480
    )

    private val testDashboardSummary = DashboardSummary(
        burnoutRisk = testBurnoutRisk,
        stressStatistics = testStressStatistics,
        sleepStatistics = testSleepStatistics,
        workStatistics = testWorkStatistics,
        recentRecommendations = listOf(
            Recommendation(
                id = 1L,
                title = "Take a break",
                description = "Regular breaks help reduce stress",
                category = "stress",
                priority = 1
            )
        )
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel(dashboardRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.first()
        assertTrue(state.isLoading)
        assertNull(state.dashboardSummary)
        assertNull(state.error)
    }

    @Test
    fun `loadDashboard success updates state correctly`() = runTest {
        whenever(dashboardRepository.getDashboardSummary())
            .thenReturn(Result.Success(testDashboardSummary))

        viewModel.loadDashboard()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertNotNull(state.dashboardSummary)
        assertEquals(testDashboardSummary, state.dashboardSummary)
        assertNull(state.error)
    }

    @Test
    fun `loadDashboard failure updates state with error`() = runTest {
        val errorMessage = "Network error"
        whenever(dashboardRepository.getDashboardSummary())
            .thenReturn(Result.Error(errorMessage))

        viewModel.loadDashboard()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertNull(state.dashboardSummary)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `refresh calls loadDashboard`() = runTest {
        whenever(dashboardRepository.getDashboardSummary())
            .thenReturn(Result.Success(testDashboardSummary))

        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.dashboardSummary)
    }

    @Test
    fun `dashboard summary burnout risk category is correct for high risk`() = runTest {
        val highRiskBurnout = testBurnoutRisk.copy(riskLevel = 75)
        val highRiskSummary = testDashboardSummary.copy(burnoutRisk = highRiskBurnout)

        whenever(dashboardRepository.getDashboardSummary())
            .thenReturn(Result.Success(highRiskSummary))

        viewModel.loadDashboard()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(BurnoutRiskCategory.HIGH, state.dashboardSummary?.burnoutRisk?.category)
    }

    @Test
    fun `dashboard summary burnout risk category is correct for medium risk`() = runTest {
        val mediumRiskBurnout = testBurnoutRisk.copy(riskLevel = 50)
        val mediumRiskSummary = testDashboardSummary.copy(burnoutRisk = mediumRiskBurnout)

        whenever(dashboardRepository.getDashboardSummary())
            .thenReturn(Result.Success(mediumRiskSummary))

        viewModel.loadDashboard()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(BurnoutRiskCategory.MEDIUM, state.dashboardSummary?.burnoutRisk?.category)
    }

    @Test
    fun `dashboard summary burnout risk category is correct for low risk`() = runTest {
        val lowRiskBurnout = testBurnoutRisk.copy(riskLevel = 25)
        val lowRiskSummary = testDashboardSummary.copy(burnoutRisk = lowRiskBurnout)

        whenever(dashboardRepository.getDashboardSummary())
            .thenReturn(Result.Success(lowRiskSummary))

        viewModel.loadDashboard()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(BurnoutRiskCategory.LOW, state.dashboardSummary?.burnoutRisk?.category)
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        whenever(dashboardRepository.getDashboardSummary())
            .thenReturn(Result.Error("Some error"))

        viewModel.loadDashboard()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        val state = viewModel.uiState.first()
        assertNull(state.error)
    }
}
