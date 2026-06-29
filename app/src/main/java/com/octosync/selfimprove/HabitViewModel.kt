package com.octosync.selfimprove

import androidx.lifecycle.*
import com.octosync.selfimprove.data.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = settingsManager.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun toggleDarkMode(enabled: Boolean) = viewModelScope.launch {
        settingsManager.setDarkMode(enabled)
    }

    private val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000)

    val allHabits: StateFlow<List<Habit>> = repository.allHabits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayHabits: StateFlow<List<Habit>> = repository.getHabitsForDate(today).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val habitStreaks: StateFlow<Map<String, Int>> = allHabits
        .map { habits -> calculateStreaks(habits) }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val consistencyData: StateFlow<List<Float>> = allHabits
        .map { habits -> calculateConsistency(habits) }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val globalStreak: StateFlow<Int> = allHabits
        .map { habits -> calculateGlobalStreak(habits) }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private fun calculateGlobalStreak(habits: List<Habit>): Int {
        val groupedByDate = habits.groupBy { it.date }
        var streak = 0
        var checkDate = today
        
        while (true) {
            val dayHabits = groupedByDate[checkDate] ?: break
            if (dayHabits.all { it.status == Habit.Status.SUCCESS }) {
                streak++
                checkDate -= (24 * 60 * 60 * 1000)
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateConsistency(habits: List<Habit>): List<Float> {
        val groupedByDate = habits.groupBy { it.date }
        return (0..6).map { i ->
            val date = today - (i * 24 * 60 * 60 * 1000)
            val dayHabits = groupedByDate[date] ?: emptyList()
            val successCount = dayHabits.count { it.status == Habit.Status.SUCCESS }
            val totalCount = dayHabits.size
            if (totalCount > 0) successCount.toFloat() / totalCount else 0f
        }.reversed()
    }

    private fun calculateStreaks(allHabits: List<Habit>): Map<String, Int> {
        val streaks = mutableMapOf<String, Int>()
        val groupedByName = allHabits.groupBy { it.name }
        
        groupedByName.forEach { (name, habits) ->
            val sorted = habits.sortedByDescending { it.date }
            var currentStreak = 0
            var checkDate = today
            
            for (habit in sorted) {
                if (habit.date == checkDate) {
                    if (habit.status == Habit.Status.SUCCESS) {
                        currentStreak++
                        checkDate -= (24 * 60 * 60 * 1000)
                    } else if (habit.status == Habit.Status.FAILED) {
                        break
                    }
                    // If PENDING, we don't break the streak yet if it's today
                } else if (habit.date < checkDate) {
                    // Gap in days
                    break
                }
            }
            streaks[name] = currentStreak
        }
        return streaks
    }

    fun insert(habit: Habit) = viewModelScope.launch {
        repository.insert(habit)
    }

    fun update(habit: Habit) = viewModelScope.launch {
        repository.update(habit)
    }

    fun delete(habit: Habit) = viewModelScope.launch {
        repository.delete(habit)
    }
}
