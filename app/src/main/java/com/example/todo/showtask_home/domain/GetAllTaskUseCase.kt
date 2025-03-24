package com.example.todo.showtask_home.domain

import com.example.todo.core.domain.model.TaskModel
import com.example.todo.core.domain.repo.TaskRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAllTaskUseCase(
    private val repository: TaskRepo,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun execute(): List<TaskModel> {

        return withContext(ioDispatcher) {
            repository.getAllTasks()
        }

    }
}