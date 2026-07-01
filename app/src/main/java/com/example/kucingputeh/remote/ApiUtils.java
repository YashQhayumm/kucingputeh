package com.example.kucingputeh.remote;

public class ApiUtils {

// REST API server URL

    public static final String BASE_URL = "http://aptitude.my/kucingputeh/api/";
// return UserService instance

    public static UserService getUserService() {

        return RetrofitClient.getClient(BASE_URL).create(UserService.class);

    }
// return BookService instance

    public static BookingService getBookingService() {

        return RetrofitClient.getClient(BASE_URL).create(BookingService.class);

    }

}






