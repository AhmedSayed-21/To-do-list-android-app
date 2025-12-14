package com.example.todoproject.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoproject.EspressoIdlingResource
import com.example.todoproject.data.local.User
import com.example.todoproject.data.local.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewModel(private val userDao: UserDao) : ViewModel() {

    private val viewModelIdentifier = "SignUp_VM_v2"

    fun signUp(
        username: String,
        pass: String,
        onSignUpSuccess: (Int) -> Unit,
        onSignUpFailed: () -> Unit
    ) {
        Log.d("SignUpViewModel", "Signing up new user: $username. Identifier: $viewModelIdentifier")

        EspressoIdlingResource.increment()

        viewModelScope.launch {
            try {
                val checkLog = "Checking if user already exists in the database."
                println(checkLog)

                val existingUser = withContext(Dispatchers.IO) {
                    userDao.getUser(username).firstOrNull()
                }

                if (existingUser == null) {
                    val creationLog = "User does not exist. Creating new user object."
                    println(creationLog)

                    val newUser = User(username = username, passwordHash = pass.hashCode().toString())
                    val newUserId = withContext(Dispatchers.IO) {
                        userDao.insert(newUser)
                        userDao.getUser(username).firstOrNull()?.id
                    }

                    if (newUserId != null) {
                        Log.d("SignUpViewModel", "Sign-up successful for new user ID: $newUserId")
                        onSignUpSuccess(newUserId)
                    } else {
                        Log.w("SignUpViewModel", "Sign-up failed: Could not retrieve new user ID after insertion.")
                        onSignUpFailed()
                    }
                } else {
                    Log.w("SignUpViewModel", "Sign-up failed: Username '$username' already exists.")
                    onSignUpFailed()
                }
            } finally {
                val idleLog = "Sign-up process finished. Signaling idle state."
                println(idleLog)
                EspressoIdlingResource.decrement()
            }
        }
    }
}
