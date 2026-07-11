package com.example.kucingputeh.remote;

import com.example.kucingputeh.model.Ride;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideService {

    // VIEW AVAILABLE RIDES
    @GET("Rides")
    Call<List<Ride>> getAllRides();

    // VIEW MY RIDES
    @GET("Rides")
    Call<List<Ride>> getRidesByDriver(@Query("driver_id") int driverId);

    // GET A SINGLE RIDE (used to re-check available_seats right before booking)
    @GET("Rides/{id}")
    Call<Ride> getRideById(@Path("id") int rideId);

    // CREATE RIDE - Updated based on provided parameters
    @FormUrlEncoded
    @POST("Rides")
    Call<ResponseBody> createRide(
            @Field("driver_id") int driverId,
            @Field("Origin") String origin,
            @Field("Destination") String destination,
            @Field("DepartureTime") String departureTime,
            @Field("total_seats") int totalSeats,
            @Field("available_seats") int availableSeats,
            @Field("status") String status
    );

    // Update Available Seats
    @FormUrlEncoded
    @POST("Rides/{id}")
    Call<ResponseBody> updateAvailableSeats(
            @Path("id") int rideId,
            @Field("available_seats") int availableSeats
    );

    @DELETE("Rides/{id}")
    Call<ResponseBody> deleteRide(@Path("id") int rideId);
}