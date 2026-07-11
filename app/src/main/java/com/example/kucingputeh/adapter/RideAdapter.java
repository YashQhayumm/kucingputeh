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
        TextView tvRoute, tvDepartureTime, tvAvailableSeats;
        Button btnBookRide, btnViewOnMap;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvAvailableSeats = itemView.findViewById(R.id.tvAvailableSeats);
            btnBookRide = itemView.findViewById(R.id.btnBookRide);
            btnViewOnMap = itemView.findViewById(R.id.btnViewOnMap);
        }
    }
}
