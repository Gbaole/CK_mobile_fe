package com.example.ck_mobile_fe.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile_fe.R;
import com.example.ck_mobile_fe.models.OrderResponse;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<OrderResponse.OrderDTO> list;
    private final DecimalFormat df = new DecimalFormat("'$'#,###.##");

    public OrderAdapter(List<OrderResponse.OrderDTO> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đảm bảo tên layout đúng là item_order_card
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse.OrderDTO order = list.get(position);

        // ID: Lấy 8 ký tự cuối cho gọn
        holder.tvId.setText("#ORD-" + order.id.substring(order.id.length() - 8).toUpperCase());

        // Status
        holder.tvStatus.setText(order.status.toUpperCase());

        // Date
        holder.tvDate.setText(order.createdAt.split("T")[0]);

        // Address
        holder.tvAddress.setText("Delivery to: " + order.shippingAddress);

        // Total
        holder.tvTotal.setText(df.format(order.totalPrice));

        // Tóm tắt sản phẩm (Items Summary)
        if (order.items != null && !order.items.isEmpty()) {
            StringBuilder summary = new StringBuilder();
            // Lấy tối đa 2 sản phẩm đầu tiên để hiển thị tóm tắt
            summary.append(order.items.get(0).quantity).append("x ").append(order.items.get(0).name);

            if (order.items.size() > 1) {
                summary.append("\n").append(order.items.get(1).quantity).append("x ").append(order.items.get(1).name);
            }

            if (order.items.size() > 2) {
                summary.append("\n... and ").append(order.items.size() - 2).append(" more items");
            }
            holder.tvItemsSummary.setText(summary.toString());
        }
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvStatus, tvDate, tvTotal, tvAddress, tvItemsSummary;
        public ViewHolder(View v) {
            super(v);
            tvId = v.findViewById(R.id.tv_order_id);
            tvStatus = v.findViewById(R.id.tv_order_status);
            tvDate = v.findViewById(R.id.tv_order_date);
            tvTotal = v.findViewById(R.id.tv_order_total);
            tvAddress = v.findViewById(R.id.tv_order_address);
            tvItemsSummary = v.findViewById(R.id.tv_items_summary);
        }
    }
}