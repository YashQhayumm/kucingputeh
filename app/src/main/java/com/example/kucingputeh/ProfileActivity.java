package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.model.User;
import com.example.kucingputeh.remote.LoginActivity;
import com.example.kucingputeh.remote.SharedPrefManager;
import com.example.kucingputeh.remote.UpdateDriverProfile;
import com.example.kucingputeh.remote.UpdatePassengerProfile;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone;
    private Button btnUpdate, btnLogout;
    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Shared Preferences
        spm = new SharedPrefManager(this);

        // Initialize Views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);

        // Load logged-in user information
        User user = spm.getUser();

        if (user != null) {
            etName.setText(user.getUsername());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
        }

        // Open the correct update profile page
        btnUpdate.setOnClickListener(v -> {

            User currentUser = spm.getUser();

            if (currentUser != null &&
                    currentUser.getRole() != null &&
                    currentUser.getRole().equalsIgnoreCase("driver")) {

                startActivity(new Intent(ProfileActivity.this,
                        UpdateDriverProfile.class));

            } else {

                startActivity(new Intent(ProfileActivity.this,
                        UpdatePassengerProfile.class));
            }
        });

        // Logout
        btnLogout.setOnClickListener(v -> {

            spm.logout();

            Intent intent = new Intent(ProfileActivity.this,
                    LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}