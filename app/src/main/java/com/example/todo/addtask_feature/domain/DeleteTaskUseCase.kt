package com.example.todo.addtask_feature.domain

import com.example.todo.core.domain.repo.TaskRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteTaskUseCase(
    private val repository: TaskRepo,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {


    suspend fun execute(id: Int) {

        withContext(ioDispatcher) {
            repository.deleteTask(id)
        }
    }
}