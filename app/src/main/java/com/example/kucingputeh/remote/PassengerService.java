package com.example.kucingputeh.remote;

import com.example.kucingputeh.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface PassengerService {

        @GET("passenger/profile")
        Call<User> getPassengerProfile(@Header("Authorization") String token);

        @PUT("passenger/update")
        Call<ResponseBody> updatePassengerProfile(@Header("Authorization") String token, @Body User user);
    }


