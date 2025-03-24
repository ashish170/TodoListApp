package com.example.todo.showtask_home.domain

import com.example.todo.core.domain.repo.TaskRepo
import javax.inject.Inject

class ReorderUseCase @Inject constructor(
    private val taskRepository: TaskRepo
) {
    suspend fun execute(taskIds: List<Int>) {
        taskRepository.updateTaskOrder(taskIds)
    }
}