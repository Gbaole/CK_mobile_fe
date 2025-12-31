package com.example.ck_mobile_fe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile_fe.adapters.ProductAdapter;
import com.example.ck_mobile_fe.api.ApiService;
import com.example.ck_mobile_fe.api.RetrofitClient;
import com.example.ck_mobile_fe.components.ChipView;
import com.example.ck_mobile_fe.models.CartResponse;
import com.example.ck_mobile_fe.models.CategoryResponse;
import com.example.ck_mobile_fe.models.ProductResponse;
import com.example.ck_mobile_fe.utils.TokenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private LinearLayout layoutChips;
    private RecyclerView rcvProducts;
    private ProductAdapter productAdapter;
    private List<ProductResponse.Product> productList = new ArrayList<>();
    private String selectedCategoryId = null;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tokenManager = new TokenManager(this);

        // 1. Ánh xạ View
        layoutChips = findViewById(R.id.layout_chips);
        rcvProducts = findViewById(R.id.rcv_products);

        // 2. Thiết lập RecyclerView với ProductAdapter và sự kiện Add to Cart
        productAdapter = new ProductAdapter(this, productList, product -> {
            // Khi bấm nút add trên item, gọi hàm xử lý API
            addProductToCart(product.id);
        });
        rcvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rcvProducts.setAdapter(productAdapter);

        // 3. Xử lý tràn viền
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 4. Khởi chạy dữ liệu ban đầu
        fetchCategoriesFromServer();
        fetchProducts(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HeaderView header = findViewById(R.id.header_view);
        if (header != null) {
            header.refreshAvatar();
        }
    }

    // --- PHẦN GIỎ HÀNG (ADD TO CART) ---
    private void addProductToCart(String productId) {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để mua hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Tạo body JSON: { "productId": "...", "quantity": 1 }
        Map<String, Object> body = new HashMap<>();
        body.put("productId", productId);
        body.put("quantity", 1);

        String authHeader = "Bearer " + token;

        apiService.addToCart(authHeader, body).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    // Optional: Cập nhật Badge trên icon giỏ hàng nếu có
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi: Không thể thêm sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- PHẦN CATEGORIES ---
    private void fetchCategoriesFromServer() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderCategories(response.body().data);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("API_ERROR", "Categories fail: " + t.getMessage());
            }
        });
    }

    private void renderCategories(List<CategoryResponse.Category> categories) {
        if (layoutChips == null) return;
        layoutChips.removeAllViews();

        addChipToLayout("All", null, true);

        for (CategoryResponse.Category cat : categories) {
            addChipToLayout(cat.name, cat.id, false);
        }
    }

    private void addChipToLayout(String name, String id, boolean isActive) {
        ChipView chip = new ChipView(this);
        chip.setText(name);
        chip.setTag(id);
        chip.setActive(isActive);

        chip.setOnClickListener(v -> {
            for (int i = 0; i < layoutChips.getChildCount(); i++) {
                View child = layoutChips.getChildAt(i);
                if (child instanceof ChipView) ((ChipView) child).setActive(false);
            }
            chip.setActive(true);

            selectedCategoryId = (String) chip.getTag();
            fetchProducts(selectedCategoryId);
        });

        layoutChips.addView(chip);
    }

    // --- PHẦN PRODUCTS ---
    private void fetchProducts(String categoryId) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<ProductResponse> call;

        if (categoryId == null) {
            call = apiService.getAllProducts();
        } else {
            call = apiService.getProductsByCategory(categoryId);
        }

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body().data);
                    productAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Cannot fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("API_ERROR", "Products fail: " + t.getMessage());
            }
        });
    }
}