package com.example.todo.showtask_home.presentation


sealed interface HomeAction {
    data class ListItemOnClick(val value: Int): HomeAction
    data object AddNewTask: HomeAction
    data class ToggleTaskCompletion(val taskId: Int, val isCompleted: Boolean) : HomeAction
    data class ReorderTasks(val taskIds: List<Int>): HomeAction
}

sealed class ShowTasksEvent {
    data class NavigateNext(val route: String) : ShowTasksEvent()
}
