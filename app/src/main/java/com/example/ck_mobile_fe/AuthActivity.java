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
import com.example.ck_mobile_fe.models.LoginResponse;
import com.example.ck_mobile_fe.models.RegisterResponse;
import com.example.ck_mobile_fe.utils.TokenManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {
    LinearLayout layoutWelcome, layoutSignin, layoutRegister;
    EditText edtEmailSignin, edtPasswordSignin;
    EditText edtNameReg, edtEmailReg, edtPasswordReg, edtPhoneReg, edtAddressReg;
    Button btnSignIn, btnRegister;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        tokenManager = new TokenManager(this);

        // --- ÁNH XẠ VIEW ---
        layoutWelcome = findViewById(R.id.layout_welcome);
        layoutSignin = findViewById(R.id.layout_signin);
        layoutRegister = findViewById(R.id.layout_register);

        // Sign In
        edtEmailSignin = findViewById(R.id.edt_email_signin);
        edtPasswordSignin = findViewById(R.id.edt_password_signin);
        btnSignIn = findViewById(R.id.btn_signin_submit);

        // Register (Dùng findViewById trực tiếp nếu IDs là duy nhất trong layout)
        edtNameReg = findViewById(R.id.edt_name_register);
        edtEmailReg = findViewById(R.id.edt_email_register);
        edtPasswordReg = findViewById(R.id.edt_password_register);
        edtAddressReg = findViewById(R.id.edt_address_register);
        edtPhoneReg = findViewById(R.id.edt_phone_register);
        btnRegister = findViewById(R.id.btn_register_submit);

        // --- CHUYỂN ĐỔI VIEW ---
        findViewById(R.id.btn_go_to_signin).setOnClickListener(v -> showView(layoutSignin));
        findViewById(R.id.btn_go_to_register).setOnClickListener(v -> showView(layoutRegister));
        findViewById(R.id.btn_back_from_signin).setOnClickListener(v -> showView(layoutWelcome));
        findViewById(R.id.btn_back_from_register).setOnClickListener(v -> showView(layoutWelcome));
        findViewById(R.id.tv_switch_to_register).setOnClickListener(v -> showView(layoutRegister));
        findViewById(R.id.tv_switch_to_signin).setOnClickListener(v -> showView(layoutSignin));
        findViewById(R.id.btn_close_auth).setOnClickListener(v -> finish());

        // --- XỬ LÝ CLICK ---
        btnSignIn.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String name = edtNameReg.getText().toString().trim();
        String email = edtEmailReg.getText().toString().trim();
        String password = edtPasswordReg.getText().toString().trim();
        String phone = edtPhoneReg.getText().toString().trim();
        String address = edtAddressReg.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> regData = new HashMap<>();
        regData.put("name", name);
        regData.put("email", email);
        regData.put("password", password);
        regData.put("phoneNumber", phone);
        regData.put("shippingAddress", address);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.register(regData).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse res = response.body();
                    Toast.makeText(AuthActivity.this, res.message, Toast.LENGTH_LONG).show();

                    // 1. Chuyển sang layout đăng nhập
                    showView(layoutSignin);

                    // 2. Điền sẵn email vừa đăng ký qua ô login
                    edtEmailSignin.setText(email);

                    // 3. Xóa trắng form đăng ký
                    clearRegisterFields();
                } else {
                    Toast.makeText(AuthActivity.this, "Đăng ký thất bại: Email hoặc SĐT đã tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e("API_REG", t.getMessage());
                Toast.makeText(AuthActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm phụ dọn dẹp các ô nhập liệu
    private void clearRegisterFields() {
        edtNameReg.setText("");
        edtEmailReg.setText("");
        edtPasswordReg.setText("");
        edtPhoneReg.setText("");
        edtAddressReg.setText("");
    }

    private void handleLogin() {
        String email = edtEmailSignin.getText().toString().trim();
        String password = edtPasswordSignin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> loginData = new HashMap<>();
        loginData.put("identifier", email);
        loginData.put("password", password);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.login(loginData).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();

                    String token = res.data.token;
                    String name = res.data.user.name;
                    String email = res.data.user.email;
                    String avatar = res.data.user.avatarURL;
                    String userId = res.data.user.id;

                    tokenManager.saveUser(token, name, email, avatar, userId);
                    Toast.makeText(AuthActivity.this, "Chào mừng " + res.data.user.name, Toast.LENGTH_SHORT).show();

                    // Chuyển màn hình
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) { /* ... */ }
        });
    }
    private void showView(View viewToShow) {
        layoutWelcome.setVisibility(View.GONE);
        layoutSignin.setVisibility(View.GONE);
        layoutRegister.setVisibility(View.GONE);
        viewToShow.setVisibility(View.VISIBLE);
    }
}