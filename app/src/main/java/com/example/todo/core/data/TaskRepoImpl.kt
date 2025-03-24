package com.example.todo.core.data

import com.example.todo.core.domain.model.TaskModel
import com.example.todo.core.domain.model.toEntity
import com.example.todo.core.domain.repo.TaskRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TaskRepoImpl(
    val dao: TaskDao
) : TaskRepo {

    private val _newTaskInsertionListener = MutableSharedFlow<TaskModel>()
    override val newTaskInsertionListener: SharedFlow<TaskModel> = _newTaskInsertionListener.asSharedFlow()

    private val _updateTaskListener = MutableSharedFlow<TaskModel>()
    override val updateTaskListener: SharedFlow<TaskModel> = _updateTaskListener.asSharedFlow()

    private val _deleteTaskListener = MutableSharedFlow<Int>()
    override val deleteTaskListener: SharedFlow<Int> = _deleteTaskListener.asSharedFlow()

    override suspend fun getAllTasks(): List<TaskModel> {
        return dao.getAllTasks().map { it.toModel() }
    }

    override suspend fun getTaskByID(id: Int): TaskModel? {
        return dao.getTaskById(id)?.toModel()
    }

    override suspend fun insertTask(item: TaskModel): Int {
        val newId = dao.insertTask(item.toEntity()).toInt()
        val newTask = item.copy(id = newId)
        _newTaskInsertionListener.emit(newTask)
        return newId
    }

    override suspend fun updateTask(item: TaskModel) {
        dao.updateTask(item.toEntity())
        _updateTaskListener.emit(item)
    }

    override suspend fun deleteTask(id: Int) {
        dao.deleteTaskById(id)
        _deleteTaskListener.emit(id)
    }

    override suspend fun deleteAllTasks() {
        dao.deleteAllTasks()
    }
    override suspend fun updateTaskOrder(taskIds: List<Int>) {
        val tasks = dao.getAllTasks()
        val positionMap = taskIds.mapIndexed { index, id -> id to index }.toMap()
        tasks.forEach { task ->
            positionMap[task.id]?.let { newPosition ->
                task.id?.let { dao.updateTaskPosition(it, newPosition) }
            }
        }
    }
}
