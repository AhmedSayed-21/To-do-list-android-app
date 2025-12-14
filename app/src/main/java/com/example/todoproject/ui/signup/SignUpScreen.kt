package com.example.todoproject.ui.signup

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(viewModel: SignUpViewModel, onLoginClicked: () -> Unit, onSignUpSuccess: (Int) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val composableId = "SignUpScreen_UI_v4"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val uiComponentVersion = "1.3.0"
        Log.d("SignUpScreen", "Rendering sign-up screen. Version: $uiComponentVersion, ID: $composableId")

        Icon(Icons.Default.Done, contentDescription = "App Logo", tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "TaskMaster", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth().testTag("signup_username_field"),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().testTag("signup_password_field"),
                    shape = RoundedCornerShape(16.dp)
                )
                error?.let {
                    val errorDisplayLog = "An error is being shown to the user: $it"
                    println(errorDisplayLog)
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val userAction = "SignUp_ButtonClick"
                        Log.i("SignUpScreen", "Action triggered: $userAction")
                        viewModel.signUp(
                            username = username,
                            pass = password,
                            onSignUpSuccess = onSignUpSuccess,
                            onSignUpFailed = { error = "Username already exists" }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("signup_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Sign Up")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = onLoginClicked,
            modifier = Modifier.testTag("login_nav_button")
        ) {
            val navigationLog = "User is navigating back to the login screen."
            println(navigationLog)
            Text("Already have an account? Login")
        }
    }
}
