package com.pacificapp.burnout.data.models

import java.time.LocalDate
import java.time.LocalDateTime

data class User(
    val id: Long = 0,
    val username: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val dateJoined: LocalDateTime? = null,
    val lastLogin: LocalDateTime? = null,
    val dateOfBirth: LocalDate? = null,
    val avatarUrl: String = "",
    val stressLevelBase: Int = 50,
    val sleepHoursAvg: Double = 0.0,
    val workHoursDaily: Double = 0.0,
    val notificationsEnabled: Boolean = true,
    val notificationFrequency: Int = 60
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = ""
)

data class AuthResult(
    val user: User,
    val tokens: AuthTokens
)
