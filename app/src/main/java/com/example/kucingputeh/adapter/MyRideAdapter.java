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
import com.example.kucingputeh.model.User;
import com.example.kucingputeh.remote.SharedPrefManager;
import com.example.kucingputeh.util.MapUtils;

import java.util.List;

public class MyRideAdapter extends RecyclerView.Adapter<MyRideAdapter.MyRideViewHolder> {

    public interface OnViewPassengersClickListener {
        void onViewPassengersClick(Ride ride);
    }

    private final List<Ride> rideList;
    private final OnViewPassengersClickListener listener;

    public MyRideAdapter(List<Ride> rideList, OnViewPassengersClickListener listener) {
        this.rideList = rideList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ride_item, parent, false);
        return new MyRideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRideViewHolder holder, int position) {
        Ride ride = rideList.get(position);

        holder.tvRoute.setText(ride.getOrigin() + " ➔ " + ride.getDestination());
        holder.tvDepartureTime.setText("Departure: " + ride.getDepartureTime());
        holder.tvAvailableSeats.setText("Available Seats: " + ride.getAvailableSeats());

        // Only admins need to see which driver owns each ride here -- a
        // driver looking at their own "My Rides" list already knows it's them.
        User currentUser = new SharedPrefManager(holder.itemView.getContext()).getUser();
        boolean isAdmin = currentUser != null && currentUser.getRole() != null
                && currentUser.getRole().equalsIgnoreCase("admin");
        if (isAdmin) {
            holder.tvDriverInfo.setText("Driver ID: " + ride.getDriverId());
            holder.tvDriverInfo.setVisibility(View.VISIBLE);
        } else {
            holder.tvDriverInfo.setVisibility(View.GONE);
        }

        holder.btnViewPassengers.setOnClickListener(v -> {
            if (listener != null) listener.onViewPassengersClick(ride);
        });

        holder.btnViewOnMap.setOnClickListener(v ->
                MapUtils.openRouteOnMap(v.getContext(), ride.getOrigin(), ride.getDestination()));
    }

    @Override
    public int getItemCount() {
        return rideList != null ? rideList.size() : 0;
    }

    public static class MyRideViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDepartureTime, tvAvailableSeats, tvDriverInfo;
        Button btnViewPassengers, btnViewOnMap;

        public MyRideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvAvailableSeats = itemView.findViewById(R.id.tvAvailableSeats);
            tvDriverInfo = itemView.findViewById(R.id.tvDriverInfo);
            btnViewPassengers = itemView.findViewById(R.id.btnViewPassengers);
            btnViewOnMap = itemView.findViewById(R.id.btnViewOnMap);
        }
    }
}
