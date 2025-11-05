package com.estoque.models;

import java.sql.Timestamp;

public class StockMovement {
    private String id;
    private String productId;
    private String type; // "entrada" ou "saida"
    private Integer quantity;
    private String reason;
    private Timestamp date;

    public StockMovement() {}

    public StockMovement(String id, String productId, String type, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
