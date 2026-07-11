package com.example.kucingputeh;

import android.app.Application;
import android.content.Context;

/**
 * Custom Application class. Lets RetrofitClient (and anything else that
 * doesn't already have a Context handy) read the currently logged-in user's
 * token from SharedPrefManager, without having to thread a Context through
 * every single API call site.
 */
public class KucingPutehApp extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
