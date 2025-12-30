package com.example.ck_mobile_fe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ck_mobile_fe.components.ChipView;
import com.example.ck_mobile_fe.models.Product;
import com.example.ck_mobile_fe.adapters.ProductAdapter;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Xử lý tràn viền (System Bars)
        // Lưu ý: Đảm bảo id của layout ngoài cùng trong activity_main.xml là "main"
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // --- KHỞI TẠO THANH CHIPS ---
        setupCategoryChips();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 1. Dùng đúng ID trong XML là header_view
        HeaderView header = findViewById(R.id.header_view);
        if (header != null) {
            // 2. Gọi hàm refresh để load lại avatar mới nhất từ SharedPreferences
            header.refreshAvatar();
        }
    }
    private void setupCategoryChips() {
        LinearLayout layoutChips = findViewById(R.id.layout_chips);
        if (layoutChips == null) return;

        for (int i = 0; i < layoutChips.getChildCount(); i++) {
            View view = layoutChips.getChildAt(i);
            if (view instanceof ChipView) {
                ChipView chip = (ChipView) view;

                // 1. Mặc định chọn Chip đầu tiên (index 0)
                if (i == 0) {
                    chip.setActive(true);
                } else {
                    chip.setActive(false);
                }

                // 2. Xử lý click cho từng chip
                chip.setOnClickListener(v -> {
                    // Reset tất cả các chip khác
                    for (int j = 0; j < layoutChips.getChildCount(); j++) {
                        View other = layoutChips.getChildAt(j);
                        if (other instanceof ChipView) {
                            ((ChipView) other).setActive(false);
                        }
                    }
                    // Active chip hiện tại
                    chip.setActive(true);
                });
            }
        }
    }

}