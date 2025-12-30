package com.example.ck_mobile_fe.api;

import com.example.ck_mobile_fe.models.LoginResponse;
import com.example.ck_mobile_fe.models.RegisterResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("auth/register")
    Call<RegisterResponse> register(@Body Map<String, String> userData);
    @POST("auth/login")
    Call<LoginResponse> login(@Body Map<String, String> credentials);
}