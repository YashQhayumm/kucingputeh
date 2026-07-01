package com.example.kucingputeh.remote;

public class ApiUtils {

// REST API server URL

    public static final String BASE_URL = "http://aptitude.my/2024553775/api/";
// return UserService instance

    public static UserService getUserService() {

        return RetrofitClient.getClient(BASE_URL).create(UserService.class);

    }
// return BookService instance

}






