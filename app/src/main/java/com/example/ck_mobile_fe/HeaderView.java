package com.example.ck_mobile_fe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.ck_mobile_fe.utils.TokenManager;

public class HeaderView extends RelativeLayout {

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_header, this, true);
    }
    public void refreshAvatar() {
        ImageView imgAvatar = findViewById(R.id.img_avatar);
        if (imgAvatar == null) return;

        SharedPreferences sp = getContext().getSharedPreferences("LoginPref", Context.MODE_PRIVATE);
        String avatarUrl = sp.getString("avatar", null);

        // Map ảnh bằng Glide
        Glide.with(getContext())
                .load(avatarUrl) // Nếu null hoặc rỗng Glide sẽ tự hiểu
                .placeholder(R.drawable.ic_default_avatar) // Đang tải
                .error(R.drawable.ic_default_avatar)       // Lỗi hoặc link null
                .circleCrop() // Đảm bảo ảnh luôn tròn
                .into(imgAvatar);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ImageView imgAvatar = findViewById(R.id.img_avatar);

        // --- LOAD ẢNH AVATAR ---
        loadUserAvatar(imgAvatar);

        // --- XỬ LÝ CLICK ---
        if (imgAvatar != null) {
            imgAvatar.setOnClickListener(v -> {
                Context context = v.getContext();
                TokenManager tokenManager = new TokenManager(context);

                Intent intent;
                if (tokenManager.getToken() != null) {
                    intent = new Intent(context, ProfileActivity.class);
                } else {
                    intent = new Intent(context, AuthActivity.class);
                }

                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            });
        }

        // --- NÚT GIỎ HÀNG ---
        View btnCart = findViewById(R.id.btn_cart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            });
        }
    }

    private void loadUserAvatar(ImageView imageView) {
        if (imageView == null) return;

        // Lấy avatar từ file LoginPref
        SharedPreferences sp = getContext().getSharedPreferences("LoginPref", Context.MODE_PRIVATE);
        String avatarUrl = sp.getString("avatar", null);

        Glide.with(getContext())
                .load(avatarUrl) // Nếu avatarUrl là null hoặc rỗng, Glide sẽ nhảy vào .error()
                .placeholder(R.drawable.ic_default_avatar) // Ảnh hiện trong lúc chờ load
                .error(R.drawable.ic_default_avatar)       // Ảnh hiện nếu link null/lỗi
                .circleCrop() // Bo tròn ảnh nếu img_avatar không phải là CircleImageView
                .into(imageView);
    }
}