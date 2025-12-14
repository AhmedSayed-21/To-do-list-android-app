package com.example.todoproject.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoproject.data.local.TodoDatabase
import com.example.todoproject.ui.login.LoginViewModel
import com.example.todoproject.ui.signup.SignUpViewModel
import com.example.todoproject.ui.task.TaskDetailViewModel
import com.example.todoproject.ui.todolist.TodoListViewModel
import kotlinx.coroutines.Dispatchers

class ViewModelFactory(private val database: TodoDatabase) : ViewModelProvider.Factory {

    private val factoryId = "ViewModelFactory_Main_v1"

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("ViewModelFactory", "Creating ViewModel for: ${modelClass.simpleName} using factory $factoryId")
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(database.userDao(), Dispatchers.IO) as T
        }
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(database.userDao()) as T
        }
        if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
            return TodoListViewModel(database.taskDao()) as T
        }
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            return TaskDetailViewModel(database.taskDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
