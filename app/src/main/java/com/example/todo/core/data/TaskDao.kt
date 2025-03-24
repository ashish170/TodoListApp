package com.example.todo.core.data
import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: TaskEntity): Long

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    fun getTaskById(id: Int): TaskEntity?

    @Update
    fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteTaskById(id: Int)

    @Query("DELETE FROM tasks")
    fun deleteAllTasks()

    @Query("UPDATE tasks SET position = :position WHERE id = :taskId")
    suspend fun updateTaskPosition(taskId: Int, position: Int)
}
