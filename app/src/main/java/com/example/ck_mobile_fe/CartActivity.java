package com.example.ck_mobile_fe;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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
    private AppCompatButton btnCheckout;

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
        btnCheckout = findViewById(R.id.btn_checkout);

        btnCheckout.setOnClickListener(v -> handleCheckout());
    }
    private void handleCheckout() {
        String token = "Bearer " + tokenManager.getToken();
        String address = tokenManager.getAddress();

        // Kiểm tra nếu địa chỉ trống
        if (address == null || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng cập nhật địa chỉ giao hàng!", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("address", address);

        // Hiển thị trạng thái đang xử lý (tùy chọn)
        btnCheckout.setEnabled(false);
        btnCheckout.setText("Processing...");

        apiService.checkout(token, body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnCheckout.setEnabled(true);
                btnCheckout.setText("Checkout Now");

                if (response.isSuccessful()) {
                    Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();


                    finish();
                } else {
                    Toast.makeText(CartActivity.this, "Checkout thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnCheckout.setEnabled(true);
                btnCheckout.setText("Checkout Now");
                Toast.makeText(CartActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadCart() {
        String token = "Bearer " + tokenManager.getToken();
        apiService.getMyCart(token).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse.CartData data = response.body().data;

                    tvTotal.setText(getFormattedPrice(data.totalPrice));

                    // Cập nhật Adapter với 2 sự kiện
                    CartAdapter adapter = new CartAdapter(data.items, new CartAdapter.OnItemClickListener() {
                        @Override
                        public void onDeleteClick(String productId) {
                            removeProduct(productId);
                        }

                        @Override
                        public void onUpdateQuantity(String productId, int newQuantity) {
                            updateQuantity(productId, newQuantity);
                        }
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

    // Hàm cập nhật số lượng (Thay thế số lượng cũ bằng số lượng mới)
    private void updateQuantity(String productId, int quantity) {
        String token = "Bearer " + tokenManager.getToken();

        Map<String, Object> body = new HashMap<>();
        body.put("productId", productId);
        body.put("quantity", quantity);

        apiService.updateCartQuantity(token, body).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    loadCart(); // Tải lại để cập nhật tổng tiền và UI
                } else {
                    Toast.makeText(CartActivity.this, "Không thể cập nhật số lượng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
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
                    loadCart();
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi xóa sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFormattedPrice(double price) {
        DecimalFormat df = new DecimalFormat("'$'#,###.##");
        return df.format(price);
    }
}