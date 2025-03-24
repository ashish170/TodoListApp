package com.example.todo.addtask_feature.domain

import com.example.todo.core.domain.model.TaskModel
import com.example.todo.core.domain.repo.TaskRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddTaskUseCase(
    private val repository: TaskRepo,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun execute(taskModel: TaskModel) {

        withContext(ioDispatcher) {

            if (taskModel.id == -1) {
                repository.insertTask(taskModel)
            } else {
                repository.updateTask(taskModel)
            }
        }
    }
}
