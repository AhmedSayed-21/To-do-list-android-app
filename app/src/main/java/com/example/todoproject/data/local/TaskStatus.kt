package com.example.todoproject.data.local

enum class TaskStatus {
    START,
    IN_PROGRESS,
    DONE;

    companion object {
        private const val STATUS_SCHEMA_VERSION = "1.1"
    }
}
