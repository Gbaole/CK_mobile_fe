package com.example.ck_mobile_fe.models;

public class Product {
    private String name;
    private String brand;
    private double price;
    private double rating;
    private int imageRes; // Dùng int nếu bạn để ảnh trong thư mục drawable

    public Product(String name, String brand, double price, double rating, int imageRes) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.rating = rating;
        this.imageRes = imageRes;
    }

    // Các hàm Getter...
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public int getImageRes() { return imageRes; }
}