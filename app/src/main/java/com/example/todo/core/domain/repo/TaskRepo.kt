package com.example.todo.core.domain.repo

import com.example.todo.core.domain.model.TaskModel
import kotlinx.coroutines.flow.SharedFlow

interface TaskRepo {
    val newTaskInsertionListener: SharedFlow<TaskModel>
    val updateTaskListener: SharedFlow<TaskModel>
    val deleteTaskListener: SharedFlow<Int>

    suspend fun getAllTasks(): List<TaskModel>

    suspend fun getTaskByID(id: Int): TaskModel?

    suspend fun insertTask(item: TaskModel): Int

    suspend fun updateTask(item: TaskModel)

    suspend fun deleteTask(id: Int)
    suspend fun deleteAllTasks()
    suspend fun updateTaskOrder(taskIds: List<Int>)
}