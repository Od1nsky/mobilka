package com.pacificapp.burnout.data.repository

import com.pacificapp.burnout.data.local.TokenManager
import com.pacificapp.burnout.data.model.AuthResponse
import com.pacificapp.burnout.data.model.AuthTokens
import com.pacificapp.burnout.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Throwable? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = (this as? Success)?.data
}

@Singleton
class AuthRepository @Inject constructor(
    private val tokenManager: TokenManager
) {
    val isLoggedIn: Flow<Boolean> = tokenManager.isLoggedIn

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            delay(1000)

            if (email.isNotEmpty() && password.length >= 6) {
                val user = User(
                    id = 1,
                    email = email,
                    username = email.substringBefore("@"),
                    firstName = "Пользователь",
                    lastName = ""
                )
                val tokens = AuthTokens(
                    accessToken = "mock_access_token_${System.currentTimeMillis()}",
                    refreshToken = "mock_refresh_token_${System.currentTimeMillis()}"
                )

                tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                tokenManager.saveUserInfo(user.id, user.email)

                Result.Success(AuthResponse(user, tokens))
            } else {
                Result.Error("Неверный email или пароль")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка входа: ${e.message}", e)
        }
    }

    suspend fun register(
        email: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<AuthResponse> {
        return try {
            delay(1000)

            if (email.isNotEmpty() && password.length >= 6 && username.isNotEmpty()) {
                val user = User(
                    id = 1,
                    email = email,
                    username = username,
                    firstName = firstName,
                    lastName = lastName
                )
                val tokens = AuthTokens(
                    accessToken = "mock_access_token_${System.currentTimeMillis()}",
                    refreshToken = "mock_refresh_token_${System.currentTimeMillis()}"
                )

                tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                tokenManager.saveUserInfo(user.id, user.email)

                Result.Success(AuthResponse(user, tokens))
            } else {
                Result.Error("Заполните все обязательные поля")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка регистрации: ${e.message}", e)
        }
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val userId = tokenManager.getUserId()
            if (userId != null) {
                Result.Success(
                    User(
                        id = userId,
                        email = "user@example.com",
                        username = "user",
                        firstName = "Пользователь",
                        lastName = ""
                    )
                )
            } else {
                Result.Error("Пользователь не авторизован")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка: ${e.message}", e)
        }
    }
}
