package com.example.kucingputeh;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kucingputeh.adapter.BookingAdapter;
import com.example.kucingputeh.adapter.MyRideAdapter;
import com.example.kucingputeh.model.Booking;
import com.example.kucingputeh.model.Ride;
import com.example.kucingputeh.model.User;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.BookingService;
import com.example.kucingputeh.remote.RideService;
import com.example.kucingputeh.remote.SharedPrefManager;
import com.example.kucingputeh.remote.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewMyRidesActivity extends AppCompatActivity {

    private RecyclerView rvMyRides;
    private TextView tvEmptyMyRides;
    private TextView tvMyRidesTitle;

    // Driver view: rides this driver created
    private MyRideAdapter rideAdapter;
    private final List<Ride> myRideList = new ArrayList<>();

    // Passenger/user view: rides this user has booked
    private BookingAdapter bookingAdapter;
    private final List<Booking> myBookingList = new ArrayList<>();

    private boolean isDriverView = false;

    private RideService rideService;
    private BookingService bookingService;
    private UserService userService;
    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        spm = new SharedPrefManager(getApplicationContext());
        rideService = ApiUtils.getRideService();
        bookingService = ApiUtils.getBookingService();
        userService = ApiUtils.getUserService();

        rvMyRides = findViewById(R.id.rvMyRides);
        tvEmptyMyRides = findViewById(R.id.tvEmptyMyRides);
        tvMyRidesTitle = findViewById(R.id.tvMyRidesTitle);

        rvMyRides.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = spm.getUser();
        if (user == null || user.getId() <= 0) {
            return;
        }

        isDriverView = user.getRole() != null && user.getRole().equalsIgnoreCase("driver");
        boolean isAdminView = user.getRole() != null && user.getRole().equalsIgnoreCase("admin");

        if (isAdminView) {
            // Admin: see every ride in the system, with the assigned driver shown
            // (MyRideAdapter shows "Driver ID: X" automatically when the logged-in
            // user's role is admin).
            tvMyRidesTitle.setText("All Rides");
            tvEmptyMyRides.setText("There are no rides in the system yet.");
            if (rideAdapter == null) {
                rideAdapter = new MyRideAdapter(myRideList, this::showPassengersForRide);
            }
            rvMyRides.setAdapter(rideAdapter);
            fetchAllRides();
        } else if (isDriverView) {
            tvMyRidesTitle.setText("My Rides");
            tvEmptyMyRides.setText("You haven't created any rides yet.");
            if (rideAdapter == null) {
                rideAdapter = new MyRideAdapter(myRideList, this::showPassengersForRide);
            }
            rvMyRides.setAdapter(rideAdapter);
            fetchMyRides(user.getId());
        } else {
            tvMyRidesTitle.setText("My Bookings");
            tvEmptyMyRides.setText("You haven't booked any rides yet.");
            if (bookingAdapter == null) {
                bookingAdapter = new BookingAdapter(myBookingList);
            }
            rvMyRides.setAdapter(bookingAdapter);
            fetchMyBookings(user.getId());
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
                rideAdapter.notifyDataSetChanged();
                toggleEmptyState(myRideList.isEmpty());
            }

            @Override
            public void onFailure(@NonNull Call<List<Ride>> call, @NonNull Throwable t) {
                Log.e("MY_RIDES", "Network error", t);
                Toast.makeText(ViewMyRidesActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                toggleEmptyState(myRideList.isEmpty());
            }
        });
    }

    // Admin view: every ride in the system, regardless of driver.
    private void fetchAllRides() {
        rideService.getAllRides().enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ride>> call, @NonNull Response<List<Ride>> response) {
                myRideList.clear();
                if (response.isSuccessful() && response.body() != null) {
                    myRideList.addAll(response.body());
                } else {
                    Log.e("ALL_RIDES", "Server returned code: " + response.code());
                }
                rideAdapter.notifyDataSetChanged();
                toggleEmptyState(myRideList.isEmpty());
            }

            @Override
            public void onFailure(@NonNull Call<List<Ride>> call, @NonNull Throwable t) {
                Log.e("ALL_RIDES", "Network error", t);
                Toast.makeText(ViewMyRidesActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                toggleEmptyState(myRideList.isEmpty());
            }
        });
    }

    // Fetches the rides this passenger/user has booked (not rides they created)
    private void fetchMyBookings(int passengerId) {
        Map<String, String> filters = new HashMap<>();
        filters.put("passenger_id", String.valueOf(passengerId));

        bookingService.viewBookings(filters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                myBookingList.clear();
                if (response.isSuccessful() && response.body() != null && response.code() != 204) {
                    try {
                        String json = response.body().string();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Booking>>() {}.getType();
                        List<Booking> bookings = gson.fromJson(json, listType);
                        if (bookings != null) {
                            myBookingList.addAll(bookings);
                        }
                    } catch (IOException e) {
                        Log.e("MY_RIDES", "Failed to parse bookings", e);
                        Toast.makeText(ViewMyRidesActivity.this, "Failed to load your bookings.", Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() != 204) {
                    Log.e("MY_RIDES", "Server returned code: " + response.code());
                }
                bookingAdapter.notifyDataSetChanged();
                toggleEmptyState(myBookingList.isEmpty());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("MY_RIDES", "Network error", t);
                Toast.makeText(ViewMyRidesActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                toggleEmptyState(myBookingList.isEmpty());
            }
        });
    }

    private void toggleEmptyState(boolean isEmpty) {
        tvEmptyMyRides.setVisibility(isEmpty ? android.view.View.VISIBLE : android.view.View.GONE);
        rvMyRides.setVisibility(isEmpty ? android.view.View.GONE : android.view.View.VISIBLE);
    }

    // Fetches everyone who booked a given ride and shows them in a dialog
    private void showPassengersForRide(Ride ride) {
        Map<String, String> filters = new HashMap<>();
        // NOTE: the Bookings table's foreign key to Rides is "RideID"
        // (matches what bookRide() writes via @Field("RideID") and what
        // comes back nested in the JSON), NOT "ride_id". Filtering on the
        // wrong key silently returned zero rows every time.
        filters.put("RideID", String.valueOf(ride.getRideId()));

        bookingService.viewBookings(filters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null && response.code() != 204) {
                    try {
                        String json = response.body().string();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Booking>>() {}.getType();
                        List<Booking> bookings = gson.fromJson(json, listType);
                        fetchPassengerNamesAndShowDialog(ride, bookings);
                    } catch (IOException e) {
                        Log.e("MY_RIDES", "Failed to parse bookings", e);
                        Toast.makeText(ViewMyRidesActivity.this, "Failed to load passengers.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    displayPassengerDialog(ride, null, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("MY_RIDES", "Network error", t);
                Toast.makeText(ViewMyRidesActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Looks up each passenger's full name (username) via users/{id} so the
    // dialog can show a name instead of just a raw passenger_id, then shows
    // the dialog once every lookup has finished (success or failure).
    private void fetchPassengerNamesAndShowDialog(Ride ride, List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            displayPassengerDialog(ride, bookings, null);
            return;
        }

        Map<Integer, String> passengerNames = new HashMap<>();
        AtomicInteger pending = new AtomicInteger(bookings.size());

        for (Booking booking : bookings) {
            int passengerId = booking.getPassengerId();
            userService.getUserById(passengerId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        passengerNames.put(passengerId, response.body().getUsername());
                    }
                    if (pending.decrementAndGet() == 0) {
                        displayPassengerDialog(ride, bookings, passengerNames);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Log.e("MY_RIDES", "Failed to fetch passenger #" + passengerId, t);
                    if (pending.decrementAndGet() == 0) {
                        displayPassengerDialog(ride, bookings, passengerNames);
                    }
                }
            });
        }
    }

    private void displayPassengerDialog(Ride ride, List<Booking> bookings, Map<Integer, String> passengerNames) {
        if (bookings == null || bookings.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(ride.getOrigin() + " ➔ " + ride.getDestination())
                    .setMessage("No passengers have booked this ride yet.")
                    .setPositiveButton("Close", null)
                    .show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_passengers, null);
        RecyclerView rvPassengers = dialogView.findViewById(R.id.rvPassengers);
        rvPassengers.setLayoutManager(new LinearLayoutManager(this));

        com.example.kucingputeh.adapter.PassengerAdapter adapter =
                new com.example.kucingputeh.adapter.PassengerAdapter(bookings, passengerNames, ride.getRideId(), ride.getDriverId());
        rvPassengers.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle("Passengers for " + ride.getOrigin() + " ➔ " + ride.getDestination())
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show();
    }
}