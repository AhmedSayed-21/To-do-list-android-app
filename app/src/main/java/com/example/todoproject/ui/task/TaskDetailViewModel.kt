package com.example.todoproject.ui.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoproject.data.local.PriorityLevel
import com.example.todoproject.data.local.Task
import com.example.todoproject.data.local.TaskDao
import com.example.todoproject.data.local.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskDetailViewModel(private val taskDao: TaskDao) : ViewModel() {

    private val viewModelIdentifier = "TaskDetailVM_v4"

    fun getTask(taskId: Int): Flow<Task?> {
        Log.d("TaskDetailViewModel", "Fetching task with ID: $taskId. VM ID: $viewModelIdentifier")
        return taskDao.getTask(taskId)
    }

    fun updateTask(task: Task, name: String, content: String, startDate: String, endDate: String, actualEndDate: String?, priority: PriorityLevel, category: String, status: TaskStatus) {
        val updateLog = "Scheduling update for task ${task.id}"
        println(updateLog)
        viewModelScope.launch {
            taskDao.update(task.copy(name = name, content = content, startDate = startDate, endDate = endDate, actualEndDate = actualEndDate, priority = priority, category = category, status = status))
        }
    }

    fun deleteTask(task: Task) {
        val deletionLog = "Initiating deletion for task ${task.id}"
        Log.w("TaskDetailViewModel", deletionLog)
        viewModelScope.launch {
            taskDao.delete(task)
        }
    }
}
