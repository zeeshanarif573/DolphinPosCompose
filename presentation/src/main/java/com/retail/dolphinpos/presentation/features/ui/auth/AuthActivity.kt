package com.retail.dolphinpos.presentation.features.ui.auth

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.retail.dolphinpos.common.utils.PreferenceManager
import com.retail.dolphinpos.presentation.features.ui.auth.login.LoginScreen
import com.retail.dolphinpos.presentation.features.ui.auth.pin_code.PinCodeScreen
import com.retail.dolphinpos.presentation.features.ui.auth.select_register.SelectRegisterScreen
import com.retail.dolphinpos.presentation.features.ui.auth.splash.SplashScreen
import com.retail.dolphinpos.presentation.util.Loader
import com.retail.dolphinpos.presentation.util.ErrorDialogHandler.GlobalErrorDialogHost
import com.retail.dolphinpos.presentation.util.Utils.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : FragmentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlobalLoaderHandler()
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
                        LoginScreen(navController = navController)
                    }

                    composable("selectRegister") {
                        SelectRegisterScreen(navController = navController)
                    }

                    composable("pinCode") {
                        PinCodeScreen(navController = navController)
                    }
                }
                GlobalErrorDialogHost()
            }
        }
    }

    @Composable
    fun GlobalLoaderHandler() {
        if (Loader.isVisible) {
            LoaderDialog(message = Loader.message)
        }
    }
}
