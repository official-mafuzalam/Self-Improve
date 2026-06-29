package com.octosync.selfimprove;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private HabitViewModel habitViewModel;
    private HabitAdapter habitAdapter;

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
                    systemBars.top + 16,
                    mainContent.getPaddingRight(),
                    systemBars.bottom + 16
            );
            return insets;
        });

        setupRecyclerView();

        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        habitViewModel.getAllHabits().observe(this, habits -> {
            if (habits.isEmpty()) {
                seedDefaultHabits();
            }
            habitAdapter.setHabits(habits);
        });

        FloatingActionButton fab = findViewById(R.id.fabAddHabit);
        fab.setOnClickListener(v -> showAddHabitDialog());
    }

    private void setupRecyclerView() {
        RecyclerView rvHabits = findViewById(R.id.rvHabits);
        rvHabits.setLayoutManager(new LinearLayoutManager(this));
        habitAdapter = new HabitAdapter(habit -> habitViewModel.update(habit));
        rvHabits.setAdapter(habitAdapter);
    }

    private void seedDefaultHabits() {
        String[] defaults = {
                "No Porn", "No Sugar", "No Fast Food",
                "7 Hrs Sleep", "2 KM Running", "Workout at Home",
                "Cold Shower", "Study > 1 Hr", "Screen Time < 3 Hrs"
        };
        for (String name : defaults) {
            habitViewModel.insert(new Habit(name));
        }
    }

    private void showAddHabitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_habit, null);
        EditText etHabitName = dialogView.findViewById(R.id.etDialogHabitName);

        builder.setView(dialogView)
                .setTitle(R.string.new_goal)
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    String name = etHabitName.getText().toString().trim();
                    if (!TextUtils.isEmpty(name)) {
                        habitViewModel.insert(new Habit(name));
                    } else {
                        Toast.makeText(this, R.string.enter_name_toast, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        builder.create().show();
    }
}