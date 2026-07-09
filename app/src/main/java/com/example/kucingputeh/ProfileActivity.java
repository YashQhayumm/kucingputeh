package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.remote.LoginActivity;
import com.example.kucingputeh.remote.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {

    EditText etName, etStudentID, etEmail, etPassword;
    Button btnUpdate, btnLogout;
    SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        spm = new SharedPrefManager(getApplicationContext());

        etName = findViewById(R.id.etName);
        etStudentID = findViewById(R.id.etStudentID);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);

        btnUpdate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String studentID = etStudentID.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            Toast.makeText(ProfileActivity.this,
                    "Update Profile clicked",
                    Toast.LENGTH_SHORT).show();
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