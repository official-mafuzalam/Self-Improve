package com.octosync.selfimprove.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.octosync.selfimprove.HabitRepository
import com.octosync.selfimprove.api.HabitApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: HabitRepository,
    private val apiService: HabitApiService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val allHabits = repository.allHabits.first()
            // In a real app, you'd only sync changed habits
            // apiService.syncHabits(allHabits)
            Log.d("SyncWorker", "Successfully synced ${allHabits.size} habits (simulated)")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error syncing habits", e)
            Result.retry()
        }
    }
}
