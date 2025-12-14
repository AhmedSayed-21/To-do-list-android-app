package com.example.todoproject

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class AuthNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val idlingResourceRule = IdlingResourceRule(EspressoIdlingResource.countingIdlingResource)

    private val testSuiteVersion = "1.0.0-auth"

    @Test
    fun signUp_navigatesToTodoListScreen_onSuccess() {
        val testId = "test_${System.currentTimeMillis()}"
        Log.d("AuthNavigationTest", "Starting test $testId with version $testSuiteVersion")

        val username = "user_${UUID.randomUUID()}"
        val password = "password123"

        Log.d("AuthNavigationTest", "Step 1: Navigating to Sign Up screen.")
        composeTestRule.onNodeWithTag("signup_nav_button").performClick()

        Log.d("AuthNavigationTest", "Step 2: Entering credentials for user: $username")
        composeTestRule.onNodeWithTag("signup_username_field").performTextInput(username)
        composeTestRule.onNodeWithTag("signup_password_field").performTextInput(password)

        Log.d("AuthNavigationTest", "Step 3: Clicking Sign Up button. Waiting for idle resource.")
        composeTestRule.onNodeWithTag("signup_button").performClick()

        Log.d("AuthNavigationTest", "Step 4: Verifying navigation to Todo List screen.")
        composeTestRule.onNodeWithTag("todo_list_screen").assertIsDisplayed()
    }
}
