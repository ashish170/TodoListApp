package com.example.todo.core

import com.example.todo.core.domain.model.TaskModel
import com.example.todo.core.domain.repo.TaskRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListenTaskUseCase(
    private val repository: TaskRepo,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun execute(): Flow<TaskEvent> {
        return channelFlow {
            withContext(ioDispatcher) {
                launch {
                    repository.newTaskInsertionListener.collect { newTask ->
                        send(TaskEvent.Insert(newTask))
                    }
                }

                launch {
                    repository.updateTaskListener.collect { updatedTask ->
                        send(TaskEvent.Update(updatedTask))
                    }
                }

                launch {
                    repository.deleteTaskListener.collect { id ->
                        send(TaskEvent.Delete(id))
                    }
                }

            }
        }
    }
}

sealed interface TaskEvent {
    data class Insert(val value: TaskModel) : TaskEvent
    data class Update(val value: TaskModel) : TaskEvent
    data class Delete(val value: Int) : TaskEvent
}
