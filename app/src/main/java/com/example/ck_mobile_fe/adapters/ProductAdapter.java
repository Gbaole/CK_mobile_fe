package com.example.ck_mobile_fe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile_fe.R;
import com.example.ck_mobile_fe.models.Product; // PHẢI IMPORT MODEL Ở ĐÂY

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> list;

    public ProductAdapter(List<Product> list) {
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
        Product p = list.get(position);
        holder.txtName.setText(p.getName());
        holder.txtBrand.setText(p.getBrand());
        holder.txtPrice.setText("$" + p.getPrice());
        holder.txtRating.setText(String.valueOf(p.getRating()));
        holder.imgProduct.setImageResource(p.getImageRes()); // Load ảnh từ drawable
    }

    @Override
    public int getItemCount() { return list.size(); }

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