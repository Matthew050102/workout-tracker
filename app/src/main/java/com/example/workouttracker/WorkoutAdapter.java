package com.example.workouttracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;

    public WorkoutAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.textViewName.setText(workout.getName());
        holder.textViewReps.setText("Ismétlés: " + workout.getReps());  // Itt van az ismétlés szám
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewReps;  // Az ismétlés számot ide rendeljük

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewReps = itemView.findViewById(R.id.textViewReps);  // Ismétlés szám megjelenítése
        }
    }
}
