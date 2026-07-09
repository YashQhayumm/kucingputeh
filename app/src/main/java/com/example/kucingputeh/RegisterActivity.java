package com.example.kucingputeh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kucingputeh.remote.ApiUtils;
import com.example.kucingputeh.remote.LoginActivity;
import com.example.kucingputeh.remote.UserService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtPlateNumber, edtVehicleModel, edtPhone;
    private RadioGroup rgRole;
    private Button btnRegister;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPlateNumber = findViewById(R.id.edtPlateNumber);
        edtVehicleModel = findViewById(R.id.edtVehicleModel);
        edtPhone = findViewById(R.id.edtPhone);
        rgRole = findViewById(R.id.rgRole);
        btnRegister = findViewById(R.id.btnRegister);

        userService = ApiUtils.getUserService(); // Guna service yang sama
        btnRegister.setOnClickListener(v -> performRegister());
    }

    private void performRegister() {
       //from input user
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String plate = edtPlateNumber.getText().toString().trim();
        String model = edtVehicleModel.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // radiobutton
        rgRole = findViewById(R.id.rgRole);
        EditText edtPlateNumber = findViewById(R.id.edtPlateNumber);
        EditText edtVehicleModel = findViewById(R.id.edtVehicleModel);

        int selectedId = rgRole.getCheckedRadioButtonId();
        RadioButton rbSelected = findViewById(selectedId);
        String role = rbSelected.getText().toString().toLowerCase();

        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDriver) {
                // for driver only
                edtPlateNumber.setVisibility(View.VISIBLE);
                edtVehicleModel.setVisibility(View.VISIBLE);
            } else {
                //passenger not allwed to fill in
                edtPlateNumber.setVisibility(View.GONE);
                edtVehicleModel.setVisibility(View.GONE);
            }
        });

        userService.registerUser(name, email, password, role, plate, model, phone)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            // login page after successfull register
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}