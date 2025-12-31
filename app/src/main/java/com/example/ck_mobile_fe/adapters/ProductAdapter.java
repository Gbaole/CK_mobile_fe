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
    private OnAddToCartClickListener addToCartListener;

    public interface OnAddToCartClickListener {
        void onAddClick(ProductResponse.Product product);
    }

    public ProductAdapter(Context context, List<ProductResponse.Product> list, OnAddToCartClickListener listener) {
        this.context = context;
        this.list = list;
        this.addToCartListener = listener;
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

        holder.txtName.setText(p.name);
        holder.txtBrand.setText(p.brand != null ? p.brand.name : "Unknown");
        holder.txtPrice.setText("$" + p.price);
        holder.txtRating.setText("Sold: " + p.sold);

        if (p.images != null && !p.images.isEmpty()) {
            Glide.with(context)
                    .load(p.images.get(0))
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(holder.imgProduct);
        }

        // Chuyển sang màn hình chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", p.id);
            context.startActivity(intent);
        });

        // Xử lý nút thêm vào giỏ hàng
        holder.btnAddCart.setOnClickListener(v -> {
            if (addToCartListener != null) {
                addToCartListener.onAddClick(p); // Sửa 'product' thành 'p'
            }
        });
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnAddCart; // Phải khai báo btnAddCart ở đây
        TextView txtName, txtBrand, txtPrice, txtRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            txtName = itemView.findViewById(R.id.txt_product_name);
            txtBrand = itemView.findViewById(R.id.txt_brand);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtRating = itemView.findViewById(R.id.txt_rating);
            btnAddCart = itemView.findViewById(R.id.btn_add);
        }
    }
}