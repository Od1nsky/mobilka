package com.pacificapp.burnout.viewmodels

import com.pacificapp.burnout.data.models.AuthResult
import com.pacificapp.burnout.data.models.AuthTokens
import com.pacificapp.burnout.data.models.User
import com.pacificapp.burnout.data.repository.AuthRepository
import com.pacificapp.burnout.data.models.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: AuthViewModel

    private val testUser = User(
        id = 1L,
        email = "test@example.com",
        username = "testuser",
        firstName = "Test",
        lastName = "User"
    )

    private val testTokens = AuthTokens(
        accessToken = "test_access_token",
        refreshToken = "test_refresh_token",
        expiresIn = 3600
    )

    private val testAuthResult = AuthResult(
        user = testUser,
        tokens = testTokens
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.isLoggedIn)
        assertNull(state.user)
        assertNull(state.error)
    }

    @Test
    fun `login success updates state correctly`() = runTest {
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.Success(testAuthResult))

        viewModel.login("test@example.com", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.isLoggedIn)
        assertEquals(testUser, state.user)
        assertNull(state.error)
    }

    @Test
    fun `login failure updates state with error`() = runTest {
        val errorMessage = "Invalid credentials"
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.login("test@example.com", "wrongpassword")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.isLoggedIn)
        assertNull(state.user)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `login with empty email shows validation error`() = runTest {
        viewModel.login("", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals("Email is required", state.error)
    }

    @Test
    fun `login with empty password shows validation error`() = runTest {
        viewModel.login("test@example.com", "")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals("Password is required", state.error)
    }

    @Test
    fun `register success updates state correctly`() = runTest {
        whenever(authRepository.register(any(), any(), any(), any(), any()))
            .thenReturn(Result.Success(testAuthResult))

        viewModel.register("test@example.com", "testuser", "password123", "Test", "User")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.isLoggedIn)
        assertEquals(testUser, state.user)
        assertNull(state.error)
    }

    @Test
    fun `register failure updates state with error`() = runTest {
        val errorMessage = "Email already exists"
        whenever(authRepository.register(any(), any(), any(), any(), any()))
            .thenReturn(Result.Error(errorMessage))

        viewModel.register("test@example.com", "testuser", "password123", "Test", "User")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.isLoggedIn)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `register with empty fields shows validation error`() = runTest {
        viewModel.register("", "", "", "", "")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `logout clears user state`() = runTest {
        // First login
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.Success(testAuthResult))
        viewModel.login("test@example.com", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then logout
        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoggedIn)
        assertNull(state.user)
        verify(authRepository).logout()
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        // Create error state
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.Error("Some error"))
        viewModel.login("test@example.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // Clear error
        viewModel.clearError()

        val state = viewModel.uiState.first()
        assertNull(state.error)
    }

    @Test
    fun `checkAuthState with valid token sets logged in state`() = runTest {
        whenever(authRepository.getCurrentUser())
            .thenReturn(Result.Success(testUser))

        viewModel.checkAuthState()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.isLoggedIn)
        assertEquals(testUser, state.user)
    }

    @Test
    fun `checkAuthState with no token keeps logged out state`() = runTest {
        whenever(authRepository.getCurrentUser())
            .thenReturn(Result.Error("Not authenticated"))

        viewModel.checkAuthState()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoggedIn)
        assertNull(state.user)
    }
}
