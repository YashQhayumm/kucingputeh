package com.example.kucingputeh;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.model.Ride;
import com.example.kucingputeh.model.User;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.BookingService;
import com.example.kucingputeh.remote.SharedPrefManager;
import com.example.kucingputeh.remote.RideService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRideActivity extends AppCompatActivity {

    // Keys used when launching this activity from ViewAvailableRidesActivity
    public static final String EXTRA_RIDE_ID = "extra_ride_id";
    public static final String EXTRA_ORIGIN = "extra_origin";
    public static final String EXTRA_DESTINATION = "extra_destination";
    public static final String EXTRA_DEPARTURE_TIME = "extra_departure_time";
    public static final String EXTRA_AVAILABLE_SEATS = "extra_available_seats";

    private TextView tvSelectedRide;
    private EditText etSeats;
    private Button btnConfirmBooking;

    private BookingService bookingService;
    private RideService rideService;
    private SharedPrefManager spm;

    private int currentRideId;
    private int currentPassengerId;
    private int currentAvailableSeats;
    private String origin, destination, departureTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ride);

        spm = new SharedPrefManager(getApplicationContext());
        bookingService = ApiUtils.getBookingService();
        rideService = ApiUtils.getRideService();

        tvSelectedRide = findViewById(R.id.tvSelectedRide);
        etSeats = findViewById(R.id.etSeats);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        // Get the logged-in user's id so the booking is tied to the real account
        User user = spm.getUser();
        currentPassengerId = (user != null) ? user.getId() : -1;

        // Read the ride that was picked in ViewAvailableRidesActivity
        currentRideId = getIntent().getIntExtra(EXTRA_RIDE_ID, -1);
        origin = getIntent().getStringExtra(EXTRA_ORIGIN);
        destination = getIntent().getStringExtra(EXTRA_DESTINATION);
        departureTime = getIntent().getStringExtra(EXTRA_DEPARTURE_TIME);
        currentAvailableSeats = getIntent().getIntExtra(EXTRA_AVAILABLE_SEATS, 0);

        if (currentRideId == -1 || origin == null || destination == null) {
            Toast.makeText(this, "No ride selected.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvSelectedRide.setText(origin + " ➔ " + destination + "\nDeparture: " + departureTime
                + "\nSeats left: " + currentAvailableSeats);

        btnConfirmBooking.setOnClickListener(v -> {
            if (currentPassengerId == -1) {
                Toast.makeText(this, "You must be logged in to book a ride.", Toast.LENGTH_SHORT).show();
                return;
            }

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

            if (seatsRequested > currentAvailableSeats) {
                Toast.makeText(this, "Only " + currentAvailableSeats + " seat(s) left on this ride.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnConfirmBooking.setEnabled(false);
            confirmSeatsThenBook(currentPassengerId, currentRideId, seatsRequested);
        });
    }

    // Re-check the live seat count on the server right before booking, so two people
    // booking at the same moment can't both succeed off a stale number shown on screen.
    private void confirmSeatsThenBook(int passengerId, int rideId, int seatsRequested) {
        rideService.getRideById(rideId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int liveAvailableSeats = response.body().getAvailableSeats();
                    if (seatsRequested > liveAvailableSeats) {
                        btnConfirmBooking.setEnabled(true);
                        currentAvailableSeats = liveAvailableSeats;
                        Toast.makeText(BookRideActivity.this,
                                "Only " + liveAvailableSeats + " seat(s) left now. Please adjust.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    sendBookingToDatabase(passengerId, rideId, seatsRequested, liveAvailableSeats);
                } else {
                    // Couldn't verify live seat count; fall back to the value we already have
                    sendBookingToDatabase(passengerId, rideId, seatsRequested, currentAvailableSeats);
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Log.e("NETWORK_ERROR", "Could not re-check seat count, using cached value", t);
                sendBookingToDatabase(passengerId, rideId, seatsRequested, currentAvailableSeats);
            }
        });
    }

    private void sendBookingToDatabase(int passengerId, int rideId, int seats, int seatsBeforeBooking) {
        bookingService.bookRide(passengerId, rideId, seats).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookRideActivity.this, "Booking saved and seats updating...", Toast.LENGTH_LONG).show();
                    // Reduce the seats left on the ride to reflect this booking
                    updateRemainingSeats(rideId, seatsBeforeBooking - seats);
                } else {
                    Log.e("DB_ERROR", "Server returned error code: " + response.code());
                    Toast.makeText(BookRideActivity.this, "Failed saving booking to server.", Toast.LENGTH_SHORT).show();
                    btnConfirmBooking.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("NETWORK_ERROR", t.getMessage(), t);
                Toast.makeText(BookRideActivity.this, "Network failure. Check your connection!", Toast.LENGTH_SHORT).show();
                btnConfirmBooking.setEnabled(true);
            }
        });
    }

    private void updateRemainingSeats(int rideId, int newSeatCount) {
        rideService.updateAvailableSeats(rideId, newSeatCount).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "(could not read error body)";
                    }
                    Log.e("DB_ERROR", "Failed to update available_seats. HTTP " + response.code() + " - " + errorBody);
                } else {
                    Log.d("DB_OK", "available_seats updated to " + newSeatCount + " for ride " + rideId);
                }
                // Whether or not this succeeds, the booking itself was already saved, so just return
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("NETWORK_ERROR", "Could not update seat count", t);
                finish();
            }
        });
    }
}
