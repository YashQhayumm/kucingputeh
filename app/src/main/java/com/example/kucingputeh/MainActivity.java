package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kucingputeh.remote.LoginActivity;
import com.example.kucingputeh.remote.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());

        if (!spm.isLoggedIn()) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {

            Intent intent = new Intent(this, ViewAvailableRidesActivity.class);
            startActivity(intent);
        }
        //no return back to login page
        finish();
    }
}