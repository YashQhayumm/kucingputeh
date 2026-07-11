package com.example.kucingputeh.remote;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kucingputeh.model.User;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "kucingputeh_sharedpref";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_TOKEN = "keytoken";
    private static final String KEY_ROLE = "keyrole";
    private static final String KEY_VEHICLE = "keyvehicle";
    private static final String KEY_PLATE = "keyplatenumber";
    private static final String KEY_PHONE = "keyphone";

    private final Context mCtx;

    public SharedPrefManager(Context context) {
        mCtx = context;
    }

    public void storeUser(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_PLATE, user.getPlateNumber());
        editor.putString(KEY_VEHICLE, user.getVehicleModel());
        editor.putString(KEY_PHONE, user.getPhone());

        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    // Satu sahaja method getUser yang lengkap
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        User user = new User();
        user.setId(sharedPreferences.getInt(KEY_ID, -1));
        user.setUsername(sharedPreferences.getString(KEY_USERNAME, null));
        user.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        user.setToken(sharedPreferences.getString(KEY_TOKEN, null));
        user.setRole(sharedPreferences.getString(KEY_ROLE, null));
        user.setPlateNumber(sharedPreferences.getString(KEY_PLATE, null));
        user.setVehicleModel(sharedPreferences.getString(KEY_VEHICLE, null));
        user.setPhone(sharedPreferences.getString(KEY_PHONE, null));

        return user;
    }

    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}