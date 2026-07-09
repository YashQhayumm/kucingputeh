package com.example.kucingputeh;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.model.User;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.SharedPrefManager;
import com.example.kucingputeh.remote.RideService;

import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRideActivity extends AppCompatActivity {

    private EditText etOrigin, etDestination, etDepartureTime, etAvailableSeats;
    private Button btnPublishRide;

    private RideService rideService;
    private SharedPrefManager spm;

    private final Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride);

        spm = new SharedPrefManager(getApplicationContext());
        rideService = ApiUtils.getRideService();

        etOrigin = findViewById(R.id.etOrigin);
        etDestination = findViewById(R.id.etDestination);
        etDepartureTime = findViewById(R.id.etDepartureTime);
        etAvailableSeats = findViewById(R.id.etAvailableSeats);
        btnPublishRide = findViewById(R.id.btnPublishRide);

        etDepartureTime.setOnClickListener(v -> showDateTimePicker());
        btnPublishRide.setOnClickListener(v -> validateAndPublish());
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                selectedDateTime.set(Calendar.SECOND, 0);

                String formatted = String.format(Locale.getDefault(),
                        "%04d-%02d-%02d %02d:%02d:00",
                        selectedDateTime.get(Calendar.YEAR),
                        selectedDateTime.get(Calendar.MONTH) + 1,
                        selectedDateTime.get(Calendar.DAY_OF_MONTH),
                        selectedDateTime.get(Calendar.HOUR_OF_DAY),
                        selectedDateTime.get(Calendar.MINUTE));

                etDepartureTime.setText(formatted);
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void validateAndPublish() {
        String origin = etOrigin.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String departureTime = etDepartureTime.getText().toString().trim();
        String seatsInput = etAvailableSeats.getText().toString().trim();

        if (origin.isEmpty()) {
            etOrigin.setError("Origin is required");
            return;
        }
        if (destination.isEmpty()) {
            etDestination.setError("Destination is required");
            return;
        }
        if (departureTime.isEmpty()) {
            Toast.makeText(this, "Please select a departure date and time.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (seatsInput.isEmpty()) {
            etAvailableSeats.setError("Available seats is required");
            return;
        }

        int availableSeats;
        try {
            availableSeats = Integer.parseInt(seatsInput);
        } catch (NumberFormatException e) {
            etAvailableSeats.setError("Enter a valid number of seats");
            return;
        }
        if (availableSeats <= 0) {
            etAvailableSeats.setError("Must offer at least 1 seat");
            return;
        }

        User user = spm.getUser();
        if (user == null || user.getId() <= 0) {
            Toast.makeText(this, "You must be logged in to create a ride.", Toast.LENGTH_SHORT).show();
            return;
        }

        publishRide(user.getId(), origin, destination, departureTime, availableSeats);
    }

    private void publishRide(int driverId, String origin, String destination, String departureTime, int availableSeats) {
        btnPublishRide.setEnabled(false);

        // Based on the REST test parameters, we send total_seats, available_seats, and status
        rideService.createRide(driverId, origin, destination, departureTime, availableSeats, availableSeats, "available")
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        btnPublishRide.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(CreateRideActivity.this, "Ride published successfully!", Toast.LENGTH_LONG).show();
                            
                            // Return to MainActivity
                            Intent intent = new Intent(CreateRideActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            String serverMessage = "";
                            try {
                                if (response.errorBody() != null) {
                                    serverMessage = response.errorBody().string();
                                }
                            } catch (java.io.IOException e) {
                                Log.e("CREATE_RIDE", "Could not read error body", e);
                            }
                            Log.e("CREATE_RIDE", "Server returned code: " + response.code() + " body: " + serverMessage);
                            Toast.makeText(CreateRideActivity.this,
                                    "Failed to publish ride (" + response.code() + "): " +
                                            (serverMessage.isEmpty() ? "no details from server" : serverMessage),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        btnPublishRide.setEnabled(true);
                        Log.e("CREATE_RIDE", "Network error", t);
                        Toast.makeText(CreateRideActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}