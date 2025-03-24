package com.example.todo.showtask_home.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.addtask_feature.domain.AddTaskUseCase
import com.example.todo.core.DeleteAllTasksUseCase
import com.example.todo.core.ListenTaskUseCase
import com.example.todo.core.TaskEvent
import com.example.todo.core.data.FilterOption
import com.example.todo.core.data.SortOption
import com.example.todo.core.di.IODispatcher
import com.example.todo.core.di.MainDispatcher
import com.example.todo.core.domain.model.TaskModel
import com.example.todo.navigation.Routes
import com.example.todo.showtask_home.domain.GetAllTaskUseCase
import com.example.todo.showtask_home.domain.ReorderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowTasksViewModel @Inject constructor(
    private val getTasksUseCase: GetAllTaskUseCase,
    private val listenTasksUseCase: ListenTaskUseCase,
    private val _deleteAllTaskUseCase: DeleteAllTasksUseCase,
    private val _addTaskUseCase: AddTaskUseCase,
    private val _reorderUseCase: ReorderUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val TAG = "TaskViewModel"

    val completedTasksList = mutableStateListOf<TaskModel>()
    val tasksList = mutableStateListOf<TaskModel>()
    private val _eventFlow = MutableSharedFlow<ShowTasksEvent>()
    val eventFlow: SharedFlow<ShowTasksEvent> = _eventFlow.asSharedFlow()
    val _scope = viewModelScope
    private val _currentFilter = mutableStateOf(FilterOption.ALL)
    val currentFilter: State<FilterOption> = _currentFilter

    private val _currentSort = mutableStateOf(SortOption.PRIORITY)
    val currentSort: State<SortOption> = _currentSort

    init {
        getTasks()
        _scope.launch(ioDispatcher) {
            listenTasksUseCase.execute().collect { event ->
                when (event) {
                    is TaskEvent.Insert -> tasksList.add(0, event.value)
                    is TaskEvent.Update -> {
                        val itemIndex = tasksList.indexOfFirst { it.id == event.value.id }
                        if (itemIndex != -1) {
                            tasksList[itemIndex] = event.value
                        }
                    }
                    is TaskEvent.Delete -> {
                        val itemIndex = tasksList.indexOfFirst { it.id == event.value }
                        if (itemIndex != -1) {
                            tasksList.removeAt(itemIndex)
                        }
                    }
                }
            }
        }
    }

    private fun getTasks() {
        _scope.launch(ioDispatcher) {
            println("$TAG: init - getTasks - START")
            val items = getTasksUseCase.execute()
            delay(500L) // Optional delay to simulate loading
            tasksList.addAll(items)
            println("$TAG: init - getTasks - END")
        }
    }

    fun deleteAllTasks() = viewModelScope.launch(ioDispatcher) {
        _deleteAllTaskUseCase.execute()
        val items = getTasksUseCase.execute()
        tasksList.addAll(items)
    }

    fun action(action: HomeAction) {
        when (action) {
            HomeAction.AddNewTask -> addNewTask()
            is HomeAction.ListItemOnClick -> listItemOnClick(action.value)
            is HomeAction.ToggleTaskCompletion -> toggleTaskCompletion(action.taskId, action.isCompleted)
            is HomeAction.ReorderTasks -> reorderTasks(action.taskIds)
        }
    }
    private fun reorderTasks(taskIds: List<Int>) = _scope.launch(ioDispatcher) {
        val positionMap = taskIds.mapIndexed { index, taskId -> taskId to index }.toMap()
        val reorderedList = tasksList.sortedBy { positionMap[it.id] ?: Int.MAX_VALUE }
        tasksList.clear()
        tasksList.addAll(reorderedList)
        _reorderUseCase.execute(taskIds)
    }

    private fun addNewTask() = _scope.launch {
        val route = Routes.ADD_TASK + "/-1"
        _eventFlow.emit(ShowTasksEvent.NavigateNext(route))
    }

    private fun listItemOnClick(id: Int) = _scope.launch(mainDispatcher) {
        val route = Routes.ADD_TASK + "/$id"
        _eventFlow.emit(ShowTasksEvent.NavigateNext(route))
    }

    private fun toggleTaskCompletion(taskId: Int, isCompleted: Boolean) = _scope.launch(ioDispatcher) {
        val taskIndex = tasksList.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            Log.i("Ash in vm",isCompleted.toString())
            val updatedTask = tasksList[taskIndex].copy(isCompleted = isCompleted)
            tasksList[taskIndex] = updatedTask
            updateCompletedTasks(updatedTask)
            _addTaskUseCase.execute(updatedTask)
            updateCompletedTasksList()
        }
    }

    private fun updateCompletedTasks(task: TaskModel) {
        if (task.isCompleted) {
            if (!completedTasksList.contains(task)) {
                completedTasksList.add(task)
            }
        } else {
            removeCompletedTask(task)
        }
    }

    private fun removeCompletedTask(task: TaskModel) {
        completedTasksList.removeAll { it.id == task.id }
    }

    private fun updateCompletedTasksList() {
        completedTasksList.clear()
        completedTasksList.addAll(tasksList.filter { it.isCompleted })
    }
    val filteredAndSortedTasks: List<TaskModel>
        get() = filterAndSortTasks(tasksList, currentFilter.value, currentSort.value)

    fun updateFilter(filter: FilterOption) {
        _currentFilter.value = filter

    }
    fun updateSort(sort: SortOption)
    {
        _currentSort.value = sort
    }

    private fun filterAndSortTasks(tasks: List<TaskModel>, filter: FilterOption, sort: SortOption): List<TaskModel> {
        var filteredTasks = tasks

        filteredTasks = when (filter) {
            FilterOption.ALL -> tasks
            FilterOption.COMPLETED -> tasks.filter { it.isCompleted }
            FilterOption.PENDING -> tasks.filter { !it.isCompleted }
        }

        return when (sort) {
            SortOption.PRIORITY -> filteredTasks.sortedByDescending { it.priority }
            SortOption.DUE_DATE -> filteredTasks.sortedBy { it.dueDate }
            SortOption.ALPHABETICAL -> filteredTasks.sortedBy { it.title }
        }
    }
}

