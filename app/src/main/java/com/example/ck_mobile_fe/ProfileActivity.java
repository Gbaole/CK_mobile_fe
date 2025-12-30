package com.example.ck_mobile_fe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, btnSignOut;
    private CircleImageView imgProfile;
    private ImageView btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Ánh xạ View
        btnClose = findViewById(R.id.btn_close);
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        imgProfile = findViewById(R.id.profile_image);
        btnSignOut = findViewById(R.id.btn_sign_out);

        // 2. Lấy dữ liệu từ SharedPreferences
        // Lưu ý: Tên file "EliteStorage" phải khớp với TokenManager bạn đã tạo
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "Gamer");
        String email = sharedPreferences.getString("email", "no-email@obsidian.game");
        String avatarUrl = sharedPreferences.getString("avatar", "");

        // 3. Map dữ liệu vào View
        tvName.setText(name);
        tvEmail.setText(email);

        // Dùng Glide để load ảnh, placeholder/error là icon default
        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_default_avatar) // Ảnh hiển thị khi đang load
                .error(R.drawable.ic_default_avatar)       // Ảnh hiển thị nếu URL lỗi/trống
                .into(imgProfile);

        // 4. Xử lý sự kiện
        btnClose.setOnClickListener(v -> finish());

        btnSignOut.setOnClickListener(v -> {
            // Xóa sạch bộ nhớ local
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Về màn hình chính và đóng Profile
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}