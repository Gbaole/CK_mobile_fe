package com.example.ck_mobile_fe.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    public String message;
    public boolean success;
    public Data data;

    public static class Data {
        // Dành cho Login (Cấu trúc có lồng 'user')
        public User user;
        public String token;

        // Dành cho Update Avatar (Cấu trúc phẳng - Flat)
        @SerializedName("_id")
        public String id;
        public String name;
        public String avatarURL;

    }

    public static class User {
        @SerializedName("_id")
        public String id;
        public String name;
        public String email;
        public String avatarURL;
        public String shippingAddress;
    }
}