package com.example.kucingputeh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
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
    private TextView tvName, tvEmail, tvPhone;
    private Button btnEditProfile, btnLogout;
    private SharedPrefManager spm;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        spm = new SharedPrefManager(this);

        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        User user = spm.getUser();

        if (user != null) {
            tvName.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
            tvPhone.setText(user.getPhone());
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

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileChoiceActivity.class));
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