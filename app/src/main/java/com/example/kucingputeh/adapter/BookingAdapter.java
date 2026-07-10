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
import com.example.kucingputeh.remote.ApiUtils;

import okhttp3.ResponseBody;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvRoute.setText(booking.getOrigin() + " ➔ " + booking.getDestination());
        holder.tvStatus.setText("Status: " + booking.getBookingStatus());
        holder.tvSeatsBooked.setText("Seats Secured: " + booking.getSeatsBooked());

        holder.btnChatWithDriver.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            // Assuming driver_id is available in Booking/Ride model
            intent.putExtra("RIDE_ID", booking.getRideId());
            intent.putExtra("PASSENGER_ID", booking.getPassengerId());
            v.getContext().startActivity(intent);
        });

        // CANCEL BUTTON HERE
        holder.btnCancelBooking.setOnClickListener(v -> {
            int bookingIdToDelete = booking.getBookingId();
            int rideId = booking.getRideId();
            int seatsToReturn = booking.getSeatsBooked();

            // 1. First get the current ride info to know how many seats are available NOW
            ApiUtils.getRideService().getRideById(rideId).enqueue(new retrofit2.Callback<com.example.kucingputeh.model.Ride>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<com.example.kucingputeh.model.Ride> call, @NonNull retrofit2.Response<com.example.kucingputeh.model.Ride> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int currentAvailable = response.body().getAvailableSeats();
                        int newTotalSeats = currentAvailable + seatsToReturn;

                        // 2. Now cancel the booking
                        cancelBookingAndRefreshSeats(v, bookingIdToDelete, rideId, newTotalSeats, position);
                    } else {
                        android.widget.Toast.makeText(v.getContext(), "Error fetching ride details", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<com.example.kucingputeh.model.Ride> call, @NonNull Throwable t) {
                    android.widget.Toast.makeText(v.getContext(), "Network Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void cancelBookingAndRefreshSeats(View v, int bookingId, int rideId, int newSeats, int position) {
        ApiUtils.getBookingService().cancelBooking(bookingId).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 3. Finally update the seats back in the database
                    ApiUtils.getRideService().updateAvailableSeats(rideId, newSeats).enqueue(new retrofit2.Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull retrofit2.Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                            android.widget.Toast.makeText(v.getContext(), "Booking cancelled and seats restored!", android.widget.Toast.LENGTH_SHORT).show();

                            // Remove item from list dynamically
                            if (position < bookingList.size()) {
                                bookingList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, bookingList.size());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable t) {
                            android.util.Log.e("SEAT_UPDATE_FAIL", t.getMessage());
                            // Even if seat update fails, the booking is gone from DB, so we remove from UI
                            bookingList.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    android.widget.Toast.makeText(v.getContext(), "Failed to cancel booking", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable t) {
                android.widget.Toast.makeText(v.getContext(), "Network Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDepartureTime, tvSeatsBooked, tvStatus;
        Button btnCancelBooking, btnChatWithDriver;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvSeatsBooked = itemView.findViewById(R.id.tvSeatsBooked);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
            btnChatWithDriver = itemView.findViewById(R.id.btnChatWithDriver);
        }
    }
}
