package com.pacificapp.burnout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacificapp.burnout.data.model.*
import com.pacificapp.burnout.data.repository.StressRepository
import com.pacificapp.burnout.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StressUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val stressLevels: List<StressLevel> = emptyList(),
    val statistics: StressStatistics? = null,
    val error: String? = null
) {
    val currentStressLevel: StressLevel?
        get() = stressLevels.maxByOrNull { it.createdAt }
}

@HiltViewModel
class StressViewModel @Inject constructor(
    private val stressRepository: StressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StressUiState())
    val uiState: StateFlow<StressUiState> = _uiState.asStateFlow()

    init {
        loadStressData()
    }

    fun loadStressData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = stressRepository.getStressLevels()) {
                is Result.Success -> {
                    _uiState.update { it.copy(stressLevels = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                is Result.Loading -> {}
            }

            when (val result = stressRepository.getStatistics()) {
                is Result.Success -> {
                    _uiState.update { it.copy(statistics = result.data) }
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun createStressLevel(level: Int, notes: String = "") {
        if (level < 1 || level > 100) {
            _uiState.update { it.copy(error = "Уровень от 1 до 100") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            when (val result = stressRepository.createStressLevel(level, notes)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    loadStressData()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun deleteStressLevel(id: Long) {
        viewModelScope.launch {
            when (val result = stressRepository.deleteStressLevel(id)) {
                is Result.Success -> loadStressData()
                is Result.Error -> _uiState.update { it.copy(error = result.message) }
                is Result.Loading -> {}
            }
        }
    }

    fun refresh() = loadStressData()

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
