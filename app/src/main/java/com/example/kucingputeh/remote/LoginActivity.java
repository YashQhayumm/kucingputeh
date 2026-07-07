package com.example.kucingputeh.remote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kucingputeh.MainActivity;
import com.example.kucingputeh.R;
import com.example.kucingputeh.model.FailLogin;
import com.example.kucingputeh.model.User;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
    }

    public void loginClicked(View view) {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (validateLogin(username, password)) {
            doLogin(username, password);
        }
    }

    private void doLogin(String username, String password) {
        UserService userService = ApiUtils.getUserService();
        Call<User> call;

        // Pilih kaedah login yang betul
        if(username.contains("@")) {
            call = userService.loginEmail(username, password);
        } else {
            call = userService.login(username, password);
        }

        // Jalankan API call hanya sekali di sini
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (user.getToken() != null) {
                        displayToast("Login successful");

                        // Simpan data user
                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);

                        // Pindah ke MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        displayToast("Login error: No token received");
                    }
                } else {
                    // Kendalikan ralat (401, 404, dll)
                    try {
                        String errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                        displayToast(e.getError().getMessage());
                    } catch (Exception e) {
                        displayToast("Login failed with code: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayToast("Error connecting to server.");
                Log.e("LoginActivity", "Error: " + t.getMessage());
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}