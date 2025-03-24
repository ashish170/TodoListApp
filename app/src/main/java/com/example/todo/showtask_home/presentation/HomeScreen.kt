package com.example.todo.showtask_home.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todo.R
import com.example.todo.core.data.FilterOption
import com.example.todo.core.data.Priority
import com.example.todo.core.data.SortOption
import com.example.todo.core.domain.model.TaskModel
import com.example.todo.navigation.Routes
import com.example.todo.util.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: ShowTasksViewModel = hiltViewModel(),
    navigateNext: (String) -> Unit = {},
) {
    val tasks by remember { derivedStateOf { viewModel.filteredAndSortedTasks } }
    val tasksList = viewModel.tasksList
    val completedTasks = viewModel.completedTasksList

    var selectedFilter by remember { mutableStateOf(FilterOption.ALL) }
    var selectedSort by remember { mutableStateOf(SortOption.PRIORITY) }
    var filterExpanded by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }

    var reorderableTaskList by remember { mutableStateOf(tasks) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(tasks) {
        reorderableTaskList = tasks
    }

    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val onTertiaryColor = MaterialTheme.colorScheme.onTertiary

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ShowTasksEvent.NavigateNext -> navigateNext(event.route)
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surfaceColor)
                    .padding(horizontal = 16.dp, vertical = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = onSurfaceColor,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            Log.i("Ash clicked settings", "Settings clicked")
                            navigateNext(Routes.SETTINGS_SCREEN)
                        }
                )
            }
        },
        floatingActionButton = {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale = remember { Animatable(1f) }

            LaunchedEffect(isPressed) {
                if (isPressed) {
                    scale.animateTo(
                        targetValue = 1.3f,
                        animationSpec = keyframes {
                            durationMillis = 300
                            1.1f at 50
                            1.3f at 100
                            0.9f at 150
                            1f at 300
                        }
                    )
                }
            }

            FloatingActionButton(
                modifier = Modifier
                    .semantics {
                        contentDescription = "Add New Task"
                    }
                    .scale(scale.value),
                onClick = {
                    viewModel.action(HomeAction.AddNewTask)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                containerColor = tertiaryColor,
                interactionSource = interactionSource
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = null,
                    tint = onTertiaryColor,
                )
            }
        }
    ) { paddingValues ->
        if (tasksList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "No Tasks",
                        modifier = Modifier.size(200.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tasks yet!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceColor
                    )
                    Text(
                        text = "Start adding your tasks to stay on top of things.",
                        fontSize = 16.sp,
                        color = onSurfaceColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
        else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        TaskProgressIndicator(
                            completedTasks = completedTasks.size,
                            totalTasks = tasks.size
                        )

                        Text(
                            text = "My Tasks",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = onSurfaceColor,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Text(
                        text = "${completedTasks.size} of ${tasks.size} task${if (tasks.size != 1) "s" else ""}",
                        fontSize = 18.sp,
                        color = onSurfaceColor.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = onSurfaceColor.copy(alpha = 0.1f))
                }

                Row(
                    Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_filter_alt_24),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                filterExpanded = true
                            },
                        contentDescription = null,
                        tint = onSurfaceColor,
                    )
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_sort_24),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                sortExpanded = true
                            },
                        contentDescription = null,
                        tint = onSurfaceColor,
                    )

                    if (isDragging) {
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "Drag to reorder",
                            color = primaryColor,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }
                }
                Box(modifier = Modifier.padding(start = 16.dp), contentAlignment = Alignment.TopStart)
                {
                    FilterAndSortControls(
                        filterExpanded = filterExpanded,
                        onFilterDismissRequest = { filterExpanded = false },
                        onFilterChange = { filterOption ->
                            selectedFilter = filterOption
                            filterExpanded = false
                            viewModel.updateFilter(filterOption)
                        },
                        sortExpanded = sortExpanded,
                        onSortDismissRequest = { sortExpanded = false },
                        onSortChange = { sortOption ->
                            selectedSort = sortOption
                            sortExpanded = false
                            viewModel.updateSort(sortOption)
                        }
                    )
                }

                ReorderableLazyColumn(
                    items = reorderableTaskList,
                    onMove = { fromIndex, toIndex ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        reorderableTaskList = reorderableTaskList.toMutableList().apply {
                            add(toIndex, removeAt(fromIndex))
                        }
                    },
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        viewModel.action(HomeAction.ReorderTasks(reorderableTaskList.map { it.id }))
                    },
                    modifier = Modifier
                        .fillMaxSize()
                ) { item, isDragging ->
                    TaskItem(
                        task = item,
                        isDragging = isDragging,
                        onItemClick = { viewModel.action(HomeAction.ListItemOnClick(item.id)) },
                        onCheckChange = { isCompleted ->
                            viewModel.action(HomeAction.ToggleTaskCompletion(item.id, isCompleted))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReorderableLazyColumn(
    items: List<TaskModel>,
    onMove: (Int, Int) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (TaskModel, Boolean) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var draggedItem by remember { mutableStateOf<TaskModel?>(null) }
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var currentIndexOfDraggedItem by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        itemsIndexed(items) { index, item ->
            val animDelay = 50 * index
            var itemVisible by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = item.id) {
                delay(animDelay.toLong())
                itemVisible = true
            }

            AnimatedVisibility(
                visible = itemVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                val isDragging = draggedItem == item
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .pointerInput(item.id) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    draggedItem = item
                                    draggedItemIndex = index
                                    currentIndexOfDraggedItem = index
                                    onDragStart()
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()

                                    val scrollThreshold = 64.dp.toPx()
                                    val canScrollForward = lazyListState.canScrollForward
                                    val canScrollBackward = lazyListState.canScrollBackward

                                    if (dragAmount.y < 0 && canScrollBackward && currentIndexOfDraggedItem != 0) {
                                        if (change.position.y < scrollThreshold) {
                                            scope.launch {
                                                lazyListState.scrollBy(-dragAmount.y * 2)
                                            }
                                        }
                                    } else if (dragAmount.y > 0 && canScrollForward && currentIndexOfDraggedItem != items.size - 1) {
                                        if (change.position.y > size.height - scrollThreshold) {
                                            scope.launch {
                                                lazyListState.scrollBy(-dragAmount.y * 2)
                                            }
                                        }
                                    }

                                    val hitItemInfo = lazyListState.layoutInfo.visibleItemsInfo.find { itemInfo ->
                                        val itemTop = itemInfo.offset.toFloat()
                                        val itemBottom = itemTop + itemInfo.size

                                        change.position.y >= itemTop && change.position.y <= itemBottom
                                    }

                                    hitItemInfo?.let { hitItem ->
                                        val targetIndex = hitItem.index
                                        if (targetIndex != currentIndexOfDraggedItem && draggedItemIndex != null) {
                                            onMove(draggedItemIndex!!, targetIndex)
                                            currentIndexOfDraggedItem = targetIndex
                                            draggedItemIndex = targetIndex
                                        }
                                    }
                                },
                                onDragEnd = {
                                    draggedItem = null
                                    draggedItemIndex = null
                                    currentIndexOfDraggedItem = null
                                    onDragEnd()
                                },
                                onDragCancel = {
                                    draggedItem = null
                                    draggedItemIndex = null
                                    currentIndexOfDraggedItem = null
                                    onDragEnd()
                                }
                            )
                        }
                ) {
                    itemContent(item, isDragging)
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskModel,
    isDragging: Boolean,
    onItemClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    val elevatedSurfaceColor = MaterialTheme.colorScheme.surfaceVariant


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics {
                testTag = "Task Item"
                contentDescription = task.id.toString()
            }
            .clickable { onItemClick() }
            .graphicsLayer(
                scaleX = if (isDragging) 1.05f else 1f,
                scaleY = if (isDragging) 1.05f else 1f,
                shadowElevation = if (isDragging) 8f else 0f
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 4.dp else 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) elevatedSurfaceColor else surfaceColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckChange,
                modifier = Modifier.size(32.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = primaryColor,
                    uncheckedColor = onSurfaceColor.copy(alpha = 0.5f),
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = if (task.isCompleted) onSurfaceColor.copy(alpha = 0.6f) else onSurfaceColor,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = "${task.priority}",
                        color = when (task.priority) {
                            Priority.LOW -> Color(0xFF4285F4)
                            Priority.MEDIUM -> Color.Magenta
                            Priority.HIGH -> Color.Red
                        },
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.description,
                        fontSize = 16.sp,
                        color = if (task.isCompleted) onSurfaceColor.copy(alpha = 0.6f) else onSurfaceColor.copy(alpha = 0.8f),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .weight(1f),
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    task.dueDate?.let {
                        Text(
                            text = Utils.formatDate(it),
                            fontSize = 14.sp,
                            color = onSurfaceColor.copy(alpha = 0.6f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterAndSortControls(
    filterExpanded: Boolean,
    onFilterDismissRequest: () -> Unit,
    onFilterChange: (FilterOption) -> Unit,
    sortExpanded: Boolean,
    onSortDismissRequest: () -> Unit,
    onSortChange: (SortOption) -> Unit,
) {
    DropdownMenu(
        expanded = filterExpanded,
        onDismissRequest = onFilterDismissRequest,
    ) {
        DropdownMenuItem(text = {
            Text(text = "All Tasks")
        }, onClick = { onFilterChange(FilterOption.ALL) })
        DropdownMenuItem(text = {
            Text(text = "Completed")
        }, onClick = { onFilterChange(FilterOption.COMPLETED) })
        DropdownMenuItem(text = {
            Text(text = "Pending")
        }, onClick = { onFilterChange(FilterOption.PENDING) })
    }

    DropdownMenu(
        expanded = sortExpanded,
        onDismissRequest = onSortDismissRequest,
    ) {
        DropdownMenuItem(text = {
            Text(text = "By Priority")
        }, onClick = { onSortChange(SortOption.PRIORITY) })
        DropdownMenuItem(text = {
            Text(text = "By Date")
        }, onClick = { onSortChange(SortOption.DUE_DATE) })
        DropdownMenuItem(text = {
            Text(text = "Alphabetically")
        }, onClick = { onSortChange(SortOption.ALPHABETICAL) })
    }
}

@Composable
fun TaskProgressIndicator(
    completedTasks: Int,
    totalTasks: Int,
    size: Dp = 48.dp,
    thickness: Dp = 4.dp,
    animationDuration: Int = 1000
) {
    val percentage = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f

    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface

    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "progressAnimation"
    )

    val sweepAngle = 360f * animatedPercentage

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = Color.Transparent,
                radius = (size.toPx() - thickness.toPx()) / 2,
                center = center
            )
        }

        Canvas(modifier = Modifier.size(size)) {
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round),
                size = Size(size.toPx(), size.toPx()),
                topLeft = Offset(0f, 0f)
            )
        }

        Text(
            text = "${(animatedPercentage * 100).toInt()}%",
            color = textColor,
            fontSize = (size.value / 3.5).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
