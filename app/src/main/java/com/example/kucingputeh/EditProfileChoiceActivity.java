package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.remote.UpdateDriverProfile;
import com.example.kucingputeh.remote.UpdatePassengerProfile;

public class EditProfileChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_choice);

        Button btnEditPassenger = findViewById(R.id.btnEditPassenger);
        Button btnEditDriver = findViewById(R.id.btnEditDriver);

        btnEditPassenger.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileChoiceActivity.this, UpdatePassengerProfile.class));
        });

        btnEditDriver.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileChoiceActivity.this, UpdateDriverProfile.class));
        });
    }
}
