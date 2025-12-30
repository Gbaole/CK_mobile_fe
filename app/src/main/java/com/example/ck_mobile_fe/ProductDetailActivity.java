package com.example.ck_mobile_fe;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.ck_mobile_fe.api.ApiService;
import com.example.ck_mobile_fe.api.RetrofitClient;
import com.example.ck_mobile_fe.models.ProductDetailResponse;
import com.example.ck_mobile_fe.models.ProductResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgProduct;
    private TextView txtName, txtPrice, txtDescription, txtCategory;
    private TextView txtSold, txtStock, txtCondition;

    // 4 TextView tương ứng với 4 ô Specs trong XML
    private TextView txtSpecBrand, txtSpecStorage, txtSpecColor, txtSpecRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();

        String productId = getIntent().getStringExtra("PRODUCT_ID");
        if (productId != null) {
            fetchProductDetail(productId);
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_add_to_cart).setOnClickListener(v -> {
            Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews() {
        imgProduct = findViewById(R.id.img_detail_product);
        txtName = findViewById(R.id.txt_detail_name);
        txtPrice = findViewById(R.id.txt_detail_price);
        txtDescription = findViewById(R.id.txt_detail_description);
        txtCategory = findViewById(R.id.txt_detail_category);
        txtSold = findViewById(R.id.txt_detail_sold);
        txtStock = findViewById(R.id.txt_detail_stock);
        txtCondition = findViewById(R.id.txt_detail_condition);

        // Ánh xạ 4 ô Specs
        txtSpecBrand = findViewById(R.id.txt_spec_brand);
        txtSpecStorage = findViewById(R.id.txt_spec_storage);
        txtSpecColor = findViewById(R.id.txt_spec_color);
        txtSpecRegion = findViewById(R.id.txt_spec_region);
    }

    private void fetchProductDetail(String id) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getProductDetail(id).enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayDetail(response.body().data);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDetail(ProductResponse.Product product) {
        // 1. Thông tin cơ bản
        txtName.setText(product.name);
        txtPrice.setText("$" + product.price + ".00");
        txtDescription.setText(product.description);
        txtCategory.setText(product.category != null ? product.category.name.toUpperCase() : "UNKNOWN");

        // 2. Trạng thái và Kho (Sold & Stock)
        txtSold.setText("Sold: " + product.sold);
        txtCondition.setText("NEW");

        // Giả lập logic stock
        int fakeStock = 50 - product.sold;
        if (fakeStock > 0) {
            txtStock.setText("Stock: " + fakeStock);
            txtStock.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            txtStock.setText("Out of Stock");
            txtStock.setTextColor(Color.RED);
        }

        // 3. Map Specs (Brand + Specs Object: Storage, Color, Region)
        txtSpecBrand.setText(product.brand != null ? product.brand.name : "N/A");

        if (product.specs != null) {
            // Map dữ liệu từ object specs vào các TextView tương ứng
            txtSpecStorage.setText(product.specs.storage != null ? product.specs.storage : "N/A");
            txtSpecColor.setText(product.specs.color != null ? product.specs.color : "N/A");
            txtSpecRegion.setText(product.specs.region != null ? product.specs.region : "Global");
        } else {
            // Trường hợp sản phẩm không có object specs
            txtSpecStorage.setText("N/A");
            txtSpecColor.setText("N/A");
            txtSpecRegion.setText("N/A");
        }

        // 4. Load ảnh bằng Glide
        if (product.images != null && !product.images.isEmpty()) {
            Glide.with(this)
                    .load(product.images.get(0))
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgProduct);
        }
    }
}