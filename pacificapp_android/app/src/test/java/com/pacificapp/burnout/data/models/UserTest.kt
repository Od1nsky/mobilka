package com.pacificapp.burnout.data.models

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class UserTest {

    @Test
    fun `user correctly stores all values`() {
        val dateJoined = LocalDateTime.of(2024, 6, 15, 10, 0)
        val lastLogin = LocalDateTime.of(2024, 6, 15, 12, 0)
        val dateOfBirth = LocalDate.of(1990, 1, 15)

        val user = User(
            id = 42L,
            email = "test@example.com",
            username = "testuser",
            firstName = "Test",
            lastName = "User",
            dateJoined = dateJoined,
            lastLogin = lastLogin,
            dateOfBirth = dateOfBirth,
            avatarUrl = "https://example.com/avatar.jpg",
            stressLevelBase = 60,
            sleepHoursAvg = 7.5,
            workHoursDaily = 8.0,
            notificationsEnabled = true,
            notificationFrequency = 30
        )

        assertEquals(42L, user.id)
        assertEquals("test@example.com", user.email)
        assertEquals("testuser", user.username)
        assertEquals("Test", user.firstName)
        assertEquals("User", user.lastName)
        assertEquals(dateJoined, user.dateJoined)
        assertEquals(lastLogin, user.lastLogin)
        assertEquals(dateOfBirth, user.dateOfBirth)
        assertEquals("https://example.com/avatar.jpg", user.avatarUrl)
        assertEquals(60, user.stressLevelBase)
        assertEquals(7.5, user.sleepHoursAvg, 0.01)
        assertEquals(8.0, user.workHoursDaily, 0.01)
        assertTrue(user.notificationsEnabled)
        assertEquals(30, user.notificationFrequency)
    }

    @Test
    fun `user with default values`() {
        val user = User()

        assertEquals(0L, user.id)
        assertEquals("", user.email)
        assertEquals("", user.username)
        assertEquals("", user.firstName)
        assertEquals("", user.lastName)
        assertNull(user.dateJoined)
        assertNull(user.lastLogin)
        assertNull(user.dateOfBirth)
        assertEquals("", user.avatarUrl)
        assertEquals(50, user.stressLevelBase)
        assertEquals(0.0, user.sleepHoursAvg, 0.01)
        assertEquals(0.0, user.workHoursDaily, 0.01)
        assertTrue(user.notificationsEnabled)
        assertEquals(60, user.notificationFrequency)
    }

    @Test
    fun `user fullName is correct with both names`() {
        val user = User(
            id = 1L,
            email = "test@example.com",
            username = "testuser",
            firstName = "John",
            lastName = "Doe"
        )

        assertEquals("John Doe", user.fullName)
    }

    @Test
    fun `user fullName is trimmed when lastName is empty`() {
        val user = User(
            id = 1L,
            email = "test@example.com",
            username = "testuser",
            firstName = "John",
            lastName = ""
        )

        assertEquals("John", user.fullName)
    }

    @Test
    fun `user fullName is trimmed when firstName is empty`() {
        val user = User(
            id = 1L,
            email = "test@example.com",
            username = "testuser",
            firstName = "",
            lastName = "Doe"
        )

        assertEquals("Doe", user.fullName)
    }

    @Test
    fun `user fullName is empty when both names are empty`() {
        val user = User(
            id = 1L,
            email = "test@example.com",
            username = "testuser",
            firstName = "",
            lastName = ""
        )

        assertEquals("", user.fullName)
    }

    @Test
    fun `auth tokens correctly stores all values`() {
        val tokens = AuthTokens(
            accessToken = "access_token_123",
            refreshToken = "refresh_token_456"
        )

        assertEquals("access_token_123", tokens.accessToken)
        assertEquals("refresh_token_456", tokens.refreshToken)
    }

    @Test
    fun `login request correctly stores all values`() {
        val request = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )

        assertEquals("test@example.com", request.email)
        assertEquals("password123", request.password)
    }

    @Test
    fun `register request correctly stores all values`() {
        val request = RegisterRequest(
            email = "test@example.com",
            username = "testuser",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            dateOfBirth = "1990-01-15"
        )

        assertEquals("test@example.com", request.email)
        assertEquals("testuser", request.username)
        assertEquals("password123", request.password)
        assertEquals("Test", request.firstName)
        assertEquals("User", request.lastName)
        assertEquals("1990-01-15", request.dateOfBirth)
    }

    @Test
    fun `register request with default values`() {
        val request = RegisterRequest(
            email = "test@example.com",
            username = "testuser",
            password = "password123"
        )

        assertEquals("", request.firstName)
        assertEquals("", request.lastName)
        assertEquals("", request.dateOfBirth)
    }

    @Test
    fun `auth result correctly stores all values`() {
        val user = User(
            id = 1L,
            email = "test@example.com",
            username = "testuser"
        )
        val tokens = AuthTokens(
            accessToken = "access_token",
            refreshToken = "refresh_token"
        )

        val result = AuthResult(
            user = user,
            tokens = tokens
        )

        assertEquals(user, result.user)
        assertEquals(tokens, result.tokens)
    }

    @Test
    fun `user equality works correctly`() {
        val user1 = User(
            id = 1L,
            email = "test@example.com",
            username = "testuser"
        )
        val user2 = User(
            id = 1L,
            email = "test@example.com",
            username = "testuser"
        )
        val user3 = User(
            id = 2L,
            email = "other@example.com",
            username = "otheruser"
        )

        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
    }
}
