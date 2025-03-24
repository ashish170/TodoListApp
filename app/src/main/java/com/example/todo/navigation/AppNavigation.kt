package com.example.todo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todo.addtask_feature.presentation.AddTaskScreen
import com.example.todo.showtask_home.presentation.HomeScreen
import com.example.todo.showtask_home.presentation.SettingsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        startDestination = Routes.HOME_SCREEN, navController = navController
    ) {

        composable(Routes.HOME_SCREEN) {
            HomeScreen(navigateNext = { route ->
                navController.navigate(route)
            })
        }
        composable(Routes.ADD_TASK + "/{id}",
            arguments = listOf(navArgument("id") {
                this.type = NavType.IntType
                this.defaultValue = -1

            })) {
            AddTaskScreen(navigateBack = {
               navController.popBackStack()
            }, navController = navController )
        }

        composable(Routes.SETTINGS_SCREEN) {
            SettingsScreen(navigateNext = { route ->
                navController.navigate(route)
            }, navController = navController)
        }

    }
}