package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.kucingputeh.remote.LoginActivity;
import com.example.kucingputeh.remote.PrefManager;
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
    private PrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spm = new PrefManager(getApplicationContext());

        // 1. Semak status login
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 2. Jika sudah login, paparkan Booking List
        setContentView(R.layout.activity_booking_list);

        rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        bookingService = ApiUtils.getBookingService();

        // Dapatkan ID user yang login untuk fetch data
        User user = spm.getUser();
        if (user != null) {
            fetchUserBookings(user.getId()); // Guna ID sebenar dari SharedPref
        }
    }

    private void fetchUserBookings(int passengerId) {
        Map<String, String> filters = new HashMap<>();
        filters.put("passenger_id", String.valueOf(passengerId));

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
        bookingList.clear();
        adapter = new BookingAdapter(bookingList);
        rvBookings.setAdapter(adapter);
        Toast.makeText(MainActivity.this, "No active bookings found.", Toast.LENGTH_SHORT).show();
    }
}