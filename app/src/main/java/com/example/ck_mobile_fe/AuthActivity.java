package com.example.ck_mobile_fe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ck_mobile_fe.api.ApiService;
import com.example.ck_mobile_fe.api.RetrofitClient;
import com.example.ck_mobile_fe.models.LoginResponse; // Bạn cần tạo model này
import com.example.ck_mobile_fe.utils.TokenManager; // Bạn cần tạo class này

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {
    LinearLayout layoutWelcome, layoutSignin, layoutRegister;
    EditText edtEmailSignin, edtPasswordSignin;
    Button btnSignIn;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        tokenManager = new TokenManager(this);

        // Ánh xạ View
        layoutWelcome = findViewById(R.id.layout_welcome);
        layoutSignin = findViewById(R.id.layout_signin);
        layoutRegister = findViewById(R.id.layout_register);

        // Ánh xạ các trường nhập liệu của Sign In (nhớ đặt ID trong XML tương ứng)
        edtEmailSignin = layoutSignin.findViewById(R.id.edt_email_signin);
        edtPasswordSignin = layoutSignin.findViewById(R.id.edt_password_signin);
        btnSignIn = layoutSignin.findViewById(R.id.btn_signin_submit);

        // Xử lý chuyển đổi View
        findViewById(R.id.btn_go_to_signin).setOnClickListener(v -> showView(layoutSignin));
        findViewById(R.id.btn_go_to_register).setOnClickListener(v -> showView(layoutRegister));
        findViewById(R.id.btn_back_from_signin).setOnClickListener(v -> showView(layoutWelcome));
        findViewById(R.id.btn_back_from_register).setOnClickListener(v -> showView(layoutWelcome));
        findViewById(R.id.tv_switch_to_register).setOnClickListener(v -> showView(layoutRegister));
        findViewById(R.id.tv_switch_to_signin).setOnClickListener(v -> showView(layoutSignin));
        findViewById(R.id.btn_close_auth).setOnClickListener(v -> finish());

        // Xử lý Login
        btnSignIn.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String email = edtEmailSignin.getText().toString().trim();
        String password = edtPasswordSignin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo body request khớp với API: identifier và password
        Map<String, String> loginData = new HashMap<>();
        loginData.put("identifier", email);
        loginData.put("password", password);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.login(loginData).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();

                    // Lưu vào Local Storage (SharedPreferences)
                    tokenManager.saveUser(
                            res.data.token,
                            res.data.user.name,
                            res.data.user.email,
                            res.data.user.avatarURL
                    );

                    Toast.makeText(AuthActivity.this, res.message, Toast.LENGTH_SHORT).show();

                    // Chuyển hướng sang ProfileActivity
                    Intent intent = new Intent(AuthActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish(); // Đóng màn hình Auth
                } else {
                    Toast.makeText(AuthActivity.this, "Login Failed: Check email/password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("API_LOGIN", "Error: " + t.getMessage());
                Toast.makeText(AuthActivity.this, "Server Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showView(View viewToShow) {
        layoutWelcome.setVisibility(View.GONE);
        layoutSignin.setVisibility(View.GONE);
        layoutRegister.setVisibility(View.GONE);
        viewToShow.setVisibility(View.VISIBLE);
    }
}