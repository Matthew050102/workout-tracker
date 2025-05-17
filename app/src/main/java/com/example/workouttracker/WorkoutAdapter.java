package com.example.workouttracker;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class WorkoutAdapter
        extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    public interface OnWorkoutLongClick {
        void onLongClick(Workout workout);
    }

    private List<Workout> workoutList;
    private final OnWorkoutLongClick longClick;
    private final OnWorkoutClick clickListener;

    private final Context context;

    public WorkoutAdapter(Context context, List<Workout> workoutList, OnWorkoutClick clickListener, OnWorkoutLongClick longClick) {
        this.context = context;
        this.workoutList = workoutList;
        this.clickListener = clickListener;
        this.longClick = longClick;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout w = workoutList.get(position);
        holder.name.setText(w.getName());
        holder.reps.setText("Reps: " + w.getReps());

        if (w.getLatitude() != null && w.getLongitude() != null) {
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> addresses = geocoder.getFromLocation(w.getLatitude(), w.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    String address = addresses.get(0).getAddressLine(0);
                    holder.location.setText("Hely: " + address);
                } else {
                    holder.location.setText("Hely: nem található cím");
                }
            } catch (IOException e) {
                holder.location.setText("Hely: hiba történt");
            }
        } else {
            holder.location.setText("Hely: nincs adat");
        }

        holder.itemView.setOnClickListener(v -> clickListener.onClick(w));
        holder.itemView.setOnLongClickListener(v -> {
            longClick.onLongClick(w);
            return true;
        });
    }



    public interface OnWorkoutClick {
        void onClick(Workout workout);
    }

    @Override
    public int getItemCount() {
        return workoutList == null ? 0 : workoutList.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView name, reps, location;

        WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewName);
            reps = itemView.findViewById(R.id.textViewReps);
            location = itemView.findViewById(R.id.textViewLocation);
        }
    }

}
