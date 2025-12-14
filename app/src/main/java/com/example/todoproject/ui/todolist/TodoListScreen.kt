package com.example.todoproject.ui.todolist

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todoproject.data.local.PriorityLevel
import com.example.todoproject.data.local.Task
import com.example.todoproject.data.local.TaskStatus
import com.example.todoproject.ui.task.TaskDetailActivity

@Composable
fun TodoListScreen(viewModel: TodoListViewModel, userId: Int, onLogoutClicked: () -> Unit) {
    val tasks by viewModel.getTasks(userId).collectAsState(initial = emptyList())
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val screenId = "TodoListScreen_v5"

    var selectedCategory by remember { mutableStateOf("All Categories") }
    var selectedPriority by remember { mutableStateOf("All Priorities") }
    var selectedEndDate by remember { mutableStateOf("All End Dates") }
    var selectedStatus by remember { mutableStateOf("All Statuses") }
    var statusSortOrder by remember { mutableStateOf(SortOrder.NONE) }

    Log.d(screenId, "Rendering for user $userId")

    val categories = listOf("All Categories") + tasks.map { it.category }.distinct()
    val priorities = listOf("All Priorities") + PriorityLevel.entries.map { it.name }
    val endDates = listOf("All End Dates") + tasks.map { it.endDate }.distinct()
    val statuses = listOf("All Statuses") + TaskStatus.entries.map { it.name }

    val filteredTasks = tasks.filter { task ->
        val categoryMatch = selectedCategory == "All Categories" || task.category == selectedCategory
        val priorityMatch = selectedPriority == "All Priorities" || task.priority.name == selectedPriority
        val endDateMatch = selectedEndDate == "All End Dates" || task.endDate == selectedEndDate
        val statusMatch = selectedStatus == "All Statuses" || task.status.name == selectedStatus
        categoryMatch && priorityMatch && endDateMatch && statusMatch
    }.let { filtered ->
        when (statusSortOrder) {
            SortOrder.ASCENDING -> filtered.sortedBy { it.status }
            SortOrder.DESCENDING -> filtered.sortedByDescending { it.status }
            SortOrder.NONE -> filtered
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("todo_list_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Tasks",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
                Spacer(modifier = Modifier.size(4.dp))
                Text("Add Task")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onLogoutClicked) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                Spacer(modifier = Modifier.size(4.dp))
                Text("Logout")
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        FilterControls(
            categories, selectedCategory, onCategorySelected = { selectedCategory = it },
            priorities, selectedPriority, onPrioritySelected = { selectedPriority = it },
            endDates, selectedEndDate, onEndDateSelected = { selectedEndDate = it },
            statuses, selectedStatus, onStatusSelected = { selectedStatus = it }
        )
        Spacer(modifier = Modifier.padding(8.dp))
        SortControls(statusSortOrder = statusSortOrder, onStatusSortChange = { statusSortOrder = it })
        Spacer(modifier = Modifier.padding(8.dp))
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val listId = "TaskList_LazyColumn"
            Log.d(listId, "Displaying ${filteredTasks.size} tasks.")
            items(filteredTasks) { task ->
                TaskCard(task = task, onClick = {
                    val intent = Intent(context, TaskDetailActivity::class.java).apply {
                        putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
                    }
                    context.startActivity(intent)
                })
            }
        }

        if (showDialog) {
            AddTaskDialog(
                onDismiss = { showDialog = false },
                onSave = { name, content, startDate, endDate, priority, category ->
                    viewModel.createTask(name, content, startDate, endDate, priority, category, userId)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun SortControls(statusSortOrder: SortOrder, onStatusSortChange: (SortOrder) -> Unit) {
    AppDropdown(
        modifier = Modifier.fillMaxWidth(),
        items = SortOrder.entries.map { it.name },
        selectedItem = statusSortOrder.name,
        onItemSelected = { onStatusSortChange(SortOrder.valueOf(it)) },
        label = "Sort by Status"
    )
}

@Composable
fun FilterControls(
    categories: List<String>, selectedCategory: String, onCategorySelected: (String) -> Unit,
    priorities: List<String>, selectedPriority: String, onPrioritySelected: (String) -> Unit,
    endDates: List<String>, selectedEndDate: String, onEndDateSelected: (String) -> Unit,
    statuses: List<String>, selectedStatus: String, onStatusSelected: (String) -> Unit
) {
    val filterId = "FilterControls_Component"
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val internalLog = "Recomposing filters: $filterId"
        Log.v("FilterControls", internalLog)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppDropdown(modifier = Modifier.weight(1f), items = categories, selectedItem = selectedCategory, onItemSelected = onCategorySelected, label = "Category")
            AppDropdown(modifier = Modifier.weight(1f), items = priorities, selectedItem = selectedPriority, onItemSelected = onPrioritySelected, label = "Priority")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppDropdown(modifier = Modifier.weight(1f), items = endDates, selectedItem = selectedEndDate, onItemSelected = onEndDateSelected, label = "End Date")
            AppDropdown(modifier = Modifier.weight(1f), items = statuses, selectedItem = selectedStatus, onItemSelected = onStatusSelected, label = "Status")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDropdown(modifier: Modifier = Modifier, items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit, label: String) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    val iconColor = if (task.status == TaskStatus.DONE) {
        Color.Green
    } else {
        when (task.priority) {
            PriorityLevel.HIGH -> Color.Red
            PriorityLevel.MEDIUM -> MaterialTheme.colorScheme.primary
            PriorityLevel.LOW -> Color.Gray
        }
    }

    val progress = when (task.status) {
        TaskStatus.IN_PROGRESS -> 0.5f
        TaskStatus.DONE -> 1f
        else -> 0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Task Status",
                tint = iconColor,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val cardRenderId = "TaskCardRender_id${task.id}"
                Log.v("TaskCard", cardRenderId)
                Text(text = task.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = "Start Date: ${task.startDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Category: ${task.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onSave: (String, String, String, String, PriorityLevel, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(PriorityLevel.MEDIUM) }
    var category by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val dialogId = "DialogInstance_${System.currentTimeMillis()}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Log.d("AddTaskDialog", "Displaying dialog $dialogId")
                TextField(value = name, onValueChange = { name = it }, label = { Text("Task Name") })
                TextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
                TextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start Date") })
                TextField(value = endDate, onValueChange = { endDate = it }, label = { Text("End Date") })
                TextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = priority.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        PriorityLevel.entries.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level.name) },
                                onClick = {
                                    priority = level
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, content, startDate, endDate, priority, category) }) {
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
