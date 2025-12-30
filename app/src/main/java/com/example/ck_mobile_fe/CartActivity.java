package com.example.ck_mobile_fe;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import com.example.ck_mobile_fe.adapters.CartAdapter;
import com.example.ck_mobile_fe.api.ApiService;
import com.example.ck_mobile_fe.api.RetrofitClient;
import com.example.ck_mobile_fe.models.CartResponse;
import com.example.ck_mobile_fe.utils.TokenManager;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rcvCart;
    private TextView tvTotal;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        rcvCart = findViewById(R.id.rcv_cart);
        rcvCart.setLayoutManager(new LinearLayoutManager(this));

        tvTotal = findViewById(R.id.tv_total_price);

        findViewById(R.id.btn_close_cart).setOnClickListener(v -> finish());

        loadCart();
    }

    private void loadCart() {
        String token = "Bearer " + tokenManager.getToken();
        apiService.getMyCart(token).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse.CartData data = response.body().data;

                    // Cập nhật giá tiền
                    String formattedPrice = getFormattedPrice(data.totalPrice);
                    //  android.util.Log.d("CART_DEBUG", "Original: " + data.totalPrice + " -> Formatted: " + formattedPrice);
                    tvTotal.setText(formattedPrice);

                    // Đổ dữ liệu vào Adapter
                    CartAdapter adapter = new CartAdapter(data.items, productId -> {
                        removeProduct(productId);
                    });
                    rcvCart.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeProduct(String productId) {
        String token = "Bearer " + tokenManager.getToken();
        apiService.removeFromCart(token, productId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CartActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
                    loadCart(); // Tải lại giỏ hàng sau khi xóa
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {}
        });
    }
    private String getFormattedPrice(double price) {
        DecimalFormat df = new DecimalFormat("'$'#,###.##");
        return df.format(price);
    }
}
