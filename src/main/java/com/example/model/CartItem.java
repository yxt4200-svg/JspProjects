package com.example.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;
    private String name;
    private double price;
    private int quantity;

    public CartItem() {}

    public CartItem(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 计算单项总价
    public double getTotalPrice() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "CartItem{id=" + id + ", name='" + name + "', price=" + price + ", quantity=" + quantity + '}';
    }
}