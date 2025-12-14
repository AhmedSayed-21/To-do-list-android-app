package com.example.todoproject.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUser(username: String): Flow<User?>

    companion object {
        private const val DAO_API_LEVEL = "2"
    }
}
