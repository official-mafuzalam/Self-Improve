package com.octosync.selfimprove

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit @JvmOverloads constructor(
    val name: String,
    var status: Status = Status.PENDING,
    val category: Category = Category.OTHER,
    val date: Long = System.currentTimeMillis() / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000), // Normalized to start of day
    val reminderTime: Long? = null, // Milliseconds from start of day
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    enum class Status {
        PENDING, SUCCESS, FAILED
    }

    enum class Category {
        PHYSICAL, MENTAL, PRODUCTIVITY, OTHER
    }
}
