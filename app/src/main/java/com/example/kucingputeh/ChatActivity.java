package com.example.kucingputeh;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.ChatService;
import com.example.kucingputeh.remote.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout chatContainer;
    private EditText etMessage;
    private Button btnSend;
    private ScrollView scrollChat;

    private int rideId;
    private int passengerId;
    private int myUserId;
    private int receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatContainer = findViewById(R.id.chatContainer);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        scrollChat = findViewById(R.id.scrollChat);

        if (findViewById(R.id.spinnerFrom) != null) findViewById(R.id.spinnerFrom).setVisibility(View.GONE);
        if (findViewById(R.id.spinnerTo) != null) findViewById(R.id.spinnerTo).setVisibility(View.GONE);

        rideId = getIntent().getIntExtra("RIDE_ID", -1);
        passengerId = getIntent().getIntExtra("PASSENGER_ID", -1);

        // Fixed: No getInstance
        myUserId = new SharedPrefManager(this).getUser().getId();

        if (myUserId == passengerId || passengerId == -1) {
            receiverId = getIntent().getIntExtra("DRIVER_ID", 2);
        } else {
            receiverId = passengerId;
        }

        btnSend.setOnClickListener(v -> sendMessage());

        loadMessages();
    }

    private void loadMessages() {
        ChatService chatService = ApiUtils.getChatService();
        // Added rideId parameter
        chatService.getMessages(myUserId, receiverId, rideId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        chatContainer.removeAllViews();
                        String jsonResponse = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonResponse);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int senderId = obj.getInt("sender_id");
                            String msgText = obj.getString("message");

                            TextView tvMessage = new TextView(ChatActivity.this);
                            if (senderId == myUserId) {
                                tvMessage.setText("Me: " + msgText);
                            } else {
                                tvMessage.setText("Them: " + msgText);
                            }
                            tvMessage.setTextSize(16);
                            tvMessage.setPadding(16, 12, 16, 12);
                            chatContainer.addView(tvMessage);
                        }
                        scrollChat.post(() -> scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
                    } catch (Exception e) {
                        Log.e("CHAT_ERR", "Error parsing response data: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("CHAT_ERR", "Failed to load chats: " + t.getMessage());
            }
        });
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();

        if (message.isEmpty()) {
            etMessage.setError("Please enter message");
            return;
        }

        ChatService chatService = ApiUtils.getChatService();
        // Added rideId parameter
        chatService.sendMessage(myUserId, receiverId, message, rideId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    TextView tvMessage = new TextView(ChatActivity.this);
                    tvMessage.setText("Me: " + message);
                    tvMessage.setTextSize(16);
                    tvMessage.setPadding(16, 12, 16, 12);

                    chatContainer.addView(tvMessage);
                    etMessage.setText("");

                    scrollChat.post(() -> scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to send to server. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                Log.e("CHAT_ERR", t.getMessage());
            }
        });
    }
}