package com.example.ck_mobile_fe;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    LinearLayout layoutWelcome, layoutSignin, layoutRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        layoutWelcome = findViewById(R.id.layout_welcome);
        layoutSignin = findViewById(R.id.layout_signin);
        layoutRegister = findViewById(R.id.layout_register);

        // --- Từ Welcome đi tiếp ---
        findViewById(R.id.btn_go_to_signin).setOnClickListener(v -> showView(layoutSignin));
        findViewById(R.id.btn_go_to_register).setOnClickListener(v -> showView(layoutRegister));

        // --- Nút Back ---
        findViewById(R.id.btn_back_from_signin).setOnClickListener(v -> showView(layoutWelcome));
        findViewById(R.id.btn_back_from_register).setOnClickListener(v -> showView(layoutWelcome));

        // --- Chuyển đổi qua lại giữa Sign In & Register ---
        findViewById(R.id.tv_switch_to_register).setOnClickListener(v -> showView(layoutRegister));
        findViewById(R.id.tv_switch_to_signin).setOnClickListener(v -> showView(layoutSignin));

        // Nút X (Đóng)
        findViewById(R.id.btn_close_auth).setOnClickListener(v -> finish());
    }

    private void showView(View viewToShow) {
        layoutWelcome.setVisibility(View.GONE);
        layoutSignin.setVisibility(View.GONE);
        layoutRegister.setVisibility(View.GONE);
        viewToShow.setVisibility(View.VISIBLE);
    }
}