package com.example.ck_mobile_fe;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ImageView btnClose = findViewById(R.id.btn_close_cart);
        btnClose.setOnClickListener(v -> finish());
    }
}