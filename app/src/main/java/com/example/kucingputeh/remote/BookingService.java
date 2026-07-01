package com.example.kucingputeh.remote;

//
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface BookingService {

    // BOOK A RIDE
    @FormUrlEncoded
    @POST("Bookings")
    Call<ResponseBody> bookRide(
            @Field("passenger_id") int passengerId,
            @Field("ride_id") int rideId,
            @Field("seats_booked") int seatsBooked
    );

    // VIEW MY BOOKINGS
    @GET("Bookings")
    Call<ResponseBody> viewBookings(@QueryMap Map<String, String> filters);

    // CANCEL A BOOKING
    @DELETE("Bookings/{id}") //ntah kenapa syntax camni tapi gemini tolong
    Call<ResponseBody> cancelBooking(
            @Path("id") int bookingId
    );
}