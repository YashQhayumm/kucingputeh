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
import com.example.kucingputeh.model.User;

import java.util.List;

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
        Call<List<User>> call;

        if (username.contains("@")) {
            call = userService.loginEmail(username, password);
        } else {
            call = userService.login(username, password);
        }

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> userList = response.body();
                    if (!userList.isEmpty()) {
                        User user = userList.get(0);

                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        displayToast("Invalid username/email or password.");
                    }
                } else {
                    Log.e(TAG, "Server error. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e(TAG, "Failed to connect to server: ", t);
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username/Email is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
