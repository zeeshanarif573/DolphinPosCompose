package com.retail.dolphinpos.presentation.features.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.retail.dolphinpos.common.utils.PreferenceManager
import com.retail.dolphinpos.presentation.features.ui.auth.cash_denomination.CashDenominationScreen
import com.retail.dolphinpos.presentation.features.ui.auth.login.LoginScreen
import com.retail.dolphinpos.presentation.features.ui.auth.pin_code.PinCodeScreen
import com.retail.dolphinpos.presentation.features.ui.auth.select_register.SelectRegisterScreen
import com.retail.dolphinpos.presentation.features.ui.auth.splash.SplashScreen

@Composable
fun AppNavigation(preferenceManager: PreferenceManager) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash Screen
        composable("splash") {
            SplashScreen(navController = navController, preferenceManager = preferenceManager)
        }

        // Login Screen
        composable("login") {
            LoginScreen(navController = navController)
        }

        // Select Register Screen
        composable("selectRegister") {
            SelectRegisterScreen(navController = navController)
        }

        // PIN Code Screen
        composable("pinCode") {
            PinCodeScreen(navController = navController)
        }

        // Cash Denomination Screen
        composable(
            route = "cashDenomination/{userId}/{storeId}/{registerId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("storeId") { type = NavType.IntType },
                navArgument("registerId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            CashDenominationScreen(navController = navController)
        }

        // Main Layout with Bottom Navigation (Home, Products, Orders, Reports, Setup)
        composable("home") {
            MainLayout(navController = navController)
        }

        // Products Screen (accessed through MainLayout)
        composable("products") {
            MainLayout(navController = navController)
        }

        // Orders Screen (accessed through MainLayout)
        composable("orders") {
            MainLayout(navController = navController)
        }

        // Inventory Screen (accessed through MainLayout)
        composable("inventory") {
            MainLayout(navController = navController)
        }

        // Reports Screen (accessed through MainLayout)
        composable("reports") {
            MainLayout(navController = navController)
        }

        // Hardware Setup Screen (accessed through MainLayout)
        composable("setup") {
            MainLayout(navController = navController)
        }
    }
}