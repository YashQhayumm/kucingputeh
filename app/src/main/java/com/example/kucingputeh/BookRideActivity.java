package com.example.kucingputeh;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.model.Booking;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.BookingService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRideActivity extends AppCompatActivity {

    private TextView tvSelectedRide;
    private EditText etSeats;
    private Button btnConfirmBooking;

    private BookingService bookingService;

    // Live session identifiers (In production, replace with shared preferences logged-in user data)
    private final int currentPassengerId = 1;
    private final int currentRideId = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ride);

        // Initialize API service endpoints link
        bookingService = ApiUtils.getBookingService();

        tvSelectedRide = findViewById(R.id.tvSelectedRide);
        etSeats = findViewById(R.id.etSeats);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        btnConfirmBooking.setOnClickListener(v -> {
            String seatsInput = etSeats.getText().toString().trim();

            if (seatsInput.isEmpty()) {
                Toast.makeText(this, "Please enter how many seats you want!", Toast.LENGTH_SHORT).show();
                return;
            }

            int seatsRequested = Integer.parseInt(seatsInput);
            if (seatsRequested <= 0) {
                Toast.makeText(this, "Must book at least 1 seat.", Toast.LENGTH_SHORT).show();
                return;
            }

            // HIT THE DATABASE LIVE
            sendBookingToDatabase(currentPassengerId, currentRideId, seatsRequested);
        });
    }

    private void sendBookingToDatabase(int passengerId, int rideId, int seats) {
        // 1. Call your actual method from the interface passing the fields directly
        bookingService.bookRide(passengerId, rideId, seats).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookRideActivity.this, "Booking saved directly to database!", Toast.LENGTH_LONG).show();
                    finish(); // Go back to main screen
                } else {
                    Log.e("DB_ERROR", "Server returned error code: " + response.code());
                    Toast.makeText(BookRideActivity.this, "Failed saving booking to server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Log.e("NETWORK_ERROR", t.getMessage(), t);
                Toast.makeText(BookRideActivity.this, "Network failure. Check your connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}