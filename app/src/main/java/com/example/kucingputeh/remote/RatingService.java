package com.example.kucingputeh.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RatingService {
    @FormUrlEncoded
    @POST("ratings")
    Call<ResponseBody> addRating(
            @Field("ride_id") int rideId,
            @Field("reviewer_id") int reviewerId,
            @Field("reviewee_id") int revieweeId,
            @Field("score") int score,
            @Field("comments") String comments
    );
    @GET("ratings")
    Call<java.util.List<com.example.kucingputeh.model.Rating>> getRatingsByReviewee(@retrofit2.http.Query("reviewee_id") int revieweeId);
}
