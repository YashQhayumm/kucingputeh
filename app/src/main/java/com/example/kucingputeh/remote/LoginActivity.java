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
import com.example.kucingputeh.R; // Import R yang betul
import com.example.kucingputeh.model.FailLogin;
import com.example.kucingputeh.model.User;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private static final String TAG = "LoginActivity_Debug";

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
            Log.d(TAG, "Attempting login for: " + username);
            doLogin(username, password);
        }
    }

    private void doLogin(String username, String password) {
        UserService userService = ApiUtils.getUserService();
        Call<User> call;

        if (username.contains("@")) {
            call = userService.loginEmail(username, password);
        } else {
            call = userService.login(username, password);
        }

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Response successfully received.");
                    User user = response.body();
                    if (user != null && user.getToken() != null) {
                        PrefManager spm = new PrefManager(getApplicationContext());
                        spm.storeUser(user);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        displayToast("Login successful but user data is missing");
                    }
                } else {
                    Log.e(TAG, "Server error. Code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorResp = response.errorBody().string();
                            Log.e(TAG, "Error content: " + errorResp);
                            FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                            displayToast(e.getError().getMessage());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        displayToast("Error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Failed to connect to server: ", t);
                displayToast("Connection failed: " + t.getMessage());
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            displayToast("Please enter username and password");
            return false;
        }
        return true;
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}