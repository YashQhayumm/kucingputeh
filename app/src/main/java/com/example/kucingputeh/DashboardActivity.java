package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.kucingputeh.model.User;
import com.example.kucingputeh.remote.LoginActivity;
import com.example.kucingputeh.remote.SharedPrefManager;

public class DashboardActivity extends AppCompatActivity {

    private SharedPrefManager spm;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
// ccc
        spm = new SharedPrefManager(getApplicationContext());

        CardView cardFindRides = findViewById(R.id.cardFindRides);
        CardView cardCreateRide = findViewById(R.id.cardCreateRide);
        CardView cardMyCreatedRides = findViewById(R.id.cardMyCreatedRides);
        TextView textMyCreatedRides = findViewById(R.id.textMyCreatedRides);
        CardView cardMyRides = findViewById(R.id.cardMyRides);
        CardView cardBookings = findViewById(R.id.cardBookings);
        CardView cardProfile = findViewById(R.id.cardProfile);
        CardView cardLogout = findViewById(R.id.cardLogout);

        cardFindRides.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ViewAvailableRidesActivity.class)));

        cardCreateRide.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, CreateRideActivity.class)));

        cardMyCreatedRides.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ViewMyRidesActivity.class)));

        cardMyRides.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ViewMyRidesActivity.class)));

        cardBookings.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MainActivity.class)));

        cardProfile.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class)));

        cardLogout.setOnClickListener(v -> {
            spm.logout();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Show/hide cards depending on the logged-in user's role.
        // Passenger/User: Find Rides, My Booked Ride, Profile, Logout
        // Admin:          Find Rides, Create Ride, All Ride, Booking, Profile, Logout
        // Driver:         Find Rides, Create Ride, My Rides, Booking, Profile, Logout
        User user = spm.getUser();
        String role = (user != null) ? user.getRole() : null;
        boolean isAdmin = role != null && role.equalsIgnoreCase("admin");
        boolean isDriver = role != null && role.equalsIgnoreCase("driver");
        boolean canManageRides = isAdmin || isDriver;

        cardCreateRide.setVisibility(canManageRides ? View.VISIBLE : View.GONE);
        cardMyCreatedRides.setVisibility(canManageRides ? View.VISIBLE : View.GONE);

        cardMyRides.setVisibility(canManageRides ? View.GONE : View.VISIBLE);
        cardBookings.setVisibility(canManageRides ? View.VISIBLE : View.GONE);

        // Admin sees "All Ride" (manages every ride), driver still sees "My Rides".
        textMyCreatedRides.setText(isAdmin ? "All Ride" : "My Rides");

        // cardFindRides, cardProfile, and cardLogout stay visible for both roles.
    }
}