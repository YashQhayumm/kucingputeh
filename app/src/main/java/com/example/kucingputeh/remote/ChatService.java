package com.example.kucingputeh.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatService {

    @GET("chats")
    Call<ResponseBody> getMessages(
            @Query("sender_id") int senderId,
            @Query("receiver_id") int receiverId,
            @Query("ride_id") int rideId
    );

    @FormUrlEncoded
    @POST("chats")
    Call<ResponseBody> sendMessage(
            @Field("sender_id") int senderId,
            @Field("receiver_id") int receiverId,
            @Field("message") String message,
            @Field("ride_id") int rideId
    );
}