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

public class UpdateDriverProfile extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtName, edtPlateNumber, edtVehicleModel, edtPhone;
    private Button btnUpdate;
    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_driver);

        spm = new SharedPrefManager(this);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtName);
        edtPlateNumber = findViewById(R.id.edtPlateNumber);
        edtVehicleModel = findViewById(R.id.edtVehicleModel);
        edtPhone = findViewById(R.id.edtPhone);
        btnUpdate = findViewById(R.id.btnUpdateDriverProfile);

        loadDriverData();

        btnUpdate.setOnClickListener(v -> updateDriverProfile());
    }

    private void loadDriverData() {
        User user = spm.getUser();

        if (user != null) {
            edtName.setText(user.getUsername());
            edtEmail.setText(user.getEmail());
            edtPlateNumber.setText(user.getPlateNumber());
            edtVehicleModel.setText(user.getVehicleModel());
            edtPhone.setText(user.getPhone());
            // Password field is intentionally left blank -- leaving it blank means
            // "don't change the password" (see updateDriverProfile()).
        }
    }

    private void updateDriverProfile() {

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String plateNumber = edtPlateNumber.getText().toString().trim();
        String vehicleModel = edtVehicleModel.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Name is required");
            edtName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter a valid email address");
            edtEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Phone number is required");
            edtPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(plateNumber)) {
            edtPlateNumber.setError("Plate number is required");
            edtPlateNumber.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(vehicleModel)) {
            edtVehicleModel.setError("Car model is required");
            edtVehicleModel.requestFocus();
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

        // Same server behind driver/passenger updates: it does json_decode()
        // on the raw request body against the auto-CRUD users/{id} route, so
        // we send a clean JSON object with the exact database keys instead of
        // posting a full User object to a "driver/update" route that doesn't
        // exist on the server (that's what was causing "Failed To Update
        // Profile: 400").
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", name);
        updates.put("email", email);
        updates.put("phone_number", phone);
        updates.put("plate_number", plateNumber);
        updates.put("car_model", vehicleModel);
        if (!password.isEmpty()) {
            // users/{id} is the generic auto-CRUD route and does NOT hash the
            // password server-side. Existing password hashes in the DB are
            // unsalted MD5, so we match that here to keep login working.
            updates.put("password", md5(password));
        }

        ApiUtils.getDriverService().updateDriverProfile("Bearer " + token, currentUser.getId(), updates)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        btnUpdate.setEnabled(true);
                        if (response.isSuccessful()) {

                            User user = spm.getUser();
                            user.setUsername(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            user.setPlateNumber(plateNumber);
                            user.setVehicleModel(vehicleModel);
                            spm.storeUser(user);

                            edtPassword.setText("");
                            Toast.makeText(UpdateDriverProfile.this,
                                    "Profile Successfully Updated!",
                                    Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(UpdateDriverProfile.this,
                                    "Failed To Update Profile: " + errorMsg,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        btnUpdate.setEnabled(true);
                        Toast.makeText(UpdateDriverProfile.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Matches the unsalted MD5 hashing scheme already used for the password
     * column, same as UpdatePassengerProfile's md5().
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
            throw new RuntimeException(e);
        }
    }
}