package com.example.todo.showtask_home.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import com.example.todo.addtask_feature.presentation.AddTaskViewModel
import com.example.todo.core.data.Priority
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule

import org.junit.jupiter.api.Test

 class HomeScreenKtTest {

  @get:Rule
  val composeTestRule = createComposeRule()
  val mockAddTaskViewModel = mockViewModel()
  val mockShowTasksViewModel = mockViewModelShowTask()

  @Test
  fun testEmptyTaskListDisplaysNoTasksMessage() {
   composeTestRule.setContent {
    HomeScreen(viewModel = mockShowTasksViewModel)
   }

   composeTestRule.onNodeWithText("No tasks yet!").assertIsDisplayed()
   composeTestRule.onNodeWithText("Start adding your tasks to stay on top of things.").assertIsDisplayed()
  }
  @Test
  fun testFloatingActionButtonIsClickable() {
   composeTestRule.setContent {
    HomeScreen(viewModel = mockShowTasksViewModel)
   }

   composeTestRule.onNodeWithContentDescription("Add New Task").assertExists()

   composeTestRule.onNodeWithContentDescription("Add New Task").performClick()

  }

  @Test
  fun testTaskListDisplaysTasks() {
   composeTestRule.setContent {
    HomeScreen(viewModel = mockShowTasksViewModel)
   }

   composeTestRule.onNodeWithText("Sample Task 1").assertIsDisplayed()
   composeTestRule.onNodeWithText("Sample Task 2").assertIsDisplayed()

   composeTestRule.onNodeWithText("Description for Task 1").assertIsDisplayed()
   composeTestRule.onNodeWithText("Description for Task 2").assertIsDisplayed()
  }
  @Test
  fun testTaskCompletionToggle() {
   composeTestRule.setContent {
    HomeScreen(viewModel = mockShowTasksViewModel)
   }

   composeTestRule.onNodeWithText("Sample Task 1")
    .onChildAt(0)
    .performClick()

  }
  @Test
  fun testReorderTasks() {
   composeTestRule.setContent {
    HomeScreen(viewModel = mockShowTasksViewModel)
   }

   val firstTask = composeTestRule.onNodeWithText("Sample Task 1")
   val secondTask = composeTestRule.onNodeWithText("Sample Task 2")

   firstTask.performTouchInput { swipeUp() }
   secondTask.performTouchInput { swipeDown() }

  }
  @Test
  fun testFilterAndSortOptions() {
   composeTestRule.setContent {
    HomeScreen(viewModel = mockShowTasksViewModel)
   }

   composeTestRule.onNodeWithContentDescription("Filter Options").performClick()
   composeTestRule.onNodeWithText("All Tasks").assertExists()

   composeTestRule.onNodeWithContentDescription("Sort Options").performClick()
   composeTestRule.onNodeWithText("Priority").assertExists()
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


}