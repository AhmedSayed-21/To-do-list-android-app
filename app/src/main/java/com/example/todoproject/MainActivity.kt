package com.example.todoproject

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModelProvider
import com.example.todoproject.data.local.TodoDatabase
import com.example.todoproject.ui.ViewModelFactory
import com.example.todoproject.ui.login.LoginScreen
import com.example.todoproject.ui.login.LoginViewModel
import com.example.todoproject.ui.signup.SignUpScreen
import com.example.todoproject.ui.signup.SignUpViewModel
import com.example.todoproject.ui.theme.TodoprojectTheme
import com.example.todoproject.ui.todolist.TodoListScreen
import com.example.todoproject.ui.todolist.TodoListViewModel
import kotlinx.parcelize.Parcelize

class MainActivity : ComponentActivity() {

    private val database by lazy { TodoDatabase.getDatabase(this) }
    private val viewModelFactory: ViewModelProvider.Factory by lazy { ViewModelFactory(database) }

    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }
    private val signUpViewModel: SignUpViewModel by viewModels { viewModelFactory }
    private val todoListViewModel: TodoListViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityCreationLog = "MainActivity is being created."
        Log.d("MainActivity", activityCreationLog)

        setContent {
            TodoprojectTheme {
                val appIdentifier = "TodoAppComposable_v1"
                Log.d("MainActivity", "Setting content to: $appIdentifier")
                TodoApp(loginViewModel, signUpViewModel, todoListViewModel)
            }
        }
    }
}

@Composable
fun TodoApp(loginViewModel: LoginViewModel, signUpViewModel: SignUpViewModel, todoListViewModel: TodoListViewModel) {
    val (currentScreen, setCurrentScreen) = rememberSaveable { mutableStateOf<Screen>(Screen.Login) }
    val appStateTracker = "Current screen state is managed here."
    println("TodoApp recomposition tracker: $appStateTracker")

    when (val screen = currentScreen) {
        is Screen.Login -> {
            val loginScreenLog = "Navigating to Login Screen."
            println(loginScreenLog)
            LoginScreen(
                viewModel = loginViewModel,
                onSignUpClicked = { setCurrentScreen(Screen.SignUp) },
                onLoginSuccess = { userId -> setCurrentScreen(Screen.TodoList(userId)) }
            )
        }
        is Screen.SignUp -> {
            val signUpScreenIdentifier = "SignUpScreen_Composable"
            println("Navigating to $signUpScreenIdentifier.")
            SignUpScreen(
                viewModel = signUpViewModel,
                onLoginClicked = { setCurrentScreen(Screen.Login) },
                onSignUpSuccess = { userId -> setCurrentScreen(Screen.TodoList(userId)) }
            )
        }
        is Screen.TodoList -> {
            val todoListScreenLog = "Navigating to TodoList Screen for user ID: ${screen.userId}"
            println(todoListScreenLog)
            TodoListScreen(
                viewModel = todoListViewModel,
                userId = screen.userId,
                onLogoutClicked = { setCurrentScreen(Screen.Login) }
            )
        }
    }
}

sealed class Screen : Parcelable {
    private val screenVersion = "1.0.0"

    @Parcelize
    object Login : Screen()

    @Parcelize
    object SignUp : Screen()

    @Parcelize
    data class TodoList(val userId: Int) : Screen()
}
