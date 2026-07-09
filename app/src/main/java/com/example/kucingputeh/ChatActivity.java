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

        // Hide spinners since we are context-aware now
        if (findViewById(R.id.spinnerFrom) != null) findViewById(R.id.spinnerFrom).setVisibility(View.GONE);
        if (findViewById(R.id.spinnerTo) != null) findViewById(R.id.spinnerTo).setVisibility(View.GONE);

        rideId = getIntent().getIntExtra("RIDE_ID", -1);
        passengerId = getIntent().getIntExtra("PASSENGER_ID", -1);

        // Fixed: Instantiate using standard constructor 'new'
        myUserId = new SharedPrefManager(this).getUser().getId();

        // FAILSAFE: If PASSENGER_ID wasn't passed down, or if you are the passenger,
        // set the receiver to the DRIVER_ID. Otherwise, you are the driver, so talk to the passenger.
        if (myUserId == passengerId || passengerId == -1) {
            receiverId = getIntent().getIntExtra("DRIVER_ID", 2); // Defaults to user 2 if intent is completely empty
        } else {
            receiverId = passengerId;
        }

        // Quick debug check to see what IDs your app is sending to the DB
        Log.d("CHAT_DEBUG_IDS", "Sender ID: " + myUserId + " | Receiver ID: " + receiverId);

        btnSend.setOnClickListener(v -> sendMessage());

        // Load all older messages straight out of the database on launch
        loadMessages();
    }

    private void loadMessages() {
        ChatService chatService = ApiUtils.getChatService();
        chatService.getMessages(myUserId, receiverId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        chatContainer.removeAllViews(); // Clear view before loading
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

        // Send to Live Production Database
        ChatService chatService = ApiUtils.getChatService();
        chatService.sendMessage(myUserId, receiverId, message).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Locally update UI since DB processed it successfully
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