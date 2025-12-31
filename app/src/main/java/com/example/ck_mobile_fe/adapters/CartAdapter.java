package com.example.ck_mobile_fe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile_fe.R;
import com.example.ck_mobile_fe.models.CartResponse;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartResponse.CartItem> items;
    private OnItemClickListener listener;
    private final DecimalFormat df = new DecimalFormat("'$'#,###.##");

    public interface OnItemClickListener {
        void onDeleteClick(String productId);
        void onUpdateQuantity(String productId, int newQuantity);
    }

    public CartAdapter(List<CartResponse.CartItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartResponse.CartItem item = items.get(position);

        holder.name.setText(item.name);
        holder.price.setText(df.format(item.price));
        holder.qty.setText(String.valueOf(item.quantity));

        // Xử lý nút tăng (+)
        holder.btnPlus.setOnClickListener(v ->
                listener.onUpdateQuantity(item.productId, item.quantity + 1)
        );

        // Xử lý nút giảm (-)
        holder.btnMinus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                listener.onUpdateQuantity(item.productId, item.quantity - 1);
            } else {
                // Nếu giảm xuống 0 thì kích hoạt xóa sản phẩm
                listener.onDeleteClick(item.productId);
            }
        });

        // Xử lý nút xóa hẳn
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item.productId));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, qty;
        ImageView btnDelete, btnPlus, btnMinus; // Khai báo thêm 2 nút mới ở đây

        public ViewHolder(View v) {
            super(v);
            // Ánh xạ View từ XML vào Java
            name = v.findViewById(R.id.tv_cart_name);
            price = v.findViewById(R.id.tv_cart_price);
            qty = v.findViewById(R.id.tv_cart_qty);
            btnDelete = v.findViewById(R.id.btn_cart_delete);
            btnPlus = v.findViewById(R.id.btn_plus);   // Ánh xạ nút cộng
            btnMinus = v.findViewById(R.id.btn_minus); // Ánh xạ nút trừ
        }
    }
}