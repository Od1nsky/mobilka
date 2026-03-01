package com.pacificapp.burnout.data.models

import org.junit.Assert.*
import org.junit.Test

class CommonTest {

    @Test
    fun `pagination request correctly stores all values`() {
        val request = PaginationRequest(
            page = 2,
            pageSize = 50
        )

        assertEquals(2, request.page)
        assertEquals(50, request.pageSize)
    }

    @Test
    fun `pagination request has correct defaults`() {
        val request = PaginationRequest()

        assertEquals(1, request.page)
        assertEquals(20, request.pageSize)
    }

    @Test
    fun `pagination info correctly stores all values`() {
        val info = PaginationInfo(
            count = 48,
            page = 2,
            pageSize = 10,
            totalPages = 5,
            hasNext = true,
            hasPrevious = true
        )

        assertEquals(48, info.count)
        assertEquals(2, info.page)
        assertEquals(10, info.pageSize)
        assertEquals(5, info.totalPages)
        assertTrue(info.hasNext)
        assertTrue(info.hasPrevious)
    }

    @Test
    fun `pagination info with defaults`() {
        val info = PaginationInfo()

        assertEquals(0, info.count)
        assertEquals(1, info.page)
        assertEquals(20, info.pageSize)
        assertEquals(0, info.totalPages)
        assertFalse(info.hasNext)
        assertFalse(info.hasPrevious)
    }

    @Test
    fun `pagination info hasNext is true when specified`() {
        val info = PaginationInfo(
            count = 100,
            page = 2,
            pageSize = 20,
            totalPages = 5,
            hasNext = true,
            hasPrevious = true
        )

        assertTrue(info.hasNext)
    }

    @Test
    fun `pagination info hasNext is false when on last page`() {
        val info = PaginationInfo(
            count = 100,
            page = 5,
            pageSize = 20,
            totalPages = 5,
            hasNext = false,
            hasPrevious = true
        )

        assertFalse(info.hasNext)
    }

    @Test
    fun `pagination info hasPrevious is true when specified`() {
        val info = PaginationInfo(
            count = 100,
            page = 2,
            pageSize = 20,
            totalPages = 5,
            hasNext = true,
            hasPrevious = true
        )

        assertTrue(info.hasPrevious)
    }

    @Test
    fun `pagination info hasPrevious is false on first page`() {
        val info = PaginationInfo(
            count = 100,
            page = 1,
            pageSize = 20,
            totalPages = 5,
            hasNext = true,
            hasPrevious = false
        )

        assertFalse(info.hasPrevious)
    }

    @Test
    fun `date range correctly stores all values`() {
        val range = DateRange(
            startDate = "2024-06-01",
            endDate = "2024-06-30"
        )

        assertEquals("2024-06-01", range.startDate)
        assertEquals("2024-06-30", range.endDate)
    }

    @Test
    fun `date range with defaults`() {
        val range = DateRange()

        assertEquals("", range.startDate)
        assertEquals("", range.endDate)
    }

    @Test
    fun `result success correctly wraps data`() {
        val data = "Test data"
        val result: Result<String> = Result.Success(data)

        assertTrue(result is Result.Success)
        assertEquals(data, (result as Result.Success).data)
    }

    @Test
    fun `result error correctly wraps message`() {
        val errorMessage = "Something went wrong"
        val result: Result<String> = Result.Error(errorMessage)

        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
    }

    @Test
    fun `result error with code correctly wraps both`() {
        val errorMessage = "Something went wrong"
        val code = 404
        val result: Result<String> = Result.Error(errorMessage, code)

        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        assertEquals(code, result.code)
    }

    @Test
    fun `result loading state`() {
        val result: Result<String> = Result.Loading

        assertTrue(result is Result.Loading)
        assertTrue(result.isLoading)
    }

    @Test
    fun `result getOrNull returns data on success`() {
        val result: Result<String> = Result.Success("data")
        assertEquals("data", result.getOrNull())
    }

    @Test
    fun `result getOrNull returns null on error`() {
        val result: Result<String> = Result.Error("Error")
        assertNull(result.getOrNull())
    }

    @Test
    fun `result getOrNull returns null on loading`() {
        val result: Result<String> = Result.Loading
        assertNull(result.getOrNull())
    }

    @Test
    fun `result getOrThrow returns data on success`() {
        val result: Result<String> = Result.Success("data")
        assertEquals("data", result.getOrThrow())
    }

    @Test
    fun `result isSuccess returns true for success`() {
        val result: Result<String> = Result.Success("data")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `result isSuccess returns false for error`() {
        val result: Result<String> = Result.Error("Error")
        assertFalse(result.isSuccess)
    }

    @Test
    fun `result isSuccess returns false for loading`() {
        val result: Result<String> = Result.Loading
        assertFalse(result.isSuccess)
    }

    @Test
    fun `result isError returns false for success`() {
        val result: Result<String> = Result.Success("data")
        assertFalse(result.isError)
    }

    @Test
    fun `result isError returns true for error`() {
        val result: Result<String> = Result.Error("Error")
        assertTrue(result.isError)
    }

    @Test
    fun `result isError returns false for loading`() {
        val result: Result<String> = Result.Loading
        assertFalse(result.isError)
    }

    @Test
    fun `result isLoading returns false for success`() {
        val result: Result<String> = Result.Success("data")
        assertFalse(result.isLoading)
    }

    @Test
    fun `result isLoading returns true for loading`() {
        val result: Result<String> = Result.Loading
        assertTrue(result.isLoading)
    }

    @Test
    fun `paginated result correctly stores values`() {
        val items = listOf("item1", "item2", "item3")
        val pagination = PaginationInfo(
            count = 3,
            page = 1,
            pageSize = 20,
            totalPages = 1,
            hasNext = false,
            hasPrevious = false
        )

        val result = PaginatedResult(
            items = items,
            pagination = pagination
        )

        assertEquals(items, result.items)
        assertEquals(pagination, result.pagination)
    }
}
