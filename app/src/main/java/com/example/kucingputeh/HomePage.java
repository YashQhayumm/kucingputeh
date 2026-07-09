package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kucingputeh.remote.LoginActivity; // Import LoginActivity anda

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Panggil layout yang anda buat tadi
        setContentView(R.layout.activity_homepage); // Pastikan nama fail XML anda di sini betul

        // 2. Setup button "Log In"
        Button btnGoToLogin = findViewById(R.id.btnGoToLogin);

        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3. Intent untuk pergi ke LoginActivity
                Intent intent = new Intent(HomePage.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}