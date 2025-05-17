package com.example.workouttracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import java.util.List;


public class AddWorkoutActivity extends AppCompatActivity {

    private EditText nameEt, repsEt;
    private String workoutId;
    private boolean isUpdate = false;
    private static final int REQUEST_LOCATION_PERMISSION = 300;
    private FusedLocationProviderClient fusedLocationClient;
    private Double currentLat = null;
    private Double currentLng = null;


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_workout);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button locationBtn = findViewById(R.id.buttonGetLocation);
        locationBtn.setOnClickListener(v -> checkLocationPermission());

        nameEt = findViewById(R.id.editTextWorkoutName);
        repsEt = findViewById(R.id.editTextWorkoutReps);
        Button save = findViewById(R.id.buttonSaveWorkout);

        isUpdate = getIntent().getBooleanExtra("isUpdate", false);
        if (isUpdate) {
            workoutId = getIntent().getStringExtra("id");
            nameEt.setText(getIntent().getStringExtra("name"));
            repsEt.setText(getIntent().getStringExtra("reps"));

            if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
                currentLat = getIntent().getDoubleExtra("latitude", 0.0);
                currentLng = getIntent().getDoubleExtra("longitude", 0.0);
            }
        }

        save.setOnClickListener(v -> {
            String n = nameEt.getText().toString().trim();
            String r = repsEt.getText().toString().trim();

            if (n.isEmpty() || r.isEmpty()) {
                Toast.makeText(this, "Minden mezőt tölts ki!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent();
            i.putExtra("name", n);
            i.putExtra("reps", r);

            if (currentLat != null && currentLng != null) {
                i.putExtra("latitude", currentLat);
                i.putExtra("longitude", currentLng);
            }

            if (isUpdate) {
                i.putExtra("isUpdate", true);
                i.putExtra("id", workoutId);
            }

            sendWorkoutSavedNotification();

            setResult(RESULT_OK, i);
            finish();
        });

    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Helymeghatározás engedély szükséges!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();

                        Toast.makeText(this, "Helyzet: " + currentLat + ", " + currentLng,
                                Toast.LENGTH_LONG).show();

                        Geocoder geocoder = new Geocoder(this);
                        try {
                            List<Address> addresses = geocoder.getFromLocation(currentLat, currentLng, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String fullAddress = address.getAddressLine(0);
                                Toast.makeText(this, "Cím: " + fullAddress, Toast.LENGTH_LONG).show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Nem sikerült címet lekérni", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "Helyzet nem elérhető", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendWorkoutSavedNotification() {
        String channelId = "workout_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Workout Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Edzés elmentve")
                .setContentText("Sikeresen elmentetted az edzést!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }


}
