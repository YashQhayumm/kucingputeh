package com.example.kucingputeh.remote;

import com.example.kucingputeh.model.User;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PassengerService {

    @GET("passenger/profile")
    Call<User> getPassengerProfile(@Header("Authorization") String token);

    // "passenger/update" doesn't exist on the server (confirmed via testing).
    // The auto-generated CRUD route for the users table is users/{id}, which
    // does exist and returns/accepts JSON directly.
    @PUT("users/{id}")
    Call<ResponseBody> updatePassengerProfile(
            @Header("Authorization") String token,
            @Path("id") int userId,
            @Body Map<String, Object> updates
    );
}