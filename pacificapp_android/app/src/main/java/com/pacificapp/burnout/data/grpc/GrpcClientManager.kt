package com.pacificapp.burnout.data.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrpcClientManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private var channel: ManagedChannel? = null

    companion object {
        private const val DEFAULT_HOST = "10.0.2.2" // Android emulator localhost
        private const val DEFAULT_PORT = 50051
    }

    fun getChannel(host: String = DEFAULT_HOST, port: Int = DEFAULT_PORT): ManagedChannel {
        if (channel == null || channel!!.isShutdown) {
            channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build()
        }
        return channel!!
    }

    suspend fun <T> withAuthHeader(block: suspend (Metadata) -> T): T {
        val metadata = Metadata()
        val token = tokenManager.getAccessToken()
        if (token != null) {
            val key = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)
            metadata.put(key, "bearer $token")
        }
        return block(metadata)
    }

    fun shutdown() {
        channel?.shutdown()
        channel = null
    }
}

interface TokenManager {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
    fun observeAuthState(): Flow<Boolean>
}
