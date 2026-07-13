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
    private String myUsername;
    private String receiverUsername = "Them";

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
        int driverId = getIntent().getIntExtra("DRIVER_ID", -1);

        SharedPrefManager spm = new SharedPrefManager(this);
        myUserId = spm.getUser().getId();
        myUsername = spm.getUser().getUsername();

        if (driverId <= 0 && rideId > 0) {
            // If driverId is missing, fetch it from the ride details
            fetchRideDetailsAndInit(rideId, passengerId);
        } else {
            initializeChat(driverId, passengerId);
        }

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void fetchRideDetailsAndInit(int rideId, int passengerId) {
        ApiUtils.getRideService().getRideById(rideId).enqueue(new Callback<com.example.kucingputeh.model.Ride>() {
            @Override
            public void onResponse(Call<com.example.kucingputeh.model.Ride> call, Response<com.example.kucingputeh.model.Ride> response) {
                int dId = -1;
                if (response.isSuccessful() && response.body() != null) {
                    dId = response.body().getDriverId();
                }
                initializeChat(dId, passengerId);
            }

            @Override
            public void onFailure(Call<com.example.kucingputeh.model.Ride> call, Throwable t) {
                initializeChat(-1, passengerId);
            }
        });
    }

    private void initializeChat(int driverId, int passengerId) {
        if (myUserId == passengerId) {
            receiverId = driverId;
        } else if (myUserId == driverId) {
            receiverId = passengerId;
        } else {
            receiverId = (myUserId == passengerId || passengerId == -1) ? driverId : passengerId;
        }

        if (receiverId <= 0) {
            Toast.makeText(this, "Error: Could not identify chat recipient", Toast.LENGTH_SHORT).show();
            Log.e("CHAT_ERR", "receiverId is " + receiverId + ". RideID: " + rideId + ", PassID: " + passengerId + ", DriverID: " + driverId);
        } else {
            fetchReceiverName(receiverId);
            loadMessages();
        }
    }

    private void fetchReceiverName(int id) {
        ApiUtils.getUserService().getUserById(id).enqueue(new Callback<com.example.kucingputeh.model.User>() {
            @Override
            public void onResponse(Call<com.example.kucingputeh.model.User> call, Response<com.example.kucingputeh.model.User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    receiverUsername = response.body().getUsername();
                    // Set title to include receiver's name
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Chat with " + receiverUsername);
                    }
                    // Refresh the message display to show the correct name
                    loadMessages();
                }
            }

            @Override
            public void onFailure(Call<com.example.kucingputeh.model.User> call, Throwable t) {
                Log.e("CHAT_ERR", "Failed to fetch receiver name: " + t.getMessage());
            }
        });
    }

    private void loadMessages() {
        if (receiverId <= 0) return;
        
        ChatService chatService = ApiUtils.getChatService();
        final JSONArray[] combinedResults = new JSONArray[2];
        final boolean[] callFinished = {false, false};

        // Call 1: ME to THEM
        chatService.getMessages(myUserId, receiverId, rideId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    combinedResults[0] = response.isSuccessful() && response.body() != null ? 
                        new JSONArray(response.body().string()) : new JSONArray();
                } catch (Exception e) { combinedResults[0] = new JSONArray(); }
                callFinished[0] = true;
                checkAndDisplay(callFinished, combinedResults);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                combinedResults[0] = new JSONArray();
                callFinished[0] = true;
                checkAndDisplay(callFinished, combinedResults);
            }
        });

        // Call 2: THEM to ME
        chatService.getMessages(receiverId, myUserId, rideId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    combinedResults[1] = response.isSuccessful() && response.body() != null ? 
                        new JSONArray(response.body().string()) : new JSONArray();
                } catch (Exception e) { combinedResults[1] = new JSONArray(); }
                callFinished[1] = true;
                checkAndDisplay(callFinished, combinedResults);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                combinedResults[1] = new JSONArray();
                callFinished[1] = true;
                checkAndDisplay(callFinished, combinedResults);
            }
        });
    }

    private void checkAndDisplay(boolean[] finished, JSONArray[] results) {
        if (finished[0] && finished[1]) {
            try {
                chatContainer.removeAllViews();
                JSONArray all = new JSONArray();
                for (int i = 0; i < results[0].length(); i++) all.put(results[0].get(i));
                for (int i = 0; i < results[1].length(); i++) all.put(results[1].get(i));
                displayCombinedMessages(all);
            } catch (Exception e) {
                Log.e("CHAT_ERR", "Error merging: " + e.getMessage());
            }
        }
    }

    private void displayCombinedMessages(JSONArray jsonArray) throws Exception {
        // Sort array by 'id' if possible (assuming higher ID = newer)
        java.util.List<JSONObject> list = new java.util.ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) list.add(jsonArray.getJSONObject(i));
        
        // Sort by ID to keep conversation in order
        java.util.Collections.sort(list, (a, b) -> {
            try { return a.getInt("id") - b.getInt("id"); } catch (Exception e) { return 0; }
        });

        for (JSONObject obj : list) {
            int senderId = obj.getInt("sender_id");
            String msgText = obj.getString("message");

            TextView tvMessage = new TextView(ChatActivity.this);
            String displayName = (senderId == myUserId) ? myUsername : receiverUsername;
            tvMessage.setText(displayName + ": " + msgText);
            tvMessage.setTextSize(16);
            tvMessage.setPadding(16, 12, 16, 12);
            chatContainer.addView(tvMessage);
        }
        scrollChat.post(() -> scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
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
                    tvMessage.setText(myUsername + ": " + message);
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