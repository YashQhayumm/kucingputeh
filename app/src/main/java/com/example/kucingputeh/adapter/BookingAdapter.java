package com.example.kucingputeh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kucingputeh.R;
import com.example.kucingputeh.model.Booking;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.BookingService;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final Context context;
    private final List<Booking> bookingList;
    private final BookingService bookingService;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.bookingService = ApiUtils.getBookingService();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Bind data to layout views
        String routeText = booking.getOrigin() + " ➔ " + booking.getDestination();
        holder.tvRoute.setText(routeText);
        holder.tvDepartureTime.setText("Departure: " + booking.getDepartureTime());
        holder.tvSeatsBooked.setText("Seats Secured: " + booking.getSeatsBooked());

        // Handle Cancel Booking Button Click
        holder.btnCancelBooking.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            // Call the networking service
            bookingService.cancelBooking(booking.getBookingId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Booking cancelled successfully!", Toast.LENGTH_SHORT).show();
                        // Remove item from UI list dynamically
                        bookingList.remove(currentPos);
                        notifyItemRemoved(currentPos);
                    } else {
                        Toast.makeText(context, "Failed to cancel booking.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDepartureTime, tvSeatsBooked;
        Button btnCancelBooking;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvSeatsBooked = itemView.findViewById(R.id.tvSeatsBooked);
            btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}