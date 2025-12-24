package com.example.ck_mobile_fe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class HeaderView extends RelativeLayout {

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_header, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View avatar = findViewById(R.id.img_avatar);
        if (avatar != null) {
            avatar.setOnClickListener(v -> {
                // Lấy context trực tiếp từ view đang được click
                Context context = v.getContext();

                Intent intent = new Intent(context, ProfileActivity.class);

                // Đảm bảo Flag luôn có nếu context không phải Activity
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                context.startActivity(intent);


            });
        }
        View btnCart = findViewById(R.id.btn_cart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            });
        }
    }
}