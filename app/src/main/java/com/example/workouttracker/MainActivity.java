package com.example.workouttracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button buttonAddWorkout;
    private RecyclerView recyclerViewWorkouts;
    private WorkoutAdapter workoutAdapter;
    private ArrayList<Workout> workoutList;
    private View mainLayout;
    private Animation fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mainLayout = findViewById(R.id.mainLayout);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mainLayout.startAnimation(fadeIn);

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        buttonAddWorkout = findViewById(R.id.buttonAddWorkout);
        recyclerViewWorkouts = findViewById(R.id.recyclerViewWorkouts);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        workoutList = new ArrayList<>();
        workoutList.add(new Workout("Fekvenyomás", "5x100"));
        workoutList.add(new Workout("Guggolás", "100x9"));
        workoutList.add(new Workout("Deadlift", "900x1"));
        workoutAdapter = new WorkoutAdapter(workoutList);
        recyclerViewWorkouts.setAdapter(workoutAdapter);
        recyclerViewWorkouts.setLayoutManager(new LinearLayoutManager(this));

        buttonAddWorkout.setOnClickListener(v -> {
            startActivityForResult(new Intent(MainActivity.this, AddWorkoutActivity.class), 1);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_workouts) {
                return true; // Már itt vagyunk
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String name = data.getStringExtra("name");
            String reps = data.getStringExtra("reps");
            workoutList.add(new Workout(name, reps));
            workoutAdapter.notifyDataSetChanged();  // Frissítjük a RecyclerView-t
        }
    }
}
