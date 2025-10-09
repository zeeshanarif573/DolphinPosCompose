package com.retail.dolphinpos.presentation.features.ui.auth

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.retail.dolphinpos.common.PreferenceManager
import com.retail.dolphinpos.presentation.features.ui.auth.login.LoginScreen
import com.retail.dolphinpos.presentation.features.ui.auth.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : FragmentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val systemUiController = rememberSystemUiController()

            SideEffect {
                systemUiController.isStatusBarVisible = false
            }

            Surface(color = MaterialTheme.colorScheme.background) {
                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(
                            navController = navController,
                            preferenceManager = preferenceManager
                        )
                    }
                    composable("login") {
                        LoginScreen()
                    }
                }
            }
        }
    }
}
