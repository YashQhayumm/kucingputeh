package com.example.kucingputeh.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        // CANCEL BUTTON HERE 
        holder.btnCancelBooking.setOnClickListener(v -> {
            int bookingIdToDelete = booking.getBookingId();

            ApiUtils.getBookingService().cancelBooking(bookingIdToDelete).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    // REST delete operations typically return 200 OK or 204 No Content upon success
                    if (response.isSuccessful()) {
                        android.widget.Toast.makeText(v.getContext(), "Booking cancelled successfully!", android.widget.Toast.LENGTH_SHORT).show();

                        // Remove item from listdynamically
                        bookingList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, bookingList.size());
                    } else {
                        android.util.Log.e("CANCEL_FAILED", "Code: " + response.code());
                        android.widget.Toast.makeText(v.getContext(), "Failed to cancel. Server code: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable t) {
                    android.widget.Toast.makeText(v.getContext(), "Network Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDepartureTime, tvSeatsBooked, tvStatus;
        Button btnCancelBooking;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvSeatsBooked = itemView.findViewById(R.id.tvSeatsBooked);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
