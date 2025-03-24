package com.example.todo.addtask_feature.presentation.components

import com.example.todo.core.data.Priority
import java.util.Date

sealed interface AddTaskAction {
    data class TitleOnValueChange(val value: String): AddTaskAction
    data class DescriptionOnValueChange(val value: String): AddTaskAction
    data class PriorityOnValueChange(val value: Priority): AddTaskAction
    data class DueDateOnValueChange(val value: Date?): AddTaskAction
    data object BackIconOnClick: AddTaskAction
    data object HideConfirmationDialog: AddTaskAction
    data object ShowConfirmationDialog: AddTaskAction
    data object DeleteTask: AddTaskAction
    data object ToggleCompleted: AddTaskAction
}

sealed class AddTaskEvent {
    data object NavigateBack: AddTaskEvent()
}