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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ck_mobile_fe.api.ApiService;
import com.example.ck_mobile_fe.api.RetrofitClient;
import com.example.ck_mobile_fe.models.LoginResponse;
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
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private SharedPreferences sharedPreferences;
    private TokenManager tokenManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadUserData();
        setupImagePicker();

        // Nút quay lại
        btnClose.setOnClickListener(v -> finish());

        // Nút đăng xuất
        btnSignOut.setOnClickListener(v -> handleSignOut());

        // Click vào ảnh để chọn ảnh mới
        imgProfile.setOnClickListener(v -> checkPermissionAndOpenGallery());
    }

    private void initViews() {
        tokenManager = new TokenManager(this);
        btnClose = findViewById(R.id.btn_close);
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        imgProfile = findViewById(R.id.profile_image);
        btnSignOut = findViewById(R.id.btn_sign_out);
        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
    }

    private void loadUserData() {
        String name = sharedPreferences.getString("name", "Gamer");
        String email = sharedPreferences.getString("email", "no-email@obsidian.game");
        String avatarUrl = sharedPreferences.getString("avatar", "");

        tvName.setText(tokenManager.getName());
        tvEmail.setText(tokenManager.getEmail());

        Glide.with(this)
                .load(tokenManager.getAvatar())
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(imgProfile);
    }

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
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

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
            // 1. Lấy UserId từ SharedPreferences
            String userId = tokenManager.getUserId();
            // 2. TẠO FILE TẠM VÀ COPY DỮ LIỆU
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

            // 3. Tạo RequestBody cho Multipart
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
            RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId);

            // 4. Gọi API
            ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

            apiService.uploadAvatar(userIdPart, imagePart).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String newAvatarUrl = response.body().data.avatarURL;

                        // Cập nhật lại TokenManager (Ảnh mới)
                        tokenManager.saveUser(
                                tokenManager.getToken(),
                                tokenManager.getName(),
                                tokenManager.getEmail(),
                                newAvatarUrl,
                                tokenManager.getUserId()
                        );

                        // Cập nhật UI
                        Glide.with(ProfileActivity.this)
                                .load(newAvatarUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imgProfile);
                    }
                }
                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    // Xử lý khi không có mạng, timeout hoặc server không phản hồi
                    Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("UPLOAD_ERR", t.getMessage());
                }
            });
        } catch (IOException e) { e.printStackTrace(); }
    }
    private void handleSignOut() {
        tokenManager.clear();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}