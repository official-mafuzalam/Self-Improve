package com.octosync.selfimprove

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Habit::class], version = 4, exportSchema = false)
@TypeConverters(AppDatabase.Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "self_improve_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000)
                        val defaults = listOf(
                            "('7 Hrs Sleep', 'PHYSICAL', 'PENDING', $today)",
                            "('2 KM Running', 'PHYSICAL', 'PENDING', $today)",
                            "('Workout at Home', 'PHYSICAL', 'PENDING', $today)",
                            "('Cold Shower', 'PHYSICAL', 'PENDING', $today)",
                            "('Study > 1 Hr', 'MENTAL', 'PENDING', $today)",
                            "('Screen Time < 3 Hrs', 'PRODUCTIVITY', 'PENDING', $today)",
                            "('No Porn', 'MENTAL', 'PENDING', $today)",
                            "('No Sugar', 'PHYSICAL', 'PENDING', $today)",
                            "('No Fast Food', 'PHYSICAL', 'PENDING', $today)"
                        )
                        defaults.forEach { values ->
                            db.execSQL("INSERT INTO habits (name, category, status, date) VALUES $values")
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    class Converters {
        @TypeConverter
        fun fromString(value: String?): Habit.Status? {
            return value?.let { Habit.Status.valueOf(it) }
        }

        @TypeConverter
        fun statusToString(status: Habit.Status?): String? {
            return status?.name
        }

        @TypeConverter
        fun fromCategoryString(value: String?): Habit.Category? {
            return value?.let { Habit.Category.valueOf(it) }
        }

        @TypeConverter
        fun categoryToString(category: Habit.Category?): String? {
            return category?.name
        }
    }
}
