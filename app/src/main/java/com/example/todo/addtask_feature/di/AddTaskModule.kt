package com.example.todo.addtask_feature.di

import com.example.todo.addtask_feature.domain.AddTaskUseCase
import com.example.todo.addtask_feature.domain.DeleteTaskUseCase
import com.example.todo.addtask_feature.domain.GetTaskUseCase
import com.example.todo.core.domain.repo.TaskRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideAddTaskUseCase(
        taskRepository: TaskRepo
    ): AddTaskUseCase {
        return AddTaskUseCase(taskRepository)
    }

    @Provides
    fun provideGetTaskUseCase(
        taskRepository: TaskRepo
    ): GetTaskUseCase {
        return GetTaskUseCase(taskRepository)
    }

    @Provides
    fun provideDeleteTaskUseCase(
        taskRepository: TaskRepo
    ): DeleteTaskUseCase {
        return DeleteTaskUseCase(taskRepository)
    }
}

