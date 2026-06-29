package com.octosync.selfimprove.api

import com.octosync.selfimprove.Habit
import retrofit2.http.Body
import retrofit2.http.POST

interface HabitApiService {
    @POST("sync")
    suspend fun syncHabits(@Body habits: List<Habit>)
}
