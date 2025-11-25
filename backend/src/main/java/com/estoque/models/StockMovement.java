package com.estoque.models;

import java.sql.Timestamp;

public class StockMovement {
    // ... (variáveis existentes)

    // Novas variáveis
    private String userId; // Identificador do usuário que realizou a movimentação
    private String supplierId; // Identificador do fornecedor (para entradas)
    private String customerId; // Identificador do cliente (para saídas)
    private Double costPrice; // Preço de custo unitário
    private Double sellingPrice; // Preço de venda unitário

    // ... (construtores existentes)

    // Construtor completo
    public StockMovement(String id, String productId, String type, Integer quantity, 
                         String userId, String supplierId, String customerId, 
                         Double costPrice, Double sellingPrice) {
        // ... (inicialização das variáveis existentes)
        this.userId = userId;
        this.supplierId = supplierId;
        this.customerId = customerId;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
    }

    // ... (getters e setters existentes)

    // Getters e Setters para as novas variáveis
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
