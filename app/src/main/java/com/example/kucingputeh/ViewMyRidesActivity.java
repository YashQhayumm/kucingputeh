package com.example.kucingputeh;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kucingputeh.adapter.MyRideAdapter;
import com.example.kucingputeh.model.Booking;
import com.example.kucingputeh.model.Ride;
import com.example.kucingputeh.model.User;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.BookingService;
import com.example.kucingputeh.remote.PrefManager;
import com.example.kucingputeh.remote.RideService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewMyRidesActivity extends AppCompatActivity {

    private RecyclerView rvMyRides;
    private TextView tvEmptyMyRides;

    private MyRideAdapter adapter;
    private final List<Ride> myRideList = new ArrayList<>();

    private RideService rideService;
    private BookingService bookingService;
    private PrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        spm = new PrefManager(getApplicationContext());
        rideService = ApiUtils.getRideService();
        bookingService = ApiUtils.getBookingService();

        rvMyRides = findViewById(R.id.rvMyRides);
        tvEmptyMyRides = findViewById(R.id.tvEmptyMyRides);

        rvMyRides.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRideAdapter(myRideList, this::showPassengersForRide);
        rvMyRides.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = spm.getUser();
        if (user != null && user.getId() > 0) {
            fetchMyRides(user.getId());
        }
    }

    private void fetchMyRides(int driverId) {
        rideService.getRidesByDriver(driverId).enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ride>> call, @NonNull Response<List<Ride>> response) {
                myRideList.clear();
                if (response.isSuccessful() && response.body() != null) {
                    myRideList.addAll(response.body());
                } else {
                    Log.e("MY_RIDES", "Server returned code: " + response.code());
                }
                adapter.notifyDataSetChanged();
                toggleEmptyState();
            }

            @Override
            public void onFailure(@NonNull Call<List<Ride>> call, @NonNull Throwable t) {
                Log.e("MY_RIDES", "Network error", t);
                Toast.makeText(ViewMyRidesActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                toggleEmptyState();
            }
        });
    }

    private void toggleEmptyState() {
        boolean isEmpty = myRideList.isEmpty();
        tvEmptyMyRides.setVisibility(isEmpty ? android.view.View.VISIBLE : android.view.View.GONE);
        rvMyRides.setVisibility(isEmpty ? android.view.View.GONE : android.view.View.VISIBLE);
    }

    // Fetches everyone who booked a given ride and shows them in a dialog
    private void showPassengersForRide(Ride ride) {
        Map<String, String> filters = new HashMap<>();
        filters.put("ride_id", String.valueOf(ride.getRideId()));

        bookingService.viewBookings(filters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null && response.code() != 204) {
                    try {
                        String json = response.body().string();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Booking>>() {}.getType();
                        List<Booking> bookings = gson.fromJson(json, listType);
                        displayPassengerDialog(ride, bookings);
                    } catch (IOException e) {
                        Log.e("MY_RIDES", "Failed to parse bookings", e);
                        Toast.makeText(ViewMyRidesActivity.this, "Failed to load passengers.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    displayPassengerDialog(ride, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("MY_RIDES", "Network error", t);
                Toast.makeText(ViewMyRidesActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayPassengerDialog(Ride ride, List<Booking> bookings) {
        StringBuilder message = new StringBuilder();

        if (bookings == null || bookings.isEmpty()) {
            message.append("No passengers have booked this ride yet.");
        } else {
            for (Booking booking : bookings) {
                message.append("Passenger ID: ").append(booking.getPassengerId())
                        .append("  •  Seats: ").append(booking.getSeatsBooked())
                        .append("  •  Status: ").append(booking.getBookingStatus())
                        .append("\n\n");
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(ride.getOrigin() + " ➔ " + ride.getDestination())
                .setMessage(message.toString().trim())
                .setPositiveButton("Close", null)
                .show();
    }
}
