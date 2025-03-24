package com.example.todo.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todo.core.domain.model.TaskModel
import java.util.Date

enum class Priority {
    LOW, MEDIUM, HIGH
}
enum class FilterOption { ALL, COMPLETED, PENDING }
enum class SortOption { PRIORITY, DUE_DATE, ALPHABETICAL }

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val title: String,
    val description: String?,
    val priority: Priority,
    val dueDate: Date?,
    val isCompleted: Boolean,

    val position: Int
)

fun TaskEntity.toModel(): TaskModel {
    return TaskModel(
        id = this.id ?: -1,
        title = this.title,
        description = this.description ?: "",
        priority = this.priority,
        dueDate = this.dueDate,
        isCompleted = this.isCompleted,
        position = this.position
    )
}