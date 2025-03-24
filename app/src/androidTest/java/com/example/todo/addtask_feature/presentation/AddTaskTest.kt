package com.example.todo.addtask_feature.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.todo.addtask_feature.presentation.components.AddTaskAction
import com.example.todo.core.data.Priority
import com.example.todo.showtask_home.presentation.ShowTasksViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class AddTaskScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTaskCreationFlow() {
        val mockAddTaskViewModel = mockViewModel()
        val mockShowTasksViewModel = mockViewModelShowTask()

        composeTestRule.setContent {
            AddTaskScreen(
                viewModel = mockAddTaskViewModel,
                viewModelShowTask = mockShowTasksViewModel,
                navController = rememberNavController(),
                navigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("What do you want to add?")
            .performTextInput("My New Task")

        composeTestRule.onNodeWithText("Add Description")
            .performTextInput("This is a new task description")

        composeTestRule.onNodeWithText("Priority").performClick()
        composeTestRule.onNodeWithText("High").performClick()
        composeTestRule.onNodeWithText("Due Date").performClick()
        composeTestRule.onNodeWithText("Add Task").performClick()

        coVerify {
            mockAddTaskViewModel.action(AddTaskAction.BackIconOnClick)
        }
    }

    private fun mockViewModel(): AddTaskViewModel {
        val mockViewModel = mockk<AddTaskViewModel>(relaxed = true)

        every { mockViewModel.title } returns MutableStateFlow("My New Task")
        every { mockViewModel.description } returns MutableStateFlow("This is a new task description")
        every { mockViewModel.priority } returns MutableStateFlow(Priority.HIGH)
        every { mockViewModel.dueDate } returns MutableStateFlow(null)
        every { mockViewModel.showConfirmationDialog } returns MutableStateFlow(false)
        every { mockViewModel.isCompleted } returns MutableStateFlow(false)

        coEvery { mockViewModel.action(any()) } just Runs

        return mockViewModel
    }

    private fun mockViewModelShowTask(): ShowTasksViewModel {
        val mockViewModelShowTask = mockk<ShowTasksViewModel>(relaxed = true)

        coEvery { mockViewModelShowTask.action(any()) } just Runs

        return mockViewModelShowTask
    }
    @Test
    fun testAnimationTriggers() {
        composeTestRule.setContent {
            AddTaskScreen(
                viewModel = mockViewModel(),
                viewModelShowTask = mockViewModelShowTask(),
                navController = rememberNavController(),
                navigateBack = { }
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Add New Task")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("What do you want to add?")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Add Task")
            .assertExists()
            .assertIsDisplayed()
    }
}