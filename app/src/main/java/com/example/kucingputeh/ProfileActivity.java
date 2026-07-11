package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.model.User;
import com.example.kucingputeh.remote.LoginActivity;
import com.example.kucingputeh.remote.SharedPrefManager;
import com.example.kucingputeh.remote.UpdateDriverProfile;
import com.example.kucingputeh.remote.UpdatePassengerProfile;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText etName, etEmail, etPhone;
    private Button btnUpdate, btnLogout;
    private SharedPrefManager spm;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        spm = new SharedPrefManager(this);

        imgProfile = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        User user = spm.getUser();

        if (user != null) {
            etName.setText(user.getUsername());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imgProfile.setImageURI(uri);
                    }
                }
        );

        imgProfile.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnUpdate.setOnClickListener(v -> {

            User currentUser = spm.getUser();

            if (currentUser != null &&
                    currentUser.getRole() != null &&
                    currentUser.getRole().equalsIgnoreCase("driver")) {

                startActivity(new Intent(ProfileActivity.this, UpdateDriverProfile.class));

            } else {

                startActivity(new Intent(ProfileActivity.this, UpdatePassengerProfile.class));
            }
        });

        btnLogout.setOnClickListener(v -> {

            spm.logout();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}