package com.example.kucingputeh.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kucingputeh.R;
import com.example.kucingputeh.model.Ride;
import com.example.kucingputeh.util.MapUtils;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Ride ride);
    }

    private final List<Ride> rideList;
    private final OnBookClickListener listener;

    public RideAdapter(List<Ride> rideList, OnBookClickListener listener) {
        this.rideList = rideList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);

        holder.tvRoute.setText(ride.getOrigin() + " ➔ " + ride.getDestination());
        holder.tvDepartureTime.setText("Departure: " + ride.getDepartureTime());
        holder.tvAvailableSeats.setText("Available Seats: " + ride.getAvailableSeats());

        // Fetch driver info (Name and Rating)
        holder.tvDriverInfo.setText("Driver: Loading...");
        com.example.kucingputeh.remote.ApiUtils.getUserService().getUserById(ride.getDriverId()).enqueue(new retrofit2.Callback<com.example.kucingputeh.model.User>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.kucingputeh.model.User> call, retrofit2.Response<com.example.kucingputeh.model.User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.kucingputeh.model.User driver = response.body();
                    String name = driver.getUsername();

                    // Instead of using driver.getRating(), fetch from ratings table
                    com.example.kucingputeh.remote.ApiUtils.getRatingService().getRatingsByReviewee(driver.getId()).enqueue(new retrofit2.Callback<java.util.List<com.example.kucingputeh.model.Rating>>() {
                        @Override
                        public void onResponse(retrofit2.Call<java.util.List<com.example.kucingputeh.model.Rating>> call, retrofit2.Response<java.util.List<com.example.kucingputeh.model.Rating>> response) {
                            String ratingText = "N/A";
                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                double sum = 0;
                                for (com.example.kucingputeh.model.Rating r : response.body()) {
                                    sum += r.getScore();
                                }
                                double avg = sum / response.body().size();
                                ratingText = String.format(java.util.Locale.US, "%.1f ★", avg);
                            }
                            holder.tvDriverInfo.setText("Driver: " + name + " (" + ratingText + ")");
                        }

                        @Override
                        public void onFailure(retrofit2.Call<java.util.List<com.example.kucingputeh.model.Rating>> call, Throwable t) {
                            holder.tvDriverInfo.setText("Driver: " + name + " (N/A)");
                        }
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.kucingputeh.model.User> call, Throwable t) {
                holder.tvDriverInfo.setText("Driver: Unknown");
            }
        });

        if (ride.getAvailableSeats() <= 0) {
            holder.btnBookRide.setText("Fully Booked");
            holder.btnBookRide.setEnabled(false);
        } else {
            holder.btnBookRide.setText("Book Ride");
            holder.btnBookRide.setEnabled(true);
        }

        holder.btnBookRide.setOnClickListener(v -> {
            if (listener != null) listener.onBookClick(ride);
        });

        holder.btnViewOnMap.setOnClickListener(v ->
                MapUtils.openRouteOnMap(v.getContext(), ride.getOrigin(), ride.getDestination()));
    }

    @Override
    public int getItemCount() {
        return rideList != null ? rideList.size() : 0;
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDepartureTime, tvAvailableSeats, tvDriverInfo;
        Button btnBookRide, btnViewOnMap;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvAvailableSeats = itemView.findViewById(R.id.tvAvailableSeats);
            tvDriverInfo = itemView.findViewById(R.id.tvDriverInfo);
            btnBookRide = itemView.findViewById(R.id.btnBookRide);
            btnViewOnMap = itemView.findViewById(R.id.btnViewOnMap);
        }
    }
}
