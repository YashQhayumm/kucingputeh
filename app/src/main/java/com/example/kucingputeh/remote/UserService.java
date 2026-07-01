package com.example.kucingputeh.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

import com.example.kucingputeh.model.User;

public interface UserService {

    @GET("users")
    Call<List<User>> login(
            @Query("username") String username,
            @Query("password") String password
    );

    @GET("users")
    Call<List<User>> loginEmail(
            @Query("email") String email,
            @Query("password") String password
    );
}