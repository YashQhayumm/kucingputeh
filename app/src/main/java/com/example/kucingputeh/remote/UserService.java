package com.example.kucingputeh.remote;

import com.example.kucingputeh.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {
    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("users/login")
    Call<User> loginEmail(
            @Field("email") String email,
            @Field("password") String password
    );
//
        @FormUrlEncoded
        @POST("users/register")
        Call<ResponseBody> registerUser(

                @Field("username") String username,
                @Field("StudentID") String studentId,
                @Field("email") String email,
                @Field("password") String password,
                @Field("role") String role,
                @Field("plate_number") String plateNumber,
                @Field("car_model") String vehicleModel
        );
    }

