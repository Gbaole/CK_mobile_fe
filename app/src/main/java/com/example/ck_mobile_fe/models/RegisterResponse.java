package com.example.ck_mobile_fe.models;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    public boolean success;
    public String message;
    public UserData data;

    public static class UserData {
        @SerializedName("_id")
        public String id;

        public String name;
        public String email;

        @SerializedName("avatarURL")
        public String avatarURL;

        public String phoneNumber;
        public String status;
        public String role;

        public String createdAt;
        public String updatedAt;

        @SerializedName("__v")
        public int version;
    }
}