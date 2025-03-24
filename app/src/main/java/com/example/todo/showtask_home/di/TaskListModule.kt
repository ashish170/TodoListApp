package com.example.todo.showtask_home.di

import com.example.todo.core.domain.repo.TaskRepo
import com.example.todo.showtask_home.domain.GetAllTaskUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ShowTaskModule {
    @Provides
    fun provideGetTasksUseCase(repository: TaskRepo): GetAllTaskUseCase {
        return GetAllTaskUseCase(repository)
    }
}