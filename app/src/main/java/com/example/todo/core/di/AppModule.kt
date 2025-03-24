package com.example.todo.core.di

import android.content.Context
import androidx.room.Room
import com.example.todo.core.DeleteAllTasksUseCase
import com.example.todo.core.ListenTaskUseCase
import com.example.todo.core.data.TaskAppDatabase
import com.example.todo.core.data.TaskRepoImpl
import com.example.todo.core.domain.repo.TaskRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): TaskAppDatabase {
        return Room.databaseBuilder(
            context,
            TaskAppDatabase::class.java,
            "TaskAppDatabase"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(appDatabase: TaskAppDatabase): TaskRepo {
        return TaskRepoImpl(appDatabase.taskDao())
    }

    @IODispatcher
    @Provides
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @MainDispatcher
    @Provides
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }

    @Provides
    @Singleton
    fun provideDeleteAllTasksUseCase(
        repository: TaskRepo
    ): DeleteAllTasksUseCase {
        return DeleteAllTasksUseCase(repository)
    }


    @Provides
    @Singleton
    fun provideListenTasksUseCase(repository: TaskRepo): ListenTaskUseCase {
        return ListenTaskUseCase(repository)
    }
}
