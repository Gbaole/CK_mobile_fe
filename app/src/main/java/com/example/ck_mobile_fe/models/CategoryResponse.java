package com.example.ck_mobile_fe.models;

import java.util.List;

public class CategoryResponse {
    public boolean success;
    public String message;
    public List<Category> data; // Biến này phải là public

    // Class này phải là public static
    public static class Category {
        public String id;
        public String name;
        public String createdAt;
    }
}