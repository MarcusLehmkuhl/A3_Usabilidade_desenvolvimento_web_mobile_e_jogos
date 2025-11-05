package com.estoque.services;

import com.estoque.dao.AlertDAO;
import com.estoque.models.Alert;
import com.estoque.models.Product;

public class AlertService {
    private AlertDAO alertDAO = new AlertDAO();
    
    public void checkAndCreateLowStockAlert(Product product) {
        if (product.getStock() < product.getMinStock()) {
            // Calculate deficit and severity
            int deficit = product.getMinStock() - product.getStock();
            double percentageBelow = ((double) deficit / product.getMinStock()) * 100;
            
            String severity;
            if (percentageBelow >= 75) {
                severity = "CRITICAL";
            } else if (percentageBelow >= 50) {
                severity = "HIGH";
            } else if (percentageBelow >= 25) {
                severity = "MEDIUM";
            } else {
                severity = "LOW";
            }
            
            String message = String.format(
                "Produto '%s' está com estoque abaixo do mínimo. " +
                "Estoque atual: %d, Estoque mínimo: %d, Deficit: %d unidades (%.1f%% abaixo)",
                product.getName(),
                product.getStock(),
                product.getMinStock(),
                deficit,
                percentageBelow
            );
            
            Alert alert = new Alert(product.getId(), "LOW_STOCK", severity, message);
            alertDAO.create(alert);
            
            System.out.println("⚠️ ALERTA CRIADO: " + message);
        }
    }
    
    public void checkAndCreateOutOfStockAlert(Product product) {
        if (product.getStock() <= 0) {
            String message = String.format(
                "Produto '%s' está SEM ESTOQUE! Reposição urgente necessária.",
                product.getName()
            );
            
            Alert alert = new Alert(product.getId(), "OUT_OF_STOCK", "CRITICAL", message);
            alertDAO.create(alert);
            
            System.out.println("🚨 ALERTA CRÍTICO: " + message);
        }
    }
}
