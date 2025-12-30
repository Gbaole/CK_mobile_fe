package com.example.ck_mobile_fe.api;

import com.example.ck_mobile_fe.models.CartResponse;
import com.example.ck_mobile_fe.models.CategoryResponse;
import com.example.ck_mobile_fe.models.LoginResponse;
import com.example.ck_mobile_fe.models.ProductDetailResponse;
import com.example.ck_mobile_fe.models.ProductResponse;
import com.example.ck_mobile_fe.models.RegisterResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/register")
    Call<RegisterResponse> register(@Body Map<String, String> userData);
    @POST("auth/login")
    Call<LoginResponse> login(@Body Map<String, String> credentials);
    @Multipart
    @PATCH("auth/update-avatar")
    Call<LoginResponse> uploadAvatar(
            @Part("userId") RequestBody userId,
            @Part MultipartBody.Part image
    );
    @GET("categories")
    Call<CategoryResponse> getCategories();

    @GET("products")
    Call<ProductResponse> getAllProducts();

    // Lấy sản phẩm theo Category ID
    @GET("categories/{id}/products")
    Call<ProductResponse> getProductsByCategory(@Path("id") String categoryId);
    @GET("products/{id}")
    Call<ProductDetailResponse> getProductDetail(@Path("id") String productId);

    @GET("cart")
    Call<CartResponse> getMyCart(@Header("Authorization") String token);

    @POST("cart")
    Call<CartResponse> addToCart(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );

    @DELETE("cart/{productId}")
    Call<CartResponse> removeFromCart(
            @Header("Authorization") String token,
            @Path("productId") String productId
    );
}