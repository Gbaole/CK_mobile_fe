package com.example.ck_mobile_fe.models;
import java.util.List;

public class ProductResponse {
    public boolean success;
    public String message;
    public List<Product> data;

    public static class Product {
        public String id;
        public String name;
        public String type;
        public int sold;
        public int price;
        public List<String> images;
        public String description;
        public Brand brand;
        public CategorySummary category;
        public Specs specs;


    }
    public static class Specs {
        public String storage;
        public String color;
        public String region;
    }
    public static class Brand {
        public String id;
        public String name;
    }

    public static class CategorySummary {
        public String id;
        public String name;
    }
}