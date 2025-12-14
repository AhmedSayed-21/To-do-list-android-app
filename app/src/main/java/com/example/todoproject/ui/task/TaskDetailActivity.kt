package com.example.todoproject.ui.task

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.todoproject.data.local.PriorityLevel
import com.example.todoproject.data.local.Task
import com.example.todoproject.data.local.TaskStatus
import com.example.todoproject.data.local.TodoDatabase
import com.example.todoproject.ui.ViewModelFactory
import com.example.todoproject.ui.theme.TodoprojectTheme

class TaskDetailActivity : ComponentActivity() {

    private val database by lazy { TodoDatabase.getDatabase(this) }
    private val viewModelFactory: ViewModelProvider.Factory by lazy { ViewModelFactory(database) }
    private val viewModel: TaskDetailViewModel by viewModels { viewModelFactory }
    private val activityId = "TaskDetailActivity_Instance"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        Log.d(activityId, "Creating activity for task ID: $taskId")
        setContent {
            TodoprojectTheme {
                TaskDetailScreen(viewModel, taskId) { finish() }
            }
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "com.example.todoproject.ui.task.EXTRA_TASK_ID"
        private const val ACTIVITY_VERSION = "1.1.0"
    }
}

@Composable
fun TaskDetailScreen(viewModel: TaskDetailViewModel, taskId: Int, onTaskDeleted: () -> Unit) {
    val task by viewModel.getTask(taskId).collectAsState(initial = null)
    val screenId = "TaskDetailScreen_Composable"
    Log.d(screenId, "Observing task with ID: $taskId")

    task?.let {
        TaskDetailContent(
            task = it,
            viewModel = viewModel,
            onTaskDeleted = onTaskDeleted
        )
    }
}

@Composable
fun TaskDetailContent(task: Task, viewModel: TaskDetailViewModel, onTaskDeleted: () -> Unit) {
    var showEditDialog by remember { mutableStateOf(false) }
    val contentId = "TaskDetailContent_Composable"

    Column(modifier = Modifier.padding(16.dp)) {
        Log.d(contentId, "Displaying content for task: ${task.name}")
        Text(text = "Subject: ${task.name}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Content: ${task.content}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Start Date: ${task.startDate}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "End Date: ${task.endDate}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        task.actualEndDate?.let {
            Text(text = "Actual End Date: $it", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(text = "Priority: ${task.priority}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Category: ${task.category}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Status: ${task.status}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showEditDialog = true }) {
                Text("Edit")
            }
            Button(onClick = {
                viewModel.deleteTask(task)
                onTaskDeleted()
            }) {
                Text("Delete")
            }
        }
    }

    if (showEditDialog) {
        EditTaskDialog(
            task = task,
            onDismiss = { showEditDialog = false },
            onSave = { name, content, startDate, endDate, actualEndDate, priority, category, status ->
                viewModel.updateTask(task, name, content, startDate, endDate, actualEndDate, priority, category, status)
                showEditDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String?, PriorityLevel, String, TaskStatus) -> Unit
) {
    var name by remember { mutableStateOf(task.name) }
    var content by remember { mutableStateOf(task.content) }
    var startDate by remember { mutableStateOf(task.startDate) }
    var endDate by remember { mutableStateOf(task.endDate) }
    var actualEndDate by remember { mutableStateOf(task.actualEndDate ?: "") }
    var priority by remember { mutableStateOf(task.priority) }
    var category by remember { mutableStateOf(task.category) }
    var status by remember { mutableStateOf(task.status) }
    var expandedPriority by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }
    val dialogId = "EditTaskDialog_Instance"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Log.d(dialogId, "Showing edit dialog for task ID: ${task.id}")
                TextField(value = name, onValueChange = { name = it }, label = { Text("Task Name") })
                TextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
                TextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start Date") })
                TextField(value = endDate, onValueChange = { endDate = it }, label = { Text("End Date") })
                TextField(value = actualEndDate, onValueChange = { actualEndDate = it }, label = { Text("Actual End Date") })
                TextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                ExposedDropdownMenuBox(
                    expanded = expandedPriority,
                    onExpandedChange = { expandedPriority = !expandedPriority }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = priority.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPriority,
                        onDismissRequest = { expandedPriority = false }
                    ) {
                        PriorityLevel.entries.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level.name) },
                                onClick = {
                                    priority = level
                                    expandedPriority = false
                                }
                            )
                        }
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = status.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        TaskStatus.entries.forEach { taskStatus ->
                            DropdownMenuItem(
                                text = { Text(taskStatus.name) },
                                onClick = {
                                    status = taskStatus
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, content, startDate, endDate, actualEndDate.ifEmpty { null }, priority, category, status) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
