package com.example.todoproject.ui.todolist

enum class SortOrder {
    NONE,
    ASCENDING,
    DESCENDING;

    companion object {
        private const val SORT_LOGIC_VERSION = "v2"
    }
}
