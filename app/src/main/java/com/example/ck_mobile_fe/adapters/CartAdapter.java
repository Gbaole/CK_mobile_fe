package com.example.ck_mobile_fe.adapters;
import java.text.DecimalFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile_fe.R;
import com.example.ck_mobile_fe.models.CartResponse;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartResponse.CartItem> items;
    private OnItemClickListener listener;
    private final DecimalFormat df = new DecimalFormat("'$'#,###.##");
    public interface OnItemClickListener {
        void onDeleteClick(String productId);
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
        holder.qty.setText("Qty: " + item.quantity);
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item.productId));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, qty;
        ImageView btnDelete;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tv_cart_name);
            price = v.findViewById(R.id.tv_cart_price);
            qty = v.findViewById(R.id.tv_cart_qty);
            btnDelete = v.findViewById(R.id.btn_cart_delete);
        }
    }
}