package com.pacificapp.burnout.viewmodels

import com.pacificapp.burnout.data.models.*
import com.pacificapp.burnout.data.repository.WorkRepository
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
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class WorkViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var workRepository: WorkRepository

    private lateinit var viewModel: WorkViewModel

    private val testWorkActivity = WorkActivity(
        id = 1L,
        userId = 1L,
        activityType = "coding",
        description = "Working on feature X",
        durationMinutes = 120,
        productivity = 8,
        notes = "Productive session",
        startTime = LocalDateTime.now().minusHours(2),
        endTime = LocalDateTime.now()
    )

    private val testWorkActivities = listOf(
        testWorkActivity,
        testWorkActivity.copy(id = 2L, activityType = "meeting", productivity = 5, durationMinutes = 60),
        testWorkActivity.copy(id = 3L, activityType = "review", productivity = 7, durationMinutes = 90)
    )

    private val testStatistics = WorkStatistics(
        totalActivities = 15,
        averageProductivity = 7.2f,
        totalDurationMinutes = 1800,
        averageDurationMinutes = 120
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = WorkViewModel(workRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.workActivities.isEmpty())
        assertNull(state.currentWorkActivity)
        assertNull(state.statistics)
        assertNull(state.error)
    }

    @Test
    fun `loadWorkActivities success updates state correctly`() = runTest {
        whenever(workRepository.listWorkActivities(any(), any()))
            .thenReturn(Result.Success(testWorkActivities))

        viewModel.loadWorkActivities()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(testWorkActivities, state.workActivities)
        assertNull(state.error)
    }

    @Test
    fun `loadWorkActivities failure updates state with error`() = runTest {
        val errorMessage = "Failed to load work activities"
        whenever(workRepository.listWorkActivities(any(), any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.loadWorkActivities()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `createWorkActivity success updates state and reloads list`() = runTest {
        val startTime = LocalDateTime.now().minusHours(2)
        val endTime = LocalDateTime.now()

        whenever(workRepository.createWorkActivity(any(), any(), any(), any(), any(), any()))
            .thenReturn(Result.Success(testWorkActivity))
        whenever(workRepository.listWorkActivities(any(), any()))
            .thenReturn(Result.Success(testWorkActivities))

        viewModel.createWorkActivity("coding", "Working on feature", startTime, endTime, 8, "Notes")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(testWorkActivity, state.currentWorkActivity)
    }

    @Test
    fun `createWorkActivity failure updates state with error`() = runTest {
        val errorMessage = "Failed to create work activity"
        val startTime = LocalDateTime.now().minusHours(2)
        val endTime = LocalDateTime.now()

        whenever(workRepository.createWorkActivity(any(), any(), any(), any(), any(), any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.createWorkActivity("coding", "Working", startTime, endTime, 8, "Notes")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `createWorkActivity validates productivity bounds`() = runTest {
        val startTime = LocalDateTime.now().minusHours(2)
        val endTime = LocalDateTime.now()

        viewModel.createWorkActivity("coding", "Working", startTime, endTime, 11, "Notes") // productivity > 10
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.error)
    }

    @Test
    fun `createWorkActivity validates productivity minimum`() = runTest {
        val startTime = LocalDateTime.now().minusHours(2)
        val endTime = LocalDateTime.now()

        viewModel.createWorkActivity("coding", "Working", startTime, endTime, 0, "Notes") // productivity < 1
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.error)
    }

    @Test
    fun `createWorkActivity validates end time after start time`() = runTest {
        val startTime = LocalDateTime.now()
        val endTime = LocalDateTime.now().minusHours(2) // end time before start time

        viewModel.createWorkActivity("coding", "Working", startTime, endTime, 8, "Notes")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.error)
    }

    @Test
    fun `createWorkActivity validates activity type not empty`() = runTest {
        val startTime = LocalDateTime.now().minusHours(2)
        val endTime = LocalDateTime.now()

        viewModel.createWorkActivity("", "Working", startTime, endTime, 8, "Notes")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.error)
    }

    @Test
    fun `deleteWorkActivity success reloads list`() = runTest {
        whenever(workRepository.deleteWorkActivity(any()))
            .thenReturn(Result.Success(Unit))
        whenever(workRepository.listWorkActivities(any(), any()))
            .thenReturn(Result.Success(testWorkActivities.drop(1)))

        viewModel.deleteWorkActivity(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(workRepository).deleteWorkActivity(1L)
    }

    @Test
    fun `deleteWorkActivity failure updates state with error`() = runTest {
        val errorMessage = "Failed to delete work activity"
        whenever(workRepository.deleteWorkActivity(any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.deleteWorkActivity(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `loadStatistics success updates state`() = runTest {
        whenever(workRepository.getStatistics(any()))
            .thenReturn(Result.Success(testStatistics))

        viewModel.loadStatistics()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(testStatistics, state.statistics)
    }

    @Test
    fun `loadStatistics failure updates state with error`() = runTest {
        val errorMessage = "Failed to load statistics"
        whenever(workRepository.getStatistics(any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.loadStatistics()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `work activity productivity category is correct for high`() {
        val highProductivity = testWorkActivity.copy(productivity = 9)
        assertEquals(ProductivityCategory.HIGH, highProductivity.productivityCategory)
    }

    @Test
    fun `work activity productivity category is correct for medium`() {
        val mediumProductivity = testWorkActivity.copy(productivity = 6)
        assertEquals(ProductivityCategory.MEDIUM, mediumProductivity.productivityCategory)
    }

    @Test
    fun `work activity productivity category is correct for low`() {
        val lowProductivity = testWorkActivity.copy(productivity = 3)
        assertEquals(ProductivityCategory.LOW, lowProductivity.productivityCategory)
    }

    @Test
    fun `work activity duration hours calculated correctly`() {
        val activity = testWorkActivity.copy(durationMinutes = 150) // 2.5 hours
        assertEquals(2.5f, activity.durationHours, 0.01f)
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        whenever(workRepository.listWorkActivities(any(), any()))
            .thenReturn(Result.Error("Some error"))

        viewModel.loadWorkActivities()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        val state = viewModel.uiState.first()
        assertNull(state.error)
    }
}
