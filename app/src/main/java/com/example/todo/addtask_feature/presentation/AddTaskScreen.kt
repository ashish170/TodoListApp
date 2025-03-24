package com.example.todo.addtask_feature.presentation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todo.addtask_feature.presentation.components.AddTaskAction
import com.example.todo.addtask_feature.presentation.components.AddTaskEvent
import com.example.todo.addtask_feature.presentation.components.ConfirmationDialog
import com.example.todo.core.data.Priority
import com.example.todo.showtask_home.presentation.HomeAction
import com.example.todo.showtask_home.presentation.ShowTasksViewModel
import com.example.todo.util.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel = hiltViewModel(),
    viewModelShowTask: ShowTasksViewModel = hiltViewModel(),
    navController: NavController,
    navigateBack: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val description by viewModel.description.collectAsState()
    val dueDate by viewModel.dueDate.collectAsState()
    val showConfirmationDialog = viewModel.showConfirmationDialog.collectAsState()
    val isCompleted by viewModel.isCompleted.collectAsState()
    val taskId = viewModel.taskId
    val scrollState = rememberScrollState()

    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    val animationDuration = 500
    val animationSpec = tween<Float>(durationMillis = animationDuration, easing = EaseOutQuart)
    val headerAlpha = remember { Animatable(0f) }
    val formAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }
    val contentOffsetY = remember { Animatable(50f) }

    LaunchedEffect(key1 = true) {
        launch {
            headerAlpha.animateTo(1f, animationSpec = animationSpec)
        }
        launch {
            delay(150)
            formAlpha.animateTo(1f, animationSpec = animationSpec)
        }
        launch {
            contentOffsetY.animateTo(0f, animationSpec = animationSpec)
        }
        launch {
            delay(300)
            buttonAlpha.animateTo(1f, animationSpec = animationSpec)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest { event ->
            when(event) {
                is AddTaskEvent.NavigateBack -> navigateBack()
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp)
                    .alpha(headerAlpha.value),
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = onBackgroundColor,
                        modifier = Modifier.height(36.dp)
                    )
                }
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = onBackgroundColor,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                Log.i("Ash clicked delete", "delete clicked")
                                viewModel.action(AddTaskAction.ShowConfirmationDialog)
                            }
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.action(AddTaskAction.ToggleCompleted)
                        viewModelShowTask.action(
                            HomeAction.ToggleTaskCompletion(
                                taskId,
                                isCompleted
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Complete",
                        tint = if (isCompleted) onBackgroundColor else placeholderColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Add New Task",
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = onBackgroundColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .alpha(headerAlpha.value),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(formAlpha.value)
                        .offset(y = contentOffsetY.value.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        BasicTextField(
                            value = title,
                            onValueChange = { viewModel.action(AddTaskAction.TitleOnValueChange(it)) },
                            textStyle = TextStyle(
                                fontSize = 24.sp,
                                color = if (title.isEmpty()) placeholderColor else onBackgroundColor
                            ),
                            decorationBox = { innerTextField ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (title.isEmpty()) {
                                        Text(
                                            "What do you want to add?",
                                            fontSize = 24.sp,
                                            color = placeholderColor
                                        )
                                    } else {
                                        innerTextField()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        color = dividerColor
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        BasicTextField(
                            value = description,
                            onValueChange = { viewModel.action(AddTaskAction.DescriptionOnValueChange(it)) },
                            textStyle = TextStyle(
                                fontSize = 24.sp,
                                color = if (description.isEmpty()) placeholderColor else onBackgroundColor
                            ),
                            decorationBox = { innerTextField ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (description.isEmpty()) {
                                        Text(
                                            "Add Description",
                                            fontSize = 24.sp,
                                            color = placeholderColor
                                        )
                                    } else {
                                        innerTextField()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        color = dividerColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, dividerColor, RoundedCornerShape(16.dp))
                                .background(surfaceColor)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Priority",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    color = onBackgroundColor.copy(alpha = 0.8f)
                                )
                            )
                            PriorityDropdown(
                                priority = priority,
                                onPrioritySelected = { viewModel.action(AddTaskAction.PriorityOnValueChange(it)) },
                                textColor = onBackgroundColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, dividerColor, RoundedCornerShape(16.dp))
                                .background(surfaceColor)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Due Date",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    color = onBackgroundColor.copy(alpha = 0.8f)
                                )
                            )
                            TimeDatePicker(
                                value = Utils.formatDate(dueDate),
                                onValueChange = { viewModel.action(AddTaskAction.DueDateOnValueChange(it)) },
                                textColor = onBackgroundColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                backgroundColor.copy(alpha = 0f),
                                backgroundColor
                            )
                        )
                    )
                    .padding(bottom = 24.dp)
            ) {
                Button(
                    onClick = {
                        if(title.isNotEmpty()) viewModel.action(AddTaskAction.BackIconOnClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 64.dp)
                        .height(56.dp)
                        .alpha(buttonAlpha.value),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    )
                ) {
                    Text(
                        text = "Add Task",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    if (showConfirmationDialog.value) {
        ConfirmationDialog(
            dismissButton = {
                viewModel.action(AddTaskAction.HideConfirmationDialog)
            },
            confirmButton = {
                viewModel.action(AddTaskAction.DeleteTask)
            },
        )
    }
}


@Composable
fun PriorityDropdown(
    priority: Priority,
    onPrioritySelected: (Priority) -> Unit,
    textColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Text(
            text = priority.name,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .background(Color.Transparent)
                .padding(horizontal = 25.dp, vertical = 16.dp),
            fontSize = 20.sp,
            color = textColor
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = "Dropdown Arrow",
            tint = textColor
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Priority.values().forEach { priority ->
                DropdownMenuItem(
                    onClick = {
                        onPrioritySelected(priority)
                        expanded = false
                    },
                    text = { Text(priority.name) }
                )
            }
        }
    }
}

@Composable
fun TimeDatePicker(
    value: String,
    onValueChange: (Date) -> Unit,
    textColor: Color
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onValueChange(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .clickable { datePickerDialog.show() }
                .padding(8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                color = textColor
            )
        }
    }
}
