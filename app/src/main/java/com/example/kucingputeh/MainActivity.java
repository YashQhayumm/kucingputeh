package com.example.kucingputeh;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.kucingputeh.remote.LoginActivity;
import com.example.kucingputeh.remote.SharedPrefManager;
import com.example.kucingputeh.remote.UpdateDriverProfile;
import com.example.kucingputeh.remote.UpdatePassengerProfile;
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
    private SharedPrefManager spm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        spm = new SharedPrefManager(getApplicationContext());

        // 2. Komen/Buang bahagian redirect auto-login ni supaya tak lari ke Login screen masa mula
        /*
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        *////

        Button btnLogin = findViewById(R.id.btnGoToLogin);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            });
        }


        findViewById(R.id.btnFindRides).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewAvailableRidesActivity.class)));

        findViewById(R.id.btnCreateRide).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CreateRideActivity.class)));

        findViewById(R.id.btnMyRides).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewMyRidesActivity.class)));

        findViewById(R.id.btnChatWithDriver).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ChatActivity.class)));

        findViewById(R.id.btnProfile).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        findViewById(R.id.btnUpdatePassengerProfile).setOnClickListener(v -> {
            User user = spm.getUser();
            if (user != null) {
                if ("driver".equalsIgnoreCase(user.getRole())) {
                    startActivity(new Intent(MainActivity.this, UpdateDriverProfile.class));
                } else {
                    startActivity(new Intent(MainActivity.this, UpdatePassengerProfile.class));
                }
            }
        });



        rvBookings = findViewById(R.id.rvBookings);
        if (rvBookings != null) {
            rvBookings.setLayoutManager(new LinearLayoutManager(this));
            bookingList = new ArrayList<>();
            bookingService = ApiUtils.getBookingService();

            User user = spm.getUser();
            if (user != null) {
                fetchUserBookings(user.getId());
            }
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
        if (bookingList != null) {
            bookingList.clear();
            adapter = new BookingAdapter(bookingList);
            rvBookings.setAdapter(adapter);
        }
    }
}