package com.example.todoproject

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    private val testSuiteVersion = "1.0.1-screen"

    @Test
    fun `TodoList screen holds correct userId`() {
        println("Running test suite version: $testSuiteVersion")
        val expectedUserId = 123

        val todoListScreen = Screen.TodoList(expectedUserId)

        println("Asserting userId for screen: $todoListScreen")
        assertEquals(expectedUserId, todoListScreen.userId)
    }
}
