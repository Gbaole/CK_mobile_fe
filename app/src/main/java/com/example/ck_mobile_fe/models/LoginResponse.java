package com.example.ck_mobile_fe.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    public String message;
    public boolean success;
    public Data data;

    public static class Data {
        public User user;
        public String token;

        @SerializedName("id")
        public String id;
        public String name;
        public String avatarURL;
        public String phoneNumber;
        public String shippingAddress;
        public String email;
    }

    public static class User {
        @SerializedName("id")
        public String id;
        public String name;
        public String email;
        public String avatarURL;
        public String shippingAddress;
        public String phoneNumber;
    }
}