package com.retail.dolphinpos.presentation.features.ui.auth.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.retail.dolphinpos.presentation.R
import com.retail.dolphinpos.common.PreferenceManager
import com.retail.dolphinpos.common.components.HeaderAppBarAuth

@Composable
fun SplashScreen(
    navController: NavController,
    preferenceManager: PreferenceManager,
    viewModel: SplashViewModel = hiltViewModel()
) {
    // Collect states from ViewModel flows
    val currentTime by viewModel.currentTime.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.splash_background_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        HeaderAppBarAuth()

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Time
            Text(
                text = currentTime,
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action button
            Button(
                onClick = {
                    val isLoggedIn = preferenceManager.isLogin()
                    val hasRegister = preferenceManager.getRegister()

                    when {
                        !isLoggedIn -> navController.navigate("login")
                        !hasRegister -> navController.navigate("selectRegister")
                        else -> navController.navigate("pinCode")
                    }
                },
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                Text(text = "Let’s Start")
            }
        }
    }
}
