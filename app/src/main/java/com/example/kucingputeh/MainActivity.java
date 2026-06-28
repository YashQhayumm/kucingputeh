package com.example.kucingputeh;

import android.os.Bundle;
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
import java.util.List;

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

        // COMMENTED OUT FOR TESTING: Bypassing the database network layer
        // fetchUserBookings(1);

        // Call mock data method directly instead
        loadMockBookings();
    }

    private void fetchUserBookings(int passengerId) {
        bookingService.viewBookings(passengerId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Booking>>() {}.getType();
                        List<Booking> fetchedBookings = gson.fromJson(jsonResponse, listType);

                        if (fetchedBookings != null && !fetchedBookings.isEmpty()) {
                            bookingList.clear();
                            bookingList.addAll(fetchedBookings);

                            adapter = new BookingAdapter(MainActivity.this, bookingList);
                            rvBookings.setAdapter(adapter);
                        } else {
                            Toast.makeText(MainActivity.this, "No active bookings found.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Server response failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadMockBookings() {
        // Matched perfectly to: (int, int, int, String, String, String, String)
        bookingList.add(new Booking(101, 201, 1, "2", "confirmed", "Melaka Sentral", "KL Sentral"));
        bookingList.add(new Booking(102, 202, 1, "1", "confirmed", "JB Sentral", "Larkin Sentral"));
        bookingList.add(new Booking(103, 203, 1, "4", "confirmed", "Penang Sentral", "Ipoh Amanjaya"));
        bookingList.add(new Booking(104, 204, 1, "2", "confirmed", "Terminal Bersepadu Selatan", "Kuantan Sentral"));

        // Attach adapter data straight to your user interface
        adapter = new BookingAdapter(MainActivity.this, bookingList);
        rvBookings.setAdapter(adapter);

        Toast.makeText(this, "Showing offline simulation data", Toast.LENGTH_SHORT).show();
    }
}