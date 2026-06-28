package com.example.kucingputeh.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BookingService {

    // 1. BOOK A RIDE
    @FormUrlEncoded
    @POST("book_ride.php")
    Call<ResponseBody> bookRide(
            @Field("passenger_id") int passengerId,
            @Field("ride_id") int rideId,
            @Field("seats_booked") int seatsBooked
    );

    // 2. VIEW MY BOOKINGS
    @FormUrlEncoded
    @POST("view_bookings.php")
    Call<ResponseBody> viewBookings(
            @Field("passenger_id") int passengerId
    );

    // 3. CANCEL A BOOKING
    @FormUrlEncoded
    @POST("cancel_booking.php")
    Call<ResponseBody> cancelBooking(
            @Field("booking_id") int bookingId
    );
}