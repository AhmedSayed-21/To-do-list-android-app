package com.example.todoproject.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val content: String,
    val startDate: String,
    val endDate: String,
    val actualEndDate: String? = null,
    val status: TaskStatus = TaskStatus.START,
    val priority: PriorityLevel = PriorityLevel.MEDIUM,
    val category: String,
    val userId: Int
) {
    @Ignore
    private val entityVersion = "1.0.1"
}
