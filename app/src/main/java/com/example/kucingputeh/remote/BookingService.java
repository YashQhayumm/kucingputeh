package com.example.kucingputeh.remote;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface BookingService {

    @FormUrlEncoded
    @POST("Bookings")
    Call<ResponseBody> bookRide(
            @Field("passenger_id") int passengerId,
            @Field("RideID") int rideId,
            @Field("seats_booked") int seatsBooked
    );

    @GET("Bookings")
    Call<ResponseBody> viewBookings(@QueryMap Map<String, String> filters);

 //cancel booking
    @DELETE("Bookings/{id}")
    Call<ResponseBody> cancelBooking(
            @Path("id") int bookingId
    );

    // rating lepas completed booking

    @FormUrlEncoded
    @POST("ratings")
    Call<ResponseBody> submitRating(
            @Field("ride_id") int rideId,
            @Field("reviewer_id") int reviewerId,
            @Field("reviewee_id") int revieweeId,
            @Field("score") int score,
            @Field("comments") String comments
    );
}