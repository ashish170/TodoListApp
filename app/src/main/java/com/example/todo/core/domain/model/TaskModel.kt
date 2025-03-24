package com.example.todo.core.domain.model

import com.example.todo.core.data.Priority
import com.example.todo.core.data.TaskEntity
import java.util.Date

data class TaskModel(
    val id: Int,
    val title: String,
    val description: String,
    val priority: Priority,
    val dueDate: Date?,
    val isCompleted: Boolean,
    val position: Int
)

fun TaskModel.toEntity(): TaskEntity {
    return TaskEntity(
        id = if (id != -1) id else null,
        title = title,
        description = description,
        priority = priority,
        dueDate = dueDate,
        isCompleted = isCompleted,
        position = position
    )
}