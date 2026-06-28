package com.octosync.selfimprove;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private final List<Habit> habits;

    public HabitAdapter(List<Habit> habits) {
        this.habits = habits;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.tvHabitName.setText(habit.getNameResId());

        // Reset listeners to avoid triggering on state restoration
        holder.toggleGroup.clearOnButtonCheckedListeners();
        
        // Set checked state based on habit status
        if (habit.getStatus() == Habit.Status.SUCCESS) {
            holder.toggleGroup.check(R.id.btnSuccess);
        } else if (habit.getStatus() == Habit.Status.FAILED) {
            holder.toggleGroup.check(R.id.btnFailed);
        } else {
            holder.toggleGroup.clearChecked();
        }

        holder.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnSuccess) {
                    habit.setStatus(Habit.Status.SUCCESS);
                } else if (checkedId == R.id.btnFailed) {
                    habit.setStatus(Habit.Status.FAILED);
                }
            } else {
                // If the currently checked button is unchecked and no other button is checked
                if (group.getCheckedButtonId() == View.NO_ID) {
                    habit.setStatus(Habit.Status.PENDING);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView tvHabitName;
        MaterialButtonToggleGroup toggleGroup;
        MaterialButton btnSuccess, btnFailed;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHabitName = itemView.findViewById(R.id.tvHabitName);
            toggleGroup = itemView.findViewById(R.id.toggleHabitStatus);
            btnSuccess = itemView.findViewById(R.id.btnSuccess);
            btnFailed = itemView.findViewById(R.id.btnFailed);
        }
    }
}