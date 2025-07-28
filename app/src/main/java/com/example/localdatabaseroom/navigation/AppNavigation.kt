package com.example.localdatabaseroom.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.localdatabaseroom.screens.HomeScreen
import com.example.localdatabaseroom.screens.UsersScreen
import com.example.localdatabaseroom.screens.CacheScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.HOME.route
    ) {
        composable(Screen.HOME.route) {
            HomeScreen(navController)
        }
        composable(Screen.USERS.route) {
            UsersScreen(navController)
        }
        composable(Screen.CACHE.route) {
            CacheScreen(navController)
        }
    }
}