package com.example.ck_mobile_fe.models;

import java.util.List;

public class CartResponse {
    public boolean success;
    public String message;
    public CartData data;

    public static class CartData {
        public String _id;
        public String userId;
        public List<CartItem> items;
        public double totalPrice;
    }

    public static class CartItem {
        public String productId;
        public String name;
        public int quantity;
        public double price;
        public String _id; // id của dòng item trong giỏ
    }
}