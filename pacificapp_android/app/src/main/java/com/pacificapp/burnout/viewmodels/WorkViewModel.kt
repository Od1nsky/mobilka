package com.pacificapp.burnout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacificapp.burnout.data.model.*
import com.pacificapp.burnout.data.repository.WorkRepository
import com.pacificapp.burnout.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class WorkUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val workActivities: List<WorkActivity> = emptyList(),
    val statistics: WorkStatistics? = null,
    val error: String? = null
) {
    val todayActivity: WorkActivity?
        get() = workActivities.find { it.date == LocalDate.now().format(DateTimeFormatter.ISO_DATE) }

    val averageWorkHours: Double
        get() = statistics?.averageDuration ?: 0.0

    val isOverworking: Boolean
        get() = averageWorkHours > 9.0
}

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val workRepository: WorkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkUiState())
    val uiState: StateFlow<WorkUiState> = _uiState.asStateFlow()

    init {
        loadWorkData()
    }

    fun loadWorkData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = workRepository.getWorkActivities()) {
                is Result.Success -> {
                    _uiState.update { it.copy(workActivities = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                is Result.Loading -> {}
            }

            when (val result = workRepository.getStatistics()) {
                is Result.Success -> {
                    _uiState.update { it.copy(statistics = result.data) }
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun createWorkActivity(
        date: LocalDate,
        durationHours: Double,
        breaksCount: Int = 0,
        breaksTotalMinutes: Int = 0,
        productivity: Int?,
        notes: String = ""
    ) {
        if (durationHours <= 0) {
            _uiState.update { it.copy(error = "Укажите продолжительность") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            when (val result = workRepository.createWorkActivity(
                dateStr, durationHours, breaksCount, breaksTotalMinutes, productivity, notes
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    loadWorkData()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun deleteWorkActivity(id: Long) {
        viewModelScope.launch {
            when (val result = workRepository.deleteWorkActivity(id)) {
                is Result.Success -> loadWorkData()
                is Result.Error -> _uiState.update { it.copy(error = result.message) }
                is Result.Loading -> {}
            }
        }
    }

    fun refresh() = loadWorkData()

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
