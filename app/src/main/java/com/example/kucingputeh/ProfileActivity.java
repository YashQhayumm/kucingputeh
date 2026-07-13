package com.example.kucingputeh;

import android.content.Intent;
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
    private TextView etName, etEmail, etPhone, txtRating;
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
        txtRating = findViewById(R.id.txtRating);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

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

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        User user = spm.getUser();
        if (user != null) {
            etName.setText(user.getUsername());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());

            if (user.getRating() != null) {
                txtRating.setText(String.format("Rating: %.1f ★", user.getRating()));
            } else {
                txtRating.setText("Rating: N/A");
            }

            fetchFreshUserData(user.getId());
        }
    }

    private void fetchFreshUserData(int userId) {
        com.example.kucingputeh.remote.ApiUtils.getUserService().getUserById(userId).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(retrofit2.Call<User> call, retrofit2.Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User freshUser = response.body();
                    // Update UI (except rating, because fetch separately)
                    etName.setText(freshUser.getUsername());
                    etEmail.setText(freshUser.getEmail());
                    etPhone.setText(freshUser.getPhone());

                    // Fetch fresh rating from ratings table
                    com.example.kucingputeh.remote.ApiUtils.getRatingService().getRatingsByReviewee(userId).enqueue(new retrofit2.Callback<java.util.List<com.example.kucingputeh.model.Rating>>() {
                        @Override
                        public void onResponse(retrofit2.Call<java.util.List<com.example.kucingputeh.model.Rating>> call, retrofit2.Response<java.util.List<com.example.kucingputeh.model.Rating>> response) {
                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                double sum = 0;
                                for (com.example.kucingputeh.model.Rating r : response.body()) {
                                    sum += r.getScore();
                                }
                                double avg = sum / response.body().size();
                                txtRating.setText(String.format(java.util.Locale.US, "Rating: %.1f ★", avg));
                                freshUser.setRating(avg);
                            } else {
                                txtRating.setText("Rating: N/A");
                                freshUser.setRating(null);
                            }
                            spm.storeUser(freshUser);
                        }

                        @Override
                        public void onFailure(retrofit2.Call<java.util.List<com.example.kucingputeh.model.Rating>> call, Throwable t) {
                            // Even if rating fetch fails, store the user data we got
                            spm.storeUser(freshUser);
                        }
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<User> call, Throwable t) {
            }
        });
    }
}