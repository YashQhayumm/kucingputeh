package com.example.kucingputeh.remote;

import com.example.kucingputeh.model.Rating;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RatingService {
    @POST("ratings/add")
    Call<ResponseBody> addRating(@Body Rating rating);
}
