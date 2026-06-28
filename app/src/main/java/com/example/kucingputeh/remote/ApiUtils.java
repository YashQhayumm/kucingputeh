package com.example.kucingputeh.remote;

public class ApiUtils {
    public static final String BASE_URL = "https://aptitude.my/kucingputeh/";

    public static com.example.kucingputeh.remote.BookingService getBookingService() {
        return com.example.kucingputeh.remote.RetrofitClient.getClient(BASE_URL).create(com.example.kucingputeh.remote.BookingService.class);
    }
}