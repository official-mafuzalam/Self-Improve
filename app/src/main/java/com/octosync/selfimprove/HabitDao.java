package com.octosync.selfimprove;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HabitDao {
    @Query("SELECT * FROM habits")
    LiveData<List<Habit>> getAllHabits();

    @Insert
    void insert(Habit habit);

    @Update
    void update(Habit habit);

    @Delete
    void delete(Habit habit);
}