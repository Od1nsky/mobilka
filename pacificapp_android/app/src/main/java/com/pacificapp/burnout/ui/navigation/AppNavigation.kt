package com.pacificapp.burnout.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pacificapp.burnout.ui.auth.LoginScreen
import com.pacificapp.burnout.ui.auth.RegisterScreen
import com.pacificapp.burnout.ui.home.HomeScreen
import com.pacificapp.burnout.ui.tracking.StressScreen
import com.pacificapp.burnout.ui.tracking.SleepScreen
import com.pacificapp.burnout.ui.tracking.WorkScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Stress : Screen("stress")
    object Sleep : Screen("sleep")
    object Work : Screen("work")
}

@Composable
fun AppNavigation(
    isAuthenticated: Boolean,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val startDestination = if (isAuthenticated) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    onLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToStress = {
                    navController.navigate(Screen.Stress.route)
                },
                onNavigateToSleep = {
                    navController.navigate(Screen.Sleep.route)
                },
                onNavigateToWork = {
                    navController.navigate(Screen.Work.route)
                }
            )
        }

        composable(Screen.Stress.route) {
            StressScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Sleep.route) {
            SleepScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Work.route) {
            WorkScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
