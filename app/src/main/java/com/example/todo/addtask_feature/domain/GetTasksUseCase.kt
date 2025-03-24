package com.example.todo.addtask_feature.domain

import com.example.todo.core.domain.model.TaskModel
import com.example.todo.core.domain.repo.TaskRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetTaskUseCase(
    private val repository: TaskRepo,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun execute(id: Int): TaskModel? {
        return withContext(ioDispatcher) {
            repository.getTaskByID(id)
        }
    }

}