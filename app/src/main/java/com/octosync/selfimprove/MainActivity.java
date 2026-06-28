package com.octosync.selfimprove;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvHabits;
    private List<Habit> habitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(R.id.main);
        View mainContent = findViewById(R.id.mainContent);

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            mainContent.setPadding(
                    mainContent.getPaddingLeft(),
                    systemBars.top + 16, // Original padding 16dp
                    mainContent.getPaddingRight(),
                    systemBars.bottom + 16 // Original padding 16dp
            );
            return insets;
        });

        initHabits();
        setupRecyclerView();
    }

    private void initHabits() {
        habitList = new ArrayList<>();
        habitList.add(new Habit(1, R.string.habit_no_porn));
        habitList.add(new Habit(2, R.string.habit_no_sugar));
        habitList.add(new Habit(3, R.string.habit_no_fast_food));
        habitList.add(new Habit(4, R.string.habit_7hrs_sleep));
        habitList.add(new Habit(5, R.string.habit_2km_running));
        habitList.add(new Habit(6, R.string.habit_workout));
        habitList.add(new Habit(7, R.string.habit_cold_shower));
        habitList.add(new Habit(8, R.string.habit_study));
        habitList.add(new Habit(9, R.string.habit_screen_time));
    }

    private void setupRecyclerView() {
        rvHabits = findViewById(R.id.rvHabits);
        rvHabits.setLayoutManager(new LinearLayoutManager(this));
        HabitAdapter habitAdapter = new HabitAdapter(habitList);
        rvHabits.setAdapter(habitAdapter);
    }
}