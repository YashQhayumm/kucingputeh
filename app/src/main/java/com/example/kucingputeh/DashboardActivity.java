package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        CardView cardMyRides = findViewById(R.id.cardMyRides);
        CardView cardBookings = findViewById(R.id.cardBookings);
        CardView cardProfile = findViewById(R.id.cardProfile);
        CardView cardLogout = findViewById(R.id.cardLogout);

        cardFindRides.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ViewAvailableRidesActivity.class)));

        cardCreateRide.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, CreateRideActivity.class)));

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
        // Admin: Find Rides, Create Ride, Bookings, Profile, Logout (no My Rides)
        // User:  Find Rides, My Rides, Profile, Logout (no Create Ride / Bookings)
        User user = spm.getUser();
        boolean isAdmin = user != null && user.getRole() != null
                && user.getRole().equalsIgnoreCase("admin");

        cardCreateRide.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        cardBookings.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        cardMyRides.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
        // cardFindRides, cardProfile, and cardLogout stay visible for both roles.
    }
}