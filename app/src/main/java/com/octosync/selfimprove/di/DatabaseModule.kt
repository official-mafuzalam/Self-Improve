package com.octosync.selfimprove.di

import android.content.Context
import com.octosync.selfimprove.AppDatabase
import com.octosync.selfimprove.HabitDao
import com.octosync.selfimprove.HabitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideHabitDao(appDatabase: AppDatabase): HabitDao {
        return appDatabase.habitDao()
    }

    @Provides
    @Singleton
    fun provideHabitRepository(habitDao: HabitDao): HabitRepository {
        return HabitRepository(habitDao)
    }
}
