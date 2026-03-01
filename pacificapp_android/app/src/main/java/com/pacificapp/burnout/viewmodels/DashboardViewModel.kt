package com.pacificapp.burnout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacificapp.burnout.data.model.*
import com.pacificapp.burnout.data.repository.DashboardRepository
import com.pacificapp.burnout.data.repository.RecommendationRepository
import com.pacificapp.burnout.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isCalculating: Boolean = false,
    val summary: DashboardSummary? = null,
    val burnoutRisk: BurnoutRisk? = null,
    val recommendations: List<UserRecommendation> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val recommendationRepository: RecommendationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load dashboard summary
            when (val result = dashboardRepository.getDashboardSummary()) {
                is Result.Success -> {
                    _uiState.update { it.copy(summary = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                is Result.Loading -> {}
            }

            // Load burnout risk
            when (val result = dashboardRepository.getBurnoutRisk()) {
                is Result.Success -> {
                    _uiState.update { it.copy(burnoutRisk = result.data) }
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }

            // Load recommendations
            when (val result = recommendationRepository.getActiveRecommendations()) {
                is Result.Success -> {
                    _uiState.update { it.copy(recommendations = result.data) }
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun calculateBurnoutRisk() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true) }

            when (val result = dashboardRepository.calculateBurnoutRisk()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(isCalculating = false, burnoutRisk = result.data)
                    }
                    loadDashboard()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isCalculating = false, error = result.message)
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun refresh() = loadDashboard()

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
