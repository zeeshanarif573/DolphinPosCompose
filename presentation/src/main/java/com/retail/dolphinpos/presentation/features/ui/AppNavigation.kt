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
import com.retail.dolphinpos.presentation.features.ui.home.HomeScreen

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

        // Home/Cart Screen (when you create it)
        composable("home") {
            HomeScreen(navController = navController)
        }
    }
}