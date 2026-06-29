package com.octosync.selfimprove

import org.junit.Assert.assertEquals
import org.junit.Test

class HabitTest {
    @Test
    fun habit_initialization_isCorrect() {
        val habit = Habit("Exercise", Habit.Status.PENDING, Habit.Category.PHYSICAL)
        assertEquals("Exercise", habit.name)
        assertEquals(Habit.Status.PENDING, habit.status)
        assertEquals(Habit.Category.PHYSICAL, habit.category)
    }

    @Test
    fun habit_statusUpdate_isCorrect() {
        val habit = Habit("Exercise")
        habit.status = Habit.Status.SUCCESS
        assertEquals(Habit.Status.SUCCESS, habit.status)
    }
}
