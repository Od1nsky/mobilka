package com.pacificapp.burnout.data.models

data class PaginationRequest(
    val page: Int = 1,
    val pageSize: Int = 20
)

data class PaginationInfo(
    val count: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalPages: Int = 0,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false
)

data class DateRange(
    val startDate: String = "",
    val endDate: String = ""
)

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int = 0) : Result<Nothing>()
    object Loading : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data
    fun getOrThrow(): T = (this as Success).data
}

data class PaginatedResult<T>(
    val items: List<T>,
    val pagination: PaginationInfo
)
