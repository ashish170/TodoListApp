package com.example.todo.addtask_feature.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.addtask_feature.domain.AddTaskUseCase
import com.example.todo.addtask_feature.domain.DeleteTaskUseCase
import com.example.todo.addtask_feature.domain.GetTaskUseCase
import com.example.todo.addtask_feature.presentation.components.AddTaskAction
import com.example.todo.addtask_feature.presentation.components.AddTaskEvent
import com.example.todo.core.data.Priority
import com.example.todo.core.di.IODispatcher
import com.example.todo.core.di.MainDispatcher
import com.example.todo.core.domain.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val _getTaskUseCase: GetTaskUseCase,
    private val _addTaskUseCase: AddTaskUseCase,
    private val _deleteTaskUseCase: DeleteTaskUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
): ViewModel() {

    private val TAG = "TaskViewModel"

    private var _taskId: Int = -1
    val taskId = _taskId
    private var _title: MutableStateFlow<String> = MutableStateFlow("")
    val title = _title.asStateFlow()
    private var _description = MutableStateFlow<String>("")
    val description = _description.asStateFlow()
    private var _priority = MutableStateFlow<Priority>(Priority.LOW)
    val priority = _priority.asStateFlow()

    private var _dueDate = MutableStateFlow<Date?>(null)
    val dueDate = _dueDate.asStateFlow()
    private var _pos = MutableStateFlow<Int>(0)
    val pos = _pos.asStateFlow()

    private val _showConfirmationDialog = MutableStateFlow<Boolean>(false)
    val showConfirmationDialog = _showConfirmationDialog.asStateFlow()
    private val _scope = viewModelScope

    private val _event = MutableSharedFlow<AddTaskEvent>()
    val event = _event.asSharedFlow()
    private val _isCompleted = MutableStateFlow<Boolean>(false)
    val isCompleted = _isCompleted.asStateFlow()

    init {
        val taskId = savedStateHandle.get<Int>("id") ?: -1
        _taskId = taskId

        Log.d(TAG, "init: taskId = $taskId")

        getTask(taskId)
    }

    private fun getTask(taskId: Int) {
        _scope.launch(ioDispatcher) {
            if (taskId != -1) {
                val task = _getTaskUseCase.execute(taskId) ?: return@launch
                _title.value = task.title
                _description.value = task.description
                _priority.value = task.priority
                _dueDate.value = task.dueDate
                _isCompleted.value = task.isCompleted
                _pos.value = task.position
            }
        }
    }

    fun action(action: AddTaskAction) {
        when (action) {
            AddTaskAction.BackIconOnClick -> backIconOnClick()
            AddTaskAction.DeleteTask -> deleteTask()
            is AddTaskAction.DescriptionOnValueChange -> descriptionOnValueChange(action.value)
            AddTaskAction.HideConfirmationDialog -> hideConfirmationDialog()
            AddTaskAction.ShowConfirmationDialog -> showConfirmationDialog()
            is AddTaskAction.TitleOnValueChange -> titleOnValueChange(action.value)
            is AddTaskAction.PriorityOnValueChange -> priorityOnValueChange(action.value)
            is AddTaskAction.DueDateOnValueChange -> dueDateOnValueChange(action.value)
            AddTaskAction.ToggleCompleted -> toggleCompleted()
        }
    }

    private fun priorityOnValueChange(value: Priority) {
        _priority.value = value
    }

    private fun dueDateOnValueChange(value: Date?) {
        _dueDate.value = value
    }
    private fun toggleCompleted() {
        _isCompleted.value = !_isCompleted.value
    }
    fun isCompletedChanged(value: Boolean) {
        _isCompleted.value = value
    }

    private fun titleOnValueChange(value: String) {
        Log.d(TAG, "titleOnValueChange: title = ${title.value}")
        _title.value = value
    }

    private fun descriptionOnValueChange(value: String) {
        _description.value = value
    }

    private fun backIconOnClick() = viewModelScope.launch(ioDispatcher) {
        Log.i("Ash in viewModel","Clicked")
        val taskModel = TaskModel(
            id = _taskId,
            title = _title.value,
            description = _description.value,
            priority = _priority.value,
            dueDate = _dueDate.value,
            isCompleted = _isCompleted.value,
            position = _pos.value
        )

        _addTaskUseCase.execute(taskModel)

        viewModelScope.launch(mainDispatcher) {
            _event.emit(AddTaskEvent.NavigateBack)
        }
    }

    private fun hideConfirmationDialog() {
        _showConfirmationDialog.value = false
    }

    private fun showConfirmationDialog() {
        _showConfirmationDialog.value = true
    }

    private fun deleteTask() = viewModelScope.launch(ioDispatcher) {
        val itemId = _taskId
        _deleteTaskUseCase.execute(itemId)

        hideConfirmationDialog()

        viewModelScope.launch(mainDispatcher) {
            _event.emit(AddTaskEvent.NavigateBack)
        }
    }
}
