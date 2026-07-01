package com.example.kucingputeh;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kucingputeh.adapter.BookingAdapter;
import com.example.kucingputeh.model.Booking;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.BookingService;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private BookingService bookingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        // Initialize view references
        rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        bookingService = ApiUtils.getBookingService();

        // CHANGED: Query for passenger ID 3, since that's the one we linked to Ride ID 2!
        fetchUserBookings(3);
    }

    private void fetchUserBookings(int passengerId) {
        // 1. Construct the filter map required by the new BookingService interface
        Map<String, String> filters = new HashMap<>();
        filters.put("passenger_id", String.valueOf(passengerId));

        // 2. Pass the filters map into viewBookings()
        bookingService.viewBookings(filters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                // pRESTige returns 200 OK when matching data is found.
                // If it returns 204, it means the query executed perfectly but found 0 matches.
                if (response.isSuccessful() && response.body() != null && response.code() != 204) {
                    try {
                        String jsonResponse = response.body().string();

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Booking>>() {}.getType();
                        List<Booking> fetchedBookings = gson.fromJson(jsonResponse, listType);

                        bookingList.clear();
                        if (fetchedBookings != null && !fetchedBookings.isEmpty()) {
                            bookingList.addAll(fetchedBookings);

                            // Initialize adapter with your real live records
                            adapter = new BookingAdapter(bookingList);
                            rvBookings.setAdapter(adapter);
                        } else {
                            clearUIAndShowEmpty();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // This handles empty database returns (204) and server errors safely
                    clearUIAndShowEmpty();

                    try {
                        String errorContent = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.d("SERVER_RESPONSE_LOG", "HTTP Code: " + response.code() + " | Content: " + errorContent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearUIAndShowEmpty() {
        bookingList.clear();
        adapter = new BookingAdapter(bookingList);
        rvBookings.setAdapter(adapter);
        Toast.makeText(MainActivity.this, "No active bookings found.", Toast.LENGTH_SHORT).show();
    }
}