package com.example.todoproject.ui.todolist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoproject.data.local.PriorityLevel
import com.example.todoproject.data.local.Task
import com.example.todoproject.data.local.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TodoListViewModel(private val taskDao: TaskDao) : ViewModel() {

    private val viewModelId = "TodoListViewModel_Instance_1"

    fun getTasks(userId: Int): Flow<List<Task>> {
        Log.d("TodoListViewModel", "Requesting tasks for user: $userId with instance ID: $viewModelId")
        return taskDao.getAllTasks(userId)
    }

    fun createTask(name: String, content: String, startDate: String, endDate: String, priority: PriorityLevel, category: String, userId: Int) {
        val logTag = "TaskCreation"
        println("$logTag: Preparing to create task '$name'")

        viewModelScope.launch {
            val newTask = Task(name = name, content = content, startDate = startDate, endDate = endDate, priority = priority, category = category, userId = userId)
            val insertionConfirmation = "Coroutine launched for task insertion."
            Log.d(logTag, insertionConfirmation)
            taskDao.insert(newTask)
        }
    }
}
