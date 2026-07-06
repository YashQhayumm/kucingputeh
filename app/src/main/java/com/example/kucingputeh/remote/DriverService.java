package com.example.kucingputeh.remote;

import com.example.kucingputeh.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Header;

public interface DriverService {
    @GET("driver/profile")
    Call<User>getDriverProfile(@Header("Authorization") String token);

    @PUT("driver/update")
    Call<ResponseBody> updateDriverProfile(@Header("Authorization") String token, @Body User driver);
}