package com.example.kucingputeh;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    private Spinner spinnerFrom, spinnerTo;
    private LinearLayout chatContainer;
    private EditText etMessage;
    private Button btnSend;
    private ScrollView scrollChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        chatContainer = findViewById(R.id.chatContainer);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        scrollChat = findViewById(R.id.scrollChat);

        String[] users = {
                "Passenger",
                "Driver"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                users
        );

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {

        String from = spinnerFrom.getSelectedItem().toString();
        String to = spinnerTo.getSelectedItem().toString();
        String message = etMessage.getText().toString().trim();

        if (message.isEmpty()) {
            etMessage.setError("Please enter message");
            return;
        }

        TextView tvMessage = new TextView(this);
        tvMessage.setText(from + " to " + to + ":\n" + message);
        tvMessage.setTextSize(16);
        tvMessage.setPadding(16, 12, 16, 12);

        chatContainer.addView(tvMessage);

        etMessage.setText("");

        scrollChat.post(() -> scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
    }
}