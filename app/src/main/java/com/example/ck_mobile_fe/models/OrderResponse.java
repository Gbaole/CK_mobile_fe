package com.example.ck_mobile_fe.models;

import java.util.List;

public class OrderResponse {
    public boolean success;
    public List<OrderDTO> data;

    public static class OrderDTO {
        public String id;
        public String status;
        public String shippingAddress;
        public double totalPrice;
        public String createdAt;
        public List<OrderItemDTO> items;
    }

    public static class OrderItemDTO {
        public String id;
        public String productId;
        public String name;
        public int quantity;
        public double price;
        public String image;
    }
}
