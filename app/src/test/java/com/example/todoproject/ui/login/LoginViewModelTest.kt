package com.example.todoproject.ui.login

import com.example.todoproject.MainDispatcherRule
import com.example.todoproject.data.local.User
import com.example.todoproject.data.local.UserDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var mockUserDao: UserDao

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Pass the test dispatcher from the rule to the ViewModel
        loginViewModel = LoginViewModel(mockUserDao, mainDispatcherRule.testDispatcher)
    }

    @Test
    fun `login with correct credentials calls onLoginSuccess`() = runTest {
        // Arrange
        val username = "testuser"
        val password = "password"
        val passwordHash = password.hashCode().toString()
        val fakeUser = User(id = 1, username = username, passwordHash = passwordHash)
        Mockito.`when`(mockUserDao.getUser(username)).thenReturn(flowOf(fakeUser))

        var wasSuccessCalled = false
        var receivedUserId = -1

        // Act
        loginViewModel.login(username, password,
            onLoginSuccess = { userId ->
                wasSuccessCalled = true
                receivedUserId = userId
            },
            onLoginFailed = {}
        )

        // Assert
        assertTrue("Success callback should have been called", wasSuccessCalled)
        assertEquals("The correct user ID should be received", fakeUser.id, receivedUserId)
    }

    @Test
    fun `login with incorrect credentials calls onLoginFailed`() = runTest {
        // Arrange
        val username = "testuser"
        val password = "wrongpassword"
        val correctPasswordHash = "password".hashCode().toString()
        val fakeUser = User(id = 1, username = username, passwordHash = correctPasswordHash)
        Mockito.`when`(mockUserDao.getUser(username)).thenReturn(flowOf(fakeUser))

        var wasFailureCalled = false

        // Act
        loginViewModel.login(username, password,
            onLoginSuccess = {},
            onLoginFailed = { wasFailureCalled = true }
        )

        // Assert
        assertTrue("Failure callback should have been called", wasFailureCalled)
    }
}
