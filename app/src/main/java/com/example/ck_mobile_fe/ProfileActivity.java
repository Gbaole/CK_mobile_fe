package com.example.ck_mobile_fe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ nút Close
        ImageView btnClose = findViewById(R.id.btn_close);

        // Xử lý sự kiện click để quay về
        btnClose.setOnClickListener(v -> {
            // Cách 1: Đóng activity hiện tại để lộ ra MainActivity bên dưới
//            finish();

            // Cách 2: Nếu bạn muốn dùng Intent tường minh (ít dùng cho nút đóng)
             Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
             startActivity(intent);
        });
    }
}