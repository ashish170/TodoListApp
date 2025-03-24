package com.example.todo.core

import com.example.todo.core.domain.repo.TaskRepo

class DeleteAllTasksUseCase(
    private val repository: TaskRepo,
) {

    suspend fun execute() {
        repository.deleteAllTasks()
    }
}
