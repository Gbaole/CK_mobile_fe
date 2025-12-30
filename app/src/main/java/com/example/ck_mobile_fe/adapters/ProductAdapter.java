package com.example.ck_mobile_fe.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ck_mobile_fe.ProductDetailActivity;
import com.example.ck_mobile_fe.R;
import com.example.ck_mobile_fe.models.ProductResponse;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductResponse.Product> list;
    private Context context;

    public ProductAdapter(Context context, List<ProductResponse.Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductResponse.Product p = list.get(position);

        // 1. Gán dữ liệu Text
        holder.txtName.setText(p.name);
        holder.txtBrand.setText(p.brand != null ? p.brand.name : "Unknown");
        holder.txtPrice.setText("$" + p.price);
        holder.txtRating.setText("Sold: " + p.sold);

        // 2. Load ảnh từ URL bằng Glide
        if (p.images != null && !p.images.isEmpty()) {
            Glide.with(context)
                    .load(p.images.get(0))
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(holder.imgProduct);
        }

        // --- 3. CẬP NHẬT LỆNH CLICK TẠI ĐÂY ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            // Truyền ID sản phẩm để bên Activity Detail dùng nó gọi API
            intent.putExtra("PRODUCT_ID", p.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtBrand, txtPrice, txtRating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            txtName = itemView.findViewById(R.id.txt_product_name);
            txtBrand = itemView.findViewById(R.id.txt_brand);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtRating = itemView.findViewById(R.id.txt_rating);
        }
    }
}