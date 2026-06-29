package com.octosync.selfimprove;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class HabitRepository {

    private HabitDao habitDao;
    private LiveData<List<Habit>> allHabits;

    public HabitRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        habitDao = db.habitDao();
        allHabits = habitDao.getAllHabits();
    }

    public LiveData<List<Habit>> getAllHabits() {
        return allHabits;
    }

    public void insert(Habit habit) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            habitDao.insert(habit);
        });
    }

    public void update(Habit habit) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            habitDao.update(habit);
        });
    }

    public void delete(Habit habit) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            habitDao.delete(habit);
        });
    }
}