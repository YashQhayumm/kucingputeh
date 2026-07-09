package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kucingputeh.adapter.RideAdapter;
import com.example.kucingputeh.model.Ride;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.RideService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAvailableRidesActivity extends AppCompatActivity {

    private RecyclerView rvRides;
    private TextView tvEmptyRides;
    private EditText etSearchDestination;

    private RideAdapter adapter;
    private RideService rideService;

    // Holds every ride fetched from the server; rideList holds what is currently shown (after filtering)
    private final List<Ride> allRides = new ArrayList<>();
    private final List<Ride> rideList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rides);

        rvRides = findViewById(R.id.rvRides);
        tvEmptyRides = findViewById(R.id.tvEmptyRides);
        etSearchDestination = findViewById(R.id.etSearchDestination);

        rvRides.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RideAdapter(rideList, this::openBookRideScreen);
        rvRides.setAdapter(adapter);

        rideService = ApiUtils.getRideService();

        etSearchDestination.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRides(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh every time the screen is shown, e.g. after coming back from booking a ride
        fetchAvailableRides();
    }

    private void fetchAvailableRides() {
        rideService.getAllRides().enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ride>> call, @NonNull Response<List<Ride>> response) {
                allRides.clear();
                if (response.isSuccessful() && response.body() != null) {
                    List<Ride> fetchedRides = response.body();
                    Log.d("VIEW_RIDES", "Received " + fetchedRides.size() + " rides");
                    for (Ride ride : fetchedRides) {
                        // Check data in logs to be sure
                        Log.d("VIEW_RIDES", "Ride ID: " + ride.getRideId() + ", Destination: " + ride.getDestination());
                        allRides.add(ride);
                    }
                } else {
                    String errorMsg = "Server Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += "\n" + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("VIEW_RIDES", errorMsg);
                    Toast.makeText(ViewAvailableRidesActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
                filterRides(etSearchDestination.getText().toString());
            }

            @Override
            public void onFailure(@NonNull Call<List<Ride>> call, @NonNull Throwable t) {
                Log.e("VIEW_RIDES", "Network error", t);
                Toast.makeText(ViewAvailableRidesActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterRides(String query) {
        rideList.clear();
        String q = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());

        for (Ride ride : allRides) {
            String origin = ride.getOrigin();
            String dest = ride.getDestination();
            boolean matchesOrigin = origin != null && origin.toLowerCase(Locale.getDefault()).contains(q);
            boolean matchesDest = dest != null && dest.toLowerCase(Locale.getDefault()).contains(q);

            if (q.isEmpty() || matchesOrigin || matchesDest) {
                rideList.add(ride);
            }
        }
        adapter.notifyDataSetChanged();

        boolean isEmpty = rideList.isEmpty();
        tvEmptyRides.setVisibility(isEmpty ? android.view.View.VISIBLE : android.view.View.GONE);
        rvRides.setVisibility(isEmpty ? android.view.View.GONE : android.view.View.VISIBLE);
    }

    private void openBookRideScreen(Ride ride) {
        Intent intent = new Intent(ViewAvailableRidesActivity.this, BookRideActivity.class);
        intent.putExtra(BookRideActivity.EXTRA_RIDE_ID, ride.getRideId());
        intent.putExtra(BookRideActivity.EXTRA_ORIGIN, ride.getOrigin());
        intent.putExtra(BookRideActivity.EXTRA_DESTINATION, ride.getDestination());
        intent.putExtra(BookRideActivity.EXTRA_DEPARTURE_TIME, ride.getDepartureTime());
        intent.putExtra(BookRideActivity.EXTRA_AVAILABLE_SEATS, ride.getAvailableSeats());
        startActivity(intent);
    }
}