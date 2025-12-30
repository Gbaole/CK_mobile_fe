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
import com.example.ck_mobile_fe.models.CategoryResponse;
import com.example.ck_mobile_fe.models.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private LinearLayout layoutChips;
    private RecyclerView rcvProducts;
    private ProductAdapter productAdapter;
    private List<ProductResponse.Product> productList = new ArrayList<>();
    private String selectedCategoryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ View
        layoutChips = findViewById(R.id.layout_chips);
        rcvProducts = findViewById(R.id.rcv_products);

        // 2. Thiết lập RecyclerView với ProductAdapter
        // Đảm bảo ProductAdapter của bạn nhận List<ProductResponse.Product>
        productAdapter = new ProductAdapter(this, productList);
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
        fetchProducts(null); // Load tất cả sản phẩm lần đầu
    }

    @Override
    protected void onResume() {
        super.onResume();
        HeaderView header = findViewById(R.id.header_view);
        if (header != null) {
            header.refreshAvatar();
        }
    }

    // --- PHẦN CATEGORIES ---
    private void fetchCategoriesFromServer() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
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

        addChipToLayout("All", null, true); // Chip mặc định

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
            fetchProducts(selectedCategoryId); // Gọi hàm lấy sản phẩm khi đổi chip
        });

        layoutChips.addView(chip);
    }

    // --- PHẦN PRODUCTS ---
    private void fetchProducts(String categoryId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ProductResponse> call;

        if (categoryId == null) {
            call = apiService.getAllProducts(); // GET /products
        } else {
            call = apiService.getProductsByCategory(categoryId); // GET /categories/{id}/products
        }

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body().data);
                    productAdapter.notifyDataSetChanged(); // Cập nhật giao diện
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