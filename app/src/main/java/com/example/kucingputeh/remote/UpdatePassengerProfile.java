package com.example.kucingputeh.remote;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kucingputeh.R;
import com.example.kucingputeh.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePassengerProfile extends AppCompatActivity {

    private EditText editEmail, edtPassword, edtName, edtPhone;
    private Button btnUpdate;
    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_passenger);

        spm = new SharedPrefManager(this);

        editEmail = findViewById(R.id.editEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        btnUpdate = findViewById(R.id.btnUpdatePassengerProfile);

        loadPassengerData();

        btnUpdate.setOnClickListener(v -> updatePassenger());
    }

    private void loadPassengerData() {
        User user = spm.getUser();
        if (user != null) {
            editEmail.setText(user.getEmail());
            edtName.setText(user.getUsername());
            edtPhone.setText(user.getPhone());
        }
    }

    private void updatePassenger() {

        String name = edtName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        User updatedUser = new User();
        updatedUser.setUsername(name);
        updatedUser.setEmail(email);
        updatedUser.setPassword(password);
        updatedUser.setPhone(phone);

        String token = spm.getUser().getToken();

        ApiUtils.getPassengerService().updatePassengerProfile("Bearer " + token, updatedUser)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            User user = spm.getUser();
                            user.setUsername(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            spm.storeUser(user);

                            Toast.makeText(UpdatePassengerProfile.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UpdatePassengerProfile.this, "Failed Updated: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(UpdatePassengerProfile.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}