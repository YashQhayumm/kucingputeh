package com.example.kucingputeh.remote;

import com.example.kucingputeh.model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST; // Pastikan guna @POST

public interface UserService {

    @FormUrlEncoded
    @POST("login.php") // Mesti guna @POST jika ada @FormUrlEncoded
    Call<User> login(
            @Field("username") String username,
            @Field("password") String password
    );

    // Sama juga untuk loginEmail
    @FormUrlEncoded
    @POST("login_email.php")
    Call<User> loginEmail(
            @Field("email") String email,
            @Field("password") String password
    );
}