package com.example.workouttracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddWorkoutActivity extends AppCompatActivity {

    private EditText editTextWorkoutName, editTextWorkoutReps;  // Az ismétlés számát kérdezzük
    private Button buttonSaveWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        editTextWorkoutName = findViewById(R.id.editTextWorkoutName);
        editTextWorkoutReps = findViewById(R.id.editTextWorkoutReps);

        buttonSaveWorkout = findViewById(R.id.buttonSaveWorkout);

        buttonSaveWorkout.setOnClickListener(v -> saveWorkout());
    }

    private void saveWorkout() {
        String name = editTextWorkoutName.getText().toString();
        String reps = editTextWorkoutReps.getText().toString();  // Most ismétlés számot kérünk
        if (!name.isEmpty() && !reps.isEmpty()) {
            // Visszaküldjük az adatokat (ismétlés szám)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", name);
            resultIntent.putExtra("reps", reps);  // Ismétlés szám
            setResult(RESULT_OK, resultIntent);
            finish();  // Bezárjuk az Activity-t és visszaadjuk az adatokat
        } else {
            Toast.makeText(AddWorkoutActivity.this, "Kérlek, töltsd ki a mezőket!", Toast.LENGTH_SHORT).show();
        }
    }
}
