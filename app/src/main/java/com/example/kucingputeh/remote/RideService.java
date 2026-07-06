package com.example.kucingputeh.remote;

import com.example.kucingputeh.model.Ride;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideService {

    // VIEW AVAILABLE RIDES - all rides posted on the platform
    @GET("Rides")
    Call<List<Ride>> getAllRides();

    // VIEW MY RIDES - rides created by the currently logged-in driver
    @GET("Rides")
    Call<List<Ride>> getRidesByDriver(@Query("driver_id") int driverId);

    // CREATE RIDE
    @FormUrlEncoded
    @POST("Rides")
    Call<ResponseBody> createRide(
            @Field("driver_id") int driverId,
            @Field("origin") String origin,
            @Field("destination") String destination,
            @Field("departure_time") String departureTime,
            @Field("available_seats") int availableSeats
    );

    // Used after a booking is made, to reduce the seat count on the ride
    @FormUrlEncoded
    @PATCH("Rides/{id}")
    Call<ResponseBody> updateAvailableSeats(
            @Path("id") int rideId,
            @Field("available_seats") int availableSeats
    );

    // Optional: remove a ride (not required by the spec, kept for completeness)
    @DELETE("Rides/{id}")
    Call<ResponseBody> deleteRide(@Path("id") int rideId);
}
