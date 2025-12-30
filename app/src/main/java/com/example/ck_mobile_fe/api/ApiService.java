package com.example.ck_mobile_fe.api;

import com.example.ck_mobile_fe.models.LoginResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("auth/register")
    Call<Object> register(
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part avatar
    );
    @POST("auth/login")
    Call<LoginResponse> login(@Body Map<String, String> credentials);
}