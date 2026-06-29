package com.octosync.selfimprove.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.octosync.selfimprove.Habit
import com.octosync.selfimprove.HabitRepository
import com.octosync.selfimprove.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: HabitRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000)
        val habits = repository.getHabitsForDate(today).first()
        
        val pendingCount = habits.count { it.status == Habit.Status.PENDING }
        
        if (pendingCount > 0) {
            sendNotification(pendingCount)
        }
        
        return Result.success()
    }

    private fun sendNotification(count: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "habit_reminder"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Habit Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_check) // Using ic_check for now
            .setContentTitle("Daily Habit Reminder")
            .setContentText("You have $count habits remaining for today. Don't give up!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
            
        notificationManager.notify(1, notification)
    }
}
