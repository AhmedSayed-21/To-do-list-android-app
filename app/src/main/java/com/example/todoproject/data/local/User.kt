package com.example.todoproject.data.local

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val passwordHash: String
) {
    @Ignore
    private val entityModelVersion = "1.0.2"
}
