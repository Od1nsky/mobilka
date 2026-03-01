package com.pacificapp.burnout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacificapp.burnout.data.model.*
import com.pacificapp.burnout.data.repository.SleepRepository
import com.pacificapp.burnout.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SleepUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val sleepRecords: List<SleepRecord> = emptyList(),
    val statistics: SleepStatistics? = null,
    val error: String? = null
) {
    val lastNightSleep: SleepRecord?
        get() = sleepRecords.maxByOrNull { it.date }

    val averageSleepHours: Double
        get() = statistics?.averageDuration ?: 0.0
}

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val sleepRepository: SleepRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    init {
        loadSleepData()
    }

    fun loadSleepData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = sleepRepository.getSleepRecords()) {
                is Result.Success -> {
                    _uiState.update { it.copy(sleepRecords = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                is Result.Loading -> {}
            }

            when (val result = sleepRepository.getStatistics()) {
                is Result.Success -> {
                    _uiState.update { it.copy(statistics = result.data) }
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun createSleepRecord(
        date: LocalDate,
        durationHours: Double,
        quality: Int?,
        notes: String = ""
    ) {
        if (durationHours <= 0) {
            _uiState.update { it.copy(error = "Укажите продолжительность сна") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            when (val result = sleepRepository.createSleepRecord(dateStr, durationHours, quality, notes)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    loadSleepData()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun deleteSleepRecord(id: Long) {
        viewModelScope.launch {
            when (val result = sleepRepository.deleteSleepRecord(id)) {
                is Result.Success -> loadSleepData()
                is Result.Error -> _uiState.update { it.copy(error = result.message) }
                is Result.Loading -> {}
            }
        }
    }

    fun refresh() = loadSleepData()

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
