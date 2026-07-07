package com.example.kucingputeh.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import java.util.List;

import com.example.kucingputeh.model.User;

public interface UserService {

    @FormUrlEncoded
    @GET ("users/login")
    Call<User> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @GET ("users/login")
    Call<User> loginEmail(@Field("email") String username, @Field("password") String password);

}