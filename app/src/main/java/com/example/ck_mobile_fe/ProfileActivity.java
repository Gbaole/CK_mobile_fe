package com.example.ck_mobile_fe;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ck_mobile_fe.adapters.OrderAdapter;
import com.example.ck_mobile_fe.api.ApiService;
import com.example.ck_mobile_fe.api.RetrofitClient;
import com.example.ck_mobile_fe.models.LoginResponse;
import com.example.ck_mobile_fe.models.OrderResponse;
import com.example.ck_mobile_fe.utils.TokenManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, btnSignOut;
    private CircleImageView imgProfile;
    private ImageView btnClose;
    private RecyclerView rcvOrders;
    private OrderAdapter orderAdapter;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private SharedPreferences sharedPreferences;
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadUserData();
        setupImagePicker();

        // Gọi API lấy đơn hàng ngay khi vào Profile
        fetchOrders();

        // Listeners
        btnClose.setOnClickListener(v -> finish());
        btnSignOut.setOnClickListener(v -> handleSignOut());
        imgProfile.setOnClickListener(v -> checkPermissionAndOpenGallery());
    }

    private void initViews() {
        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);

        btnClose = findViewById(R.id.btn_close);
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        imgProfile = findViewById(R.id.profile_image);
        btnSignOut = findViewById(R.id.btn_sign_out);

        // Cấu hình RecyclerView
        rcvOrders = findViewById(R.id.rcv_orders);
        rcvOrders.setLayoutManager(new LinearLayoutManager(this));
        rcvOrders.setNestedScrollingEnabled(false); // Quan trọng để cuộn mượt trong NestedScrollView
    }

    private void loadUserData() {
        tvName.setText(tokenManager.getName());
        tvEmail.setText(tokenManager.getEmail());

        Glide.with(this)
                .load(tokenManager.getAvatar())
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(imgProfile);
    }

    private void fetchOrders() {
        String token = "Bearer " + tokenManager.getToken();

        apiService.getMyOrders(token).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderAdapter = new OrderAdapter(response.body().data);
                    rcvOrders.setAdapter(orderAdapter);
                } else {
                    Log.e("ORDER_ERR", "Response not successful");
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error fetching orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- LOGIC XỬ LÝ AVATAR & SIGN OUT GIỮ NGUYÊN ---
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            uploadAvatar(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void checkPermissionAndOpenGallery() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissions(new String[]{permission}, 101);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadAvatar(Uri uri) {
        try {
            String userId = tokenManager.getUserId();
            File file = new File(getCacheDir(), "temp_avatar.jpg");
            InputStream inputStream = getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            if (inputStream != null) {
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
            RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId);

            apiService.uploadAvatar(userIdPart, imagePart).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String newAvatarUrl = response.body().data.avatarURL;
                        tokenManager.saveUser(tokenManager.getToken(), tokenManager.getName(),
                                tokenManager.getEmail(), newAvatarUrl, tokenManager.getUserId(), tokenManager.getAddress());

                        Glide.with(ProfileActivity.this)
                                .load(newAvatarUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imgProfile);
                    }
                }
                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void handleSignOut() {
        tokenManager.clear();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}