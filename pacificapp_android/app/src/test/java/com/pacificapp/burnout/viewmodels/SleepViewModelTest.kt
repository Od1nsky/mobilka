package com.pacificapp.burnout.viewmodels

import com.pacificapp.burnout.data.models.*
import com.pacificapp.burnout.data.repository.SleepRepository
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
class SleepViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var sleepRepository: SleepRepository

    private lateinit var viewModel: SleepViewModel

    private val testSleepRecord = SleepRecord(
        id = 1L,
        userId = 1L,
        quality = 4,
        durationMinutes = 420,
        bedTime = LocalDateTime.now().minusHours(8),
        wakeTime = LocalDateTime.now(),
        notes = "Good sleep"
    )

    private val testSleepRecords = listOf(
        testSleepRecord,
        testSleepRecord.copy(id = 2L, quality = 3, durationMinutes = 360),
        testSleepRecord.copy(id = 3L, quality = 5, durationMinutes = 480)
    )

    private val testStatistics = SleepStatistics(
        totalRecords = 10,
        averageQuality = 3.8f,
        averageDurationHours = 7.2f,
        averageDurationMinutes = 432
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = SleepViewModel(sleepRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.sleepRecords.isEmpty())
        assertNull(state.currentSleepRecord)
        assertNull(state.statistics)
        assertNull(state.error)
    }

    @Test
    fun `loadSleepRecords success updates state correctly`() = runTest {
        whenever(sleepRepository.listSleepRecords(any(), any()))
            .thenReturn(Result.Success(testSleepRecords))

        viewModel.loadSleepRecords()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(testSleepRecords, state.sleepRecords)
        assertNull(state.error)
    }

    @Test
    fun `loadSleepRecords failure updates state with error`() = runTest {
        val errorMessage = "Failed to load sleep records"
        whenever(sleepRepository.listSleepRecords(any(), any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.loadSleepRecords()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `createSleepRecord success updates state and reloads list`() = runTest {
        val bedTime = LocalDateTime.now().minusHours(8)
        val wakeTime = LocalDateTime.now()

        whenever(sleepRepository.createSleepRecord(any(), any(), any(), any()))
            .thenReturn(Result.Success(testSleepRecord))
        whenever(sleepRepository.listSleepRecords(any(), any()))
            .thenReturn(Result.Success(testSleepRecords))

        viewModel.createSleepRecord(4, bedTime, wakeTime, "Good sleep")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(testSleepRecord, state.currentSleepRecord)
    }

    @Test
    fun `createSleepRecord failure updates state with error`() = runTest {
        val errorMessage = "Failed to create sleep record"
        val bedTime = LocalDateTime.now().minusHours(8)
        val wakeTime = LocalDateTime.now()

        whenever(sleepRepository.createSleepRecord(any(), any(), any(), any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.createSleepRecord(4, bedTime, wakeTime, "Notes")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `createSleepRecord validates quality bounds`() = runTest {
        val bedTime = LocalDateTime.now().minusHours(8)
        val wakeTime = LocalDateTime.now()

        viewModel.createSleepRecord(6, bedTime, wakeTime, "Notes") // quality > 5
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.error)
    }

    @Test
    fun `createSleepRecord validates quality minimum`() = runTest {
        val bedTime = LocalDateTime.now().minusHours(8)
        val wakeTime = LocalDateTime.now()

        viewModel.createSleepRecord(0, bedTime, wakeTime, "Notes") // quality < 1
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.error)
    }

    @Test
    fun `createSleepRecord validates wake time after bed time`() = runTest {
        val bedTime = LocalDateTime.now()
        val wakeTime = LocalDateTime.now().minusHours(8) // wake time before bed time

        viewModel.createSleepRecord(4, bedTime, wakeTime, "Notes")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.error)
    }

    @Test
    fun `deleteSleepRecord success reloads list`() = runTest {
        whenever(sleepRepository.deleteSleepRecord(any()))
            .thenReturn(Result.Success(Unit))
        whenever(sleepRepository.listSleepRecords(any(), any()))
            .thenReturn(Result.Success(testSleepRecords.drop(1)))

        viewModel.deleteSleepRecord(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(sleepRepository).deleteSleepRecord(1L)
    }

    @Test
    fun `deleteSleepRecord failure updates state with error`() = runTest {
        val errorMessage = "Failed to delete sleep record"
        whenever(sleepRepository.deleteSleepRecord(any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.deleteSleepRecord(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `loadStatistics success updates state`() = runTest {
        whenever(sleepRepository.getStatistics(any()))
            .thenReturn(Result.Success(testStatistics))

        viewModel.loadStatistics()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(testStatistics, state.statistics)
    }

    @Test
    fun `loadStatistics failure updates state with error`() = runTest {
        val errorMessage = "Failed to load statistics"
        whenever(sleepRepository.getStatistics(any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.loadStatistics()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `sleep record quality category is correct for excellent`() {
        val excellentSleep = testSleepRecord.copy(quality = 5)
        assertEquals(SleepQualityCategory.EXCELLENT, excellentSleep.qualityCategory)
    }

    @Test
    fun `sleep record quality category is correct for good`() {
        val goodSleep = testSleepRecord.copy(quality = 4)
        assertEquals(SleepQualityCategory.GOOD, goodSleep.qualityCategory)
    }

    @Test
    fun `sleep record quality category is correct for fair`() {
        val fairSleep = testSleepRecord.copy(quality = 3)
        assertEquals(SleepQualityCategory.FAIR, fairSleep.qualityCategory)
    }

    @Test
    fun `sleep record quality category is correct for poor`() {
        val poorSleep = testSleepRecord.copy(quality = 2)
        assertEquals(SleepQualityCategory.POOR, poorSleep.qualityCategory)
    }

    @Test
    fun `sleep record quality category is correct for very poor`() {
        val veryPoorSleep = testSleepRecord.copy(quality = 1)
        assertEquals(SleepQualityCategory.VERY_POOR, veryPoorSleep.qualityCategory)
    }

    @Test
    fun `sleep record duration hours calculated correctly`() {
        val record = testSleepRecord.copy(durationMinutes = 450) // 7.5 hours
        assertEquals(7.5f, record.durationHours, 0.01f)
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        whenever(sleepRepository.listSleepRecords(any(), any()))
            .thenReturn(Result.Error("Some error"))

        viewModel.loadSleepRecords()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        val state = viewModel.uiState.first()
        assertNull(state.error)
    }
}
