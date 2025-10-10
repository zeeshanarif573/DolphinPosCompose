package com.retail.dolphinpos.presentation.features.ui.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import com.retail.dolphinpos.common.components.BaseText
import com.retail.dolphinpos.common.components.HeaderAppBarAuth
import com.retail.dolphinpos.presentation.R
import com.retail.dolphinpos.presentation.util.ComposeLoaderManager
import com.retail.dolphinpos.presentation.util.ErrorDialogHandler
import com.retail.dolphinpos.presentation.util.Utils

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isButtonEnabled = username.isNotBlank() && password.isNotBlank()

    // Observe ViewModel events
    val context = LocalContext.current

    LaunchedEffect(viewModel.loginUiEvent) {
        when (val event = viewModel.loginUiEvent) {
            is LoginUiEvent.ShowLoading -> ComposeLoaderManager.show("Please Wait...")
            is LoginUiEvent.HideLoading -> ComposeLoaderManager.hide()
            is LoginUiEvent.ShowError -> {
                ErrorDialogHandler.showError(message = event.message, buttonText = "Try Again") {}
            }

            is LoginUiEvent.NavigateToRegister -> {
                navController.navigate("selectRegister")
            }

            null -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.splash_background_image),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        HeaderAppBarAuth()

        // Foreground layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side - Texts
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BaseText(
                    text = "Let’s Get Started",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                BaseText(
                    text = "Log in to your Retail POS system and manage transactions effortlessly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            // Right Side - Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.bgColorPrimary)
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 20.dp)
                    )

                    // Username
                    BaseText(
                        text = "Username",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorResource(id = R.color.bgColorLabels),
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("Enter Username") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.primary),
                            unfocusedBorderColor = colorResource(id = R.color.bgColorLabels),
                            cursorColor = colorResource(id = R.color.primary)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password
                    BaseText(
                        text = "Password",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorResource(id = R.color.bgColorLabels),
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter Password") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.primary),
                            unfocusedBorderColor = colorResource(id = R.color.bgColorLabels),
                            cursorColor = colorResource(id = R.color.primary)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login Button
                    Button(
                        onClick = {
                            if (isButtonEnabled) {
                                viewModel.login(username, password)
                            }
                        },
                        enabled = isButtonEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isButtonEnabled)
                                colorResource(id = R.color.primary)
                            else
                                colorResource(id = R.color.primary).copy(alpha = 0.4f),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Login")
                    }
                }
            }
        }
    }
}

