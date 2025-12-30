package com.example.ck_mobile_fe.api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/api/v1/";
    private static Retrofit retrofit = null;

    // Thêm tham số Context để truy cập SharedPreferences
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // 1. Logging Interceptor (Của bạn đang có)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Auth Interceptor (Thêm mới)
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging) // Giữ lại log để debug
                    .addInterceptor(chain -> {
                        // Lấy token từ SharedPreferences (Khớp với file ProfileActivity bạn gửi)
                        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPref", Context.MODE_PRIVATE);
                        String token = sharedPreferences.getString("token", "");

                        Request.Builder builder = chain.request().newBuilder();

                        // Nếu token tồn tại thì tự động đính kèm vào mọi Request
                        if (token != null && !token.isEmpty()) {
                            builder.addHeader("Authorization", "Bearer " + token);
                        }

                        return chain.proceed(builder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}