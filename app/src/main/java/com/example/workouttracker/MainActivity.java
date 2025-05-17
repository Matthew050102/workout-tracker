package com.example.workouttracker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.Manifest;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private WorkoutAdapter adapter;
    private final List<Workout> workouts = new ArrayList<>();
    private CollectionReference workoutsRef;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "workout_channel",
                    "Workout Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200);
            }
        }



        recycler = findViewById(R.id.recyclerViewWorkouts);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WorkoutAdapter(this, workouts,
                workout -> {
                    Intent intent = new Intent(MainActivity.this, AddWorkoutActivity.class);
                    intent.putExtra("isUpdate", true);
                    intent.putExtra("id", workout.getId());
                    intent.putExtra("name", workout.getName());
                    intent.putExtra("reps", workout.getReps());
                    startActivityForResult(intent, 102);
                },
                this::deleteWorkout);

        recycler.setAdapter(adapter);

        workoutsRef = FirebaseFirestore.getInstance().collection("workouts");

        loadWorkouts();
        scheduleDailyWorkoutReminder();

        Button addBtn = findViewById(R.id.buttonAddWorkout);
        addBtn.setOnClickListener(v -> {
            Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
            v.startAnimation(scaleUp);

            addBtn.postDelayed(() -> {
                Intent i = new Intent(this, AddWorkoutActivity.class);
                startActivityForResult(i, 101);
            }, 150);
        });


        BottomNavigationView nav = findViewById(R.id.bottomNavigationView);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return true;
        });
    }

    private void loadWorkouts() {
        listenerRegistration = workoutsRef.whereEqualTo("userId",
                        FirebaseAuth.getInstance().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) { return; }
                    workouts.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        Workout w = d.toObject(Workout.class);
                        w.setId(d.getId());
                        workouts.add(w);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("XD", "Lefutott: onResume()");
        loadWorkouts();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("XD", "Lefutott: onPause()");
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }



    @Override
    protected void onActivityResult(int c, int r, @Nullable Intent data) {
        super.onActivityResult(c, r, data);
        if (c == 101 && r == RESULT_OK && data != null) {
            String name = data.getStringExtra("name");
            String reps = data.getStringExtra("reps");
            Double lat = null;
            Double lng = null;
            if (data.hasExtra("latitude") && data.hasExtra("longitude")) {
                lat = data.getDoubleExtra("latitude", 0.0);
                lng = data.getDoubleExtra("longitude", 0.0);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("reps", reps);
            if (lat != null) {
                map.put("latitude", lat);
                map.put("longitude", lng);
            }
            map.put("userId", FirebaseAuth.getInstance().getUid());
            map.put("timestamp", FieldValue.serverTimestamp());

            workoutsRef.add(map)
                    .addOnSuccessListener(doc ->
                            Toast.makeText(this,"Mentve",Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(err ->
                            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show());
        }
        else if (c == 102 && r == RESULT_OK && data != null) {
            String id   = data.getStringExtra("id");
            String name = data.getStringExtra("name");
            String reps = data.getStringExtra("reps");

            Map<String, Object> update = new HashMap<>();
            update.put("name", name);
            update.put("reps", reps);

            if (data.hasExtra("latitude") && data.hasExtra("longitude")) {
                update.put("latitude", data.getDoubleExtra("latitude", 0.0));
                update.put("longitude", data.getDoubleExtra("longitude", 0.0));
            }


            workoutsRef.document(id)
                    .update(update)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Frissítve!", Toast.LENGTH_SHORT).show());
        }
    }

    private void deleteWorkout(Workout w) {
        new AlertDialog.Builder(this)
                .setTitle("Törlés")
                .setMessage("Biztosan törölni szeretnéd ezt az edzést?")
                .setPositiveButton("Igen", (dialog, which) -> {
                    workoutsRef.document(w.getId()).delete()
                            .addOnSuccessListener(unused -> {
                                workouts.remove(w);
                                adapter.notifyDataSetChanged();
                            });
                })
                .setNegativeButton("Mégsem", null)
                .show();
    }

    public void scheduleDailyWorkoutReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                alarmIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Értesítési engedély megadva", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Értesítési engedély megtagadva, nem lesznek értesítések", Toast.LENGTH_LONG).show();
            }
        }
    }


}
