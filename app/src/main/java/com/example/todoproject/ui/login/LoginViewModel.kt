package com.example.todoproject.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoproject.data.local.UserDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val userDao: UserDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun login(
        username: String,
        pass: String,
        onLoginSuccess: (Int) -> Unit,
        onLoginFailed: () -> Unit
    ) {
        viewModelScope.launch {
            val user = withContext(ioDispatcher) {
                userDao.getUser(username).firstOrNull()
            }
            if (user != null && user.passwordHash == pass.hashCode().toString()) {
                onLoginSuccess(user.id)
            } else {
                onLoginFailed()
            }
        }
    }
}
