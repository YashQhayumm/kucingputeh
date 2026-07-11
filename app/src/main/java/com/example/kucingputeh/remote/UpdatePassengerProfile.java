package com.example.kucingputeh.remote;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.R;
import com.example.kucingputeh.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePassengerProfile extends AppCompatActivity {

    private EditText editEmail, edtPassword, edtName, edtPhone;
    private Button btnUpdate;
    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_passenger);

        spm = new SharedPrefManager(this);

        editEmail = findViewById(R.id.editEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        btnUpdate = findViewById(R.id.btnUpdatePassengerProfile);

        loadPassengerData();

        btnUpdate.setOnClickListener(v -> updatePassenger());
    }

    private void loadPassengerData() {
        User user = spm.getUser();
        if (user != null) {
            editEmail.setText(user.getEmail());
            edtName.setText(user.getUsername());
            edtPhone.setText(user.getPhone());
            // Password field is intentionally left blank -- leaving it blank means
            // "don't change the password" (see updatePassenger()).
        }
    }

    private void updatePassenger() {

        String name = edtName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Name is required");
            edtName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter a valid email address");
            editEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Phone number is required");
            edtPhone.requestFocus();
            return;
        }
        if (!password.isEmpty() && password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
            return;
        }

        User currentUser = spm.getUser();
        if (currentUser == null || currentUser.getToken() == null) {
            Toast.makeText(this, "You must be logged in to update your profile.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnUpdate.setEnabled(false);
        String token = currentUser.getToken();

        // The server says "body is empty" with FormUrlEncoded, which means it
        // likely does json_decode() on the raw request body.
        // We use a Map to send a "clean" JSON object with exact database keys.
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", name);
        updates.put("email", email);
        updates.put("phone_number", phone); // Database key name
        if (!password.isEmpty()) {
            // users/{id} is the generic auto-CRUD route and does NOT hash the
            // password server-side (unlike users/register, which is a custom
            // endpoint). Existing password hashes in the DB are unsalted MD5,
            // so we match that here to keep login working after an update.
            updates.put("password", md5(password));
        }

        ApiUtils.getPassengerService().updatePassengerProfile("Bearer " + token, currentUser.getId(), updates)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        btnUpdate.setEnabled(true);
                        if (response.isSuccessful()) {

                            // Keep the locally cached user in sync (password is never
                            // stored locally, so there's nothing to update there).
                            User user = spm.getUser();
                            user.setUsername(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            spm.storeUser(user);

                            edtPassword.setText("");
                            Toast.makeText(UpdatePassengerProfile.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String errorMsg = "HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    String body = response.errorBody().string();
                                    if (!TextUtils.isEmpty(body)) {
                                        errorMsg += " - " + body;
                                    }
                                }
                            } catch (Exception e) {
                                // ignore, fall back to just the code
                            }
                            android.util.Log.e("PROFILE_UPDATE", "Failed: " + errorMsg);
                            Toast.makeText(UpdatePassengerProfile.this, "Failed Updated: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        btnUpdate.setEnabled(true);
                        Toast.makeText(UpdatePassengerProfile.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Matches the unsalted MD5 hashing scheme already used for the password
     * column (visible in existing DB rows, e.g. MD5("admin") ==
     * 21232f297a57a5a743894a0e4a801fc3). Only needed because the auto-CRUD
     * users/{id} route stores the password field as-is, with no server-side
     * hashing, unlike the users/register endpoint.
     */
    private static String md5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            // Should never happen -- MD5 and UTF-8 are always available on Android.
            throw new RuntimeException(e);
        }
    }
}