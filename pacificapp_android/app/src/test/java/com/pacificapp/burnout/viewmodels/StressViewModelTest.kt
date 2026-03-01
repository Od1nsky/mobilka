package com.pacificapp.burnout.viewmodels

import com.pacificapp.burnout.data.models.*
import com.pacificapp.burnout.data.repository.StressRepository
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
class StressViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var stressRepository: StressRepository

    private lateinit var viewModel: StressViewModel

    private val testStressLevel = StressLevel(
        id = 1L,
        userId = 1L,
        level = 7,
        physicalLevel = 6,
        emotionalLevel = 8,
        notes = "Stressful day",
        recordedAt = LocalDateTime.now()
    )

    private val testStressLevels = listOf(
        testStressLevel,
        testStressLevel.copy(id = 2L, level = 5, physicalLevel = 4, emotionalLevel = 6),
        testStressLevel.copy(id = 3L, level = 3, physicalLevel = 2, emotionalLevel = 4)
    )

    private val testStatistics = StressStatistics(
        totalEntries = 10,
        averageLevel = 5.5f,
        maxLevel = 8,
        minLevel = 2
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = StressViewModel(stressRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.stressLevels.isEmpty())
        assertNull(state.currentStressLevel)
        assertNull(state.statistics)
        assertNull(state.error)
    }

    @Test
    fun `loadStressLevels success updates state correctly`() = runTest {
        whenever(stressRepository.listStressLevels(any(), any()))
            .thenReturn(Result.Success(testStressLevels))

        viewModel.loadStressLevels()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(testStressLevels, state.stressLevels)
        assertNull(state.error)
    }

    @Test
    fun `loadStressLevels failure updates state with error`() = runTest {
        val errorMessage = "Failed to load stress levels"
        whenever(stressRepository.listStressLevels(any(), any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.loadStressLevels()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `createStressLevel success updates state and reloads list`() = runTest {
        whenever(stressRepository.createStressLevel(any(), any(), any(), any()))
            .thenReturn(Result.Success(testStressLevel))
        whenever(stressRepository.listStressLevels(any(), any()))
            .thenReturn(Result.Success(testStressLevels))

        viewModel.createStressLevel(7, 6, 8, "Stressful day")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(testStressLevel, state.currentStressLevel)
    }

    @Test
    fun `createStressLevel failure updates state with error`() = runTest {
        val errorMessage = "Failed to create stress level"
        whenever(stressRepository.createStressLevel(any(), any(), any(), any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.createStressLevel(7, 6, 8, "Notes")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `createStressLevel validates level bounds`() = runTest {
        viewModel.createStressLevel(11, 6, 8, "Notes") // level > 10
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.error)
    }

    @Test
    fun `createStressLevel validates level minimum`() = runTest {
        viewModel.createStressLevel(0, 6, 8, "Notes") // level < 1
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertNotNull(state.error)
    }

    @Test
    fun `deleteStressLevel success reloads list`() = runTest {
        whenever(stressRepository.deleteStressLevel(any()))
            .thenReturn(Result.Success(Unit))
        whenever(stressRepository.listStressLevels(any(), any()))
            .thenReturn(Result.Success(testStressLevels.drop(1)))

        viewModel.deleteStressLevel(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(stressRepository).deleteStressLevel(1L)
    }

    @Test
    fun `deleteStressLevel failure updates state with error`() = runTest {
        val errorMessage = "Failed to delete stress level"
        whenever(stressRepository.deleteStressLevel(any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.deleteStressLevel(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `loadStatistics success updates state`() = runTest {
        whenever(stressRepository.getStatistics(any()))
            .thenReturn(Result.Success(testStatistics))

        viewModel.loadStatistics()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(testStatistics, state.statistics)
    }

    @Test
    fun `loadStatistics failure updates state with error`() = runTest {
        val errorMessage = "Failed to load statistics"
        whenever(stressRepository.getStatistics(any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.loadStatistics()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `stress level category is correct for high stress`() {
        val highStress = testStressLevel.copy(level = 9)
        assertEquals(StressCategory.HIGH, highStress.category)
    }

    @Test
    fun `stress level category is correct for medium stress`() {
        val mediumStress = testStressLevel.copy(level = 6)
        assertEquals(StressCategory.MEDIUM, mediumStress.category)
    }

    @Test
    fun `stress level category is correct for low stress`() {
        val lowStress = testStressLevel.copy(level = 3)
        assertEquals(StressCategory.LOW, lowStress.category)
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        whenever(stressRepository.listStressLevels(any(), any()))
            .thenReturn(Result.Error("Some error"))

        viewModel.loadStressLevels()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        val state = viewModel.uiState.first()
        assertNull(state.error)
    }
}
