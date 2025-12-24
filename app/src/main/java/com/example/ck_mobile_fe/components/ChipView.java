package com.example.ck_mobile_fe.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.example.ck_mobile_fe.R;

public class ChipView extends AppCompatTextView {

    public ChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        // Tính toán padding theo dp để không bị quá sát trên máy thật
        int py = (int) (8 * getResources().getDisplayMetrics().density);
        int px = (int) (20 * getResources().getDisplayMetrics().density);
        setPadding(px, py, px, py);

        setTextSize(14); // Chỉnh lại cho giống mẫu
        setTypeface(null, android.graphics.Typeface.BOLD);

        setBackgroundResource(R.drawable.chip_background);

        // Sửa lại dòng này để nạp ColorStateList chuẩn nhất
        setTextColor(ContextCompat.getColorStateList(getContext(), R.color.chip_text_color));


    }

    // Hàm để đổi text
    public void setChipText(String text) {
        setText(text);
    }

    // Hàm để đổi trạng thái Active/Inactive
    public void setActive(boolean isActive) {
        // setSelected là hàm gốc của Android để kích hoạt state_selected trong XML
        setSelected(isActive);

        // Dòng này cực kỳ quan trọng: Nó ép View phải kiểm tra lại file XML background và color
        refreshDrawableState();
    }
}