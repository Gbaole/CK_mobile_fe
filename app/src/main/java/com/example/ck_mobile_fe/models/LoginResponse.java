package com.example.ck_mobile_fe.models;

public class LoginResponse {
    public String message;
    public boolean success;
    public Data data;

    public class Data {
        public User user;
        public String token;
    }

    public static class User {
        public String id;
        public String name;
        public String email;
        public String avatarURL;
    }
}