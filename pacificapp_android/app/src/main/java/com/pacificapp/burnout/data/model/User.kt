package com.pacificapp.burnout.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long = 0,
    val email: String = "",
    val username: String = "",
    @SerializedName("first_name")
    val firstName: String = "",
    @SerializedName("last_name")
    val lastName: String = "",
    @SerializedName("created_at")
    val createdAt: String? = null
) {
    val fullName: String
        get() = "$firstName $lastName".trim().ifEmpty { username }

    val initials: String
        get() = when {
            firstName.isNotEmpty() && lastName.isNotEmpty() ->
                "${firstName.first().uppercaseChar()}${lastName.first().uppercaseChar()}"
            username.isNotEmpty() -> username.take(2).uppercase()
            else -> "U"
        }
}

data class AuthTokens(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresIn: Long = 3600
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    @SerializedName("first_name")
    val firstName: String = "",
    @SerializedName("last_name")
    val lastName: String = ""
)

data class AuthResponse(
    val user: User,
    val tokens: AuthTokens
)
