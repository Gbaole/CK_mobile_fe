package com.example.ck_mobile_fe.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.example.ck_mobile_fe.R;

public class ChipView extends AppCompatTextView {

    // Constructor 1: Dùng khi khởi tạo bằng code Java (new ChipView(context))
    public ChipView(@NonNull Context context) {
        super(context);
        init();
    }

    // Constructor 2: Dùng khi khai báo trong file XML
    public ChipView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Constructor 3: Dùng khi khai báo trong XML có kèm Style
    public ChipView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Căn giữa chữ
        setGravity(Gravity.CENTER);

        // Tính toán padding (8dp dọc, 20dp ngang) để cân đối
        float density = getResources().getDisplayMetrics().density;
        int py = (int) (8 * density);
        int px = (int) (20 * density);
        setPadding(px, py, px, py);

        // Định dạng text
        setTextSize(14);
        setTypeface(null, android.graphics.Typeface.BOLD);

        // Nạp background từ file drawable/chip_background.xml
        setBackgroundResource(R.drawable.chip_background);

        // Nạp màu chữ từ file color/chip_text_color.xml (Selector)
        if (getContext() != null) {
            setTextColor(ContextCompat.getColorStateList(getContext(), R.color.chip_text_color));
        }

        // Đảm bảo Chip có thể click được
        setClickable(true);
        setFocusable(true);
    }

    /**
     * Hàm thay đổi trạng thái Active/Inactive của Chip.
     * Khi gọi hàm này, Selector trong XML sẽ tự động đổi màu.
     */
    public void setActive(boolean isActive) {
        // setSelected thay đổi trạng thái state_selected
        setSelected(isActive);

        // refreshDrawableState thông báo cho hệ thống vẽ lại màu sắc dựa trên state mới
        refreshDrawableState();
    }
}