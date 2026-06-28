package com.example.kucingputeh;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.model.Booking;

public class BookRideActivity extends AppCompatActivity {

    private TextView tvSelectedRide;
    private EditText etSeats;
    private Button btnConfirmBooking;

    // Fixed mock configurations matching our database rows
    private final int mockPassengerId = 1;
    private final int mockRideId = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ride);

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

            // SIMULATED OFFLINE BOOKING INSERTION
            executeMockBooking(mockPassengerId, mockRideId, String.valueOf(seatsRequested));
        });
    }

    private void executeMockBooking(int passengerId, int rideId, String seats) {
        // Form a brand new Booking model matching your exact 7-parameter constructor:
        // (BookingID, RideID, passenger_id, seats_booked, booking_status, Origin, Destination)
        Booking newMockBooking = new Booking(
                105,
                rideId,
                passengerId,
                seats,
                "confirmed",
                "Melaka Sentral",
                "KL Sentral"
        );

        // Display confirmation summary tracking matching SQL schema values
        String summary = "Successfully booked " + seats + " seat(s) on Ride " + rideId + "!";
        Toast.makeText(this, summary, Toast.LENGTH_LONG).show();

        // Terminate activity view and head back automatically
        finish();
    }
}