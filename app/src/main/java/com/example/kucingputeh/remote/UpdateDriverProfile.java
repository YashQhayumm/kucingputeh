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

public class UpdateDriverProfile extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtName, edtPlateNumber, edtVehicleModel, edtPhone;
    private Button btnUpdate;
    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_driver);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtName);
        edtPlateNumber = findViewById(R.id.edtPlateNumber);
        edtVehicleModel = findViewById(R.id.edtVehicleModel);
        edtPhone = findViewById(R.id.edtPhone);
        btnUpdate = findViewById(R.id.btnUpdatePassengerProfile);

        spm = new SharedPrefManager(this);
        loadDriverData();

        btnUpdate.setOnClickListener(v -> updateDriverProfile());
    }

    private void loadDriverData() {
        User user = spm.getUser();
        if (user != null) {
            edtName.setText(user.getUsername());
            edtEmail.setText(user.getEmail());
            edtPlateNumber.setText(user.getPlateNumber());
            edtVehicleModel.setText(user.getVehicleModel());
            edtPhone.setText(user.getPhone());
        }
    }

    private void updateDriverProfile() {
        User driverUpdate = new User();
        driverUpdate.setUsername(edtName.getText().toString().trim());
        driverUpdate.setEmail(edtEmail.getText().toString().trim());
        driverUpdate.setPassword(edtPassword.getText().toString().trim());
        driverUpdate.setPlateNumber(edtPlateNumber.getText().toString().trim());
        driverUpdate.setVehicleModel(edtVehicleModel.getText().toString().trim());
        driverUpdate.setPhone(edtPhone.getText().toString().trim());

        String token = spm.getUser().getToken();

        DriverService service = ApiUtils.getDriverService();
        service.updateDriverProfile("Bearer " + token, driverUpdate).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateDriverProfile.this, "Profile Successfully Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateDriverProfile.this, "Failed To Update Profile: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UpdateDriverProfile.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}