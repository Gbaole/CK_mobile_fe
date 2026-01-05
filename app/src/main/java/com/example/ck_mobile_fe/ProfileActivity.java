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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvAddress, btnSignOut;
    private CircleImageView imgProfile;
    private ImageView btnClose;
    private RecyclerView rcvOrders;
    private OrderAdapter orderAdapter;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private SharedPreferences sharedPreferences;
    private TokenManager tokenManager;
    private LinearLayout layoutAddressClick;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initViews();

        findViewById(R.id.info_container).setOnClickListener(v -> showEditProfileDialog());
        layoutAddressClick.setOnClickListener(v -> showEditProfileDialog());        loadUserData();
        setupImagePicker();
        fetchOrders();

        // Listeners
        btnClose.setOnClickListener(v -> finish());
        btnSignOut.setOnClickListener(v -> handleSignOut());
        imgProfile.setOnClickListener(v -> checkPermissionAndOpenGallery());
    }

    private void initViews() {
        layoutAddressClick = findViewById(R.id.layout_address_click);
        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);

        btnClose = findViewById(R.id.btn_close);
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvPhone = findViewById(R.id.tv_profile_phone);
        tvAddress = findViewById(R.id.tv_profile_address);
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
        tvPhone.setText(tokenManager.getPhone());
        String address = tokenManager.getAddress();
        if (address != null && !address.isEmpty()) {
            tvAddress.setText(address);
        } else {
            tvAddress.setText("Chưa cập nhật địa chỉ");
        }
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
                                tokenManager.getEmail(), newAvatarUrl, tokenManager.getUserId(), tokenManager.getAddress(), tokenManager.getPhone());

                        Glide.with(ProfileActivity.this)
                                .load(newAvatarUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imgProfile);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
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
    private void showEditProfileDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.edt_edit_name);
        EditText edtEmail = dialogView.findViewById(R.id.edt_edit_email);
        EditText edtPhone = dialogView.findViewById(R.id.edt_edit_phone);
        EditText edtAddress = dialogView.findViewById(R.id.edt_edit_address);
        Button btnSave = dialogView.findViewById(R.id.btn_save_profile);

        // load dữ liệu từ TokenManager
        edtName.setText(tokenManager.getName());
        edtEmail.setText(tokenManager.getEmail());
        edtPhone.setText(tokenManager.getPhone());
        edtAddress.setText(tokenManager.getAddress());

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            Map<String, String> body = new HashMap<>();
            body.put("name", name);
            body.put("email", email);
            body.put("phoneNumber", phone);
            body.put("shippingAddress", address);

            String token = "Bearer " + tokenManager.getToken();
            apiService.updateProfile(token, body).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse.Data updatedData = response.body().data;

                        // Lưu lại vào SharedPreferences
                        tokenManager.saveUser(
                                tokenManager.getToken(),
                                updatedData.name,
                                updatedData.email,
                                updatedData.avatarURL,
                                updatedData.id,
                                updatedData.shippingAddress,
                                updatedData.phoneNumber
                        );

                        loadUserData(); // Cập nhật lại UI Profile
                        dialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorMsg = response.errorBody().string();
                            Log.e("UPDATE_PROFILE_ERROR", "Server: " + errorMsg);
                            Toast.makeText(ProfileActivity.this, "Lỗi 400: " + errorMsg, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}