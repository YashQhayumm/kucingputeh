package com.example.kucingputeh;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kucingputeh.adapter.BookingAdapter;
import com.example.kucingputeh.model.Booking;
import com.example.kucingputeh.model.User;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.BookingService;
import com.example.kucingputeh.remote.SharedPrefManager;
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

// This is the "Bookings" screen opened from the Dashboard's Bookings card
// (for admin/driver). It was previously pointed at activity_homepage --
// the pre-login welcome screen -- and referenced button IDs
// (btnChatWithDriver, btnUpdatePassengerProfile) that don't exist in that
// layout, which crashed onCreate() and dropped the user back to the
// welcome/login screen. Fixed to use activity_booking_list, which actually
// has the views this activity needs.
public class MainActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private BookingService bookingService;
    private SharedPrefManager spm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking_list);

        spm = new SharedPrefManager(getApplicationContext());

        findViewById(R.id.btnFindRides).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewAvailableRidesActivity.class)));

        Button btnCreateRide = findViewById(R.id.btnCreateRide);
        btnCreateRide.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CreateRideActivity.class)));

        findViewById(R.id.btnProfile).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        bookingService = ApiUtils.getBookingService();

        User user = spm.getUser();
        if (user != null) {
            boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase("admin");
            boolean isDriver = user.getRole() != null && user.getRole().equalsIgnoreCase("driver");

            // Passengers only get Find Rides, My Bookings, Profile, and their
            // own booking list here -- Create Ride is a driver/admin action.
            btnCreateRide.setVisibility((isAdmin || isDriver) ? View.VISIBLE : View.GONE);

            if (isAdmin || isDriver) {
                // Admin/driver Bookings view: show all bookings, not just
                // the current user's own passenger bookings.
                fetchBookings(new HashMap<>());
            } else {
                Map<String, String> filters = new HashMap<>();
                filters.put("passenger_id", String.valueOf(user.getId()));
                fetchBookings(filters);
            }
        }
    }

    private void fetchBookings(Map<String, String> filters) {
        bookingService.viewBookings(filters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null && response.code() != 204) {
                    try {
                        String jsonResponse = response.body().string();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Booking>>() {}.getType();
                        List<Booking> fetchedBookings = gson.fromJson(jsonResponse, listType);

                        bookingList.clear();
                        if (fetchedBookings != null && !fetchedBookings.isEmpty()) {
                            bookingList.addAll(fetchedBookings);
                            adapter = new BookingAdapter(bookingList);
                            rvBookings.setAdapter(adapter);
                        } else {
                            clearUIAndShowEmpty();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    clearUIAndShowEmpty();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearUIAndShowEmpty() {
        if (bookingList != null) {
            bookingList.clear();
            adapter = new BookingAdapter(bookingList);
            rvBookings.setAdapter(adapter);
        }
    }
}