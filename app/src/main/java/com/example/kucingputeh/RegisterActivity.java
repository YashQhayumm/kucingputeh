package com.example.kucingputeh;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.LoginActivity;
import com.example.kucingputeh.remote.UserService;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    //register part
    private EditText edtName, edtEmail, edtPassword, edtPlateNumber, edtVehicleModel, edtPhone;
    private RadioGroup rgRole;
    private Button btnRegister;
    private UserService userService;

    private LinearLayout layoutDriverFields;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPlateNumber = findViewById(R.id.edtPlateNumber);
        edtVehicleModel = findViewById(R.id.edtVehicleModel);
        edtPhone = findViewById(R.id.edtPhone);
        rgRole = findViewById(R.id.rgRole);
        btnRegister = findViewById(R.id.btnRegister);

        layoutDriverFields = findViewById(R.id.layoutDriverFields);
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDriver) {
                layoutDriverFields.setVisibility(View.VISIBLE);
            } else {
                layoutDriverFields.setVisibility(View.GONE);
            }
        });

        userService = ApiUtils.getUserService();
        btnRegister.setOnClickListener(v -> performRegister());
    }

    private void performRegister() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // get Role
        int selectedId = rgRole.getCheckedRadioButtonId();
        RadioButton rbSelected = findViewById(selectedId);
        String role = rbSelected.getText().toString().toLowerCase();


        String plate = "";
        String model = "";
        if (selectedId == R.id.rbDriver) {
            plate = edtPlateNumber.getText().toString().trim();
            model = edtVehicleModel.getText().toString().trim();
        }

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hantar ke API
        // users is the auto-CRUD table route, so it stores whatever value it
        // receives as-is -- hash the password client-side the same way we do
        // for profile updates, to match the existing unsalted-MD5 scheme.
        userService.registerUser(name, email, md5(password), role, plate, model, phone)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Matches the unsalted MD5 hashing scheme already used for the password
     * column (visible in existing DB rows). The auto-CRUD "users" insert
     * route stores the password field as-is, with no server-side hashing.
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
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}