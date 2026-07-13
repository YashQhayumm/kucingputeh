package com.example.kucingputeh.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kucingputeh.ChatActivity;
import com.example.kucingputeh.R;
import com.example.kucingputeh.model.Booking;

import java.util.List;
import java.util.Map;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.ViewHolder> {

    private final List<Booking> bookings;
    private final Map<Integer, String> passengerNames;
    private final int rideId;
    private final int driverId;

    public PassengerAdapter(List<Booking> bookings, Map<Integer, String> passengerNames, int rideId, int driverId) {
        this.bookings = bookings;
        this.passengerNames = passengerNames;
        this.rideId = rideId;
        this.driverId = driverId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.passenger_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        String name = passengerNames != null ? passengerNames.get(booking.getPassengerId()) : "Unknown";
        
        holder.tvPassengerName.setText(name != null ? name : "Unknown");
        holder.tvBookingInfo.setText("Seats: " + booking.getSeatsBooked() + " | Status: " + booking.getBookingStatus());

        holder.btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("RIDE_ID", rideId);
            intent.putExtra("PASSENGER_ID", booking.getPassengerId());
            intent.putExtra("DRIVER_ID", driverId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPassengerName, tvBookingInfo;
        Button btnChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPassengerName = itemView.findViewById(R.id.tvPassengerName);
            tvBookingInfo = itemView.findViewById(R.id.tvBookingInfo);
            btnChat = itemView.findViewById(R.id.btnChatWithPassenger);
        }
    }
}
