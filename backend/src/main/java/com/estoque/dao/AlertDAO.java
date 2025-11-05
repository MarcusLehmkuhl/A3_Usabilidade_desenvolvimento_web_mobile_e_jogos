package com.estoque.dao;

import com.estoque.models.Alert;
import com.estoque.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlertDAO {
    
    public Alert create(Alert alert) {
        String sql = "INSERT INTO alerts (id, product_id, type, severity, message, is_read) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String id = UUID.randomUUID().toString();
            stmt.setString(1, id);
            stmt.setString(2, alert.getProductId());
            stmt.setString(3, alert.getType());
            stmt.setString(4, alert.getSeverity());
            stmt.setString(5, alert.getMessage());
            stmt.setBoolean(6, alert.isRead());
            
            stmt.executeUpdate();
            alert.setId(id);
            return alert;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Alert> findAll() {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT a.*, p.name as product_name, p.stock as current_stock, p.min_stock " +
                     "FROM alerts a " +
                     "LEFT JOIN products p ON a.product_id = p.id " +
                     "ORDER BY a.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return alerts;
    }
    
    public List<Alert> findUnread() {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT a.*, p.name as product_name, p.stock as current_stock, p.min_stock " +
                     "FROM alerts a " +
                     "LEFT JOIN products p ON a.product_id = p.id " +
                     "WHERE a.is_read = FALSE " +
                     "ORDER BY a.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return alerts;
    }
    
    public List<Alert> findByProductId(String productId) {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT a.*, p.name as product_name, p.stock as current_stock, p.min_stock " +
                     "FROM alerts a " +
                     "LEFT JOIN products p ON a.product_id = p.id " +
                     "WHERE a.product_id = ? " +
                     "ORDER BY a.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return alerts;
    }
    
    public boolean markAsRead(String id) {
        String sql = "UPDATE alerts SET is_read = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean markAllAsRead() {
        String sql = "UPDATE alerts SET is_read = TRUE WHERE is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(String id) {
        String sql = "DELETE FROM alerts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int countUnread() {
        String sql = "SELECT COUNT(*) FROM alerts WHERE is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    private Alert mapResultSetToAlert(ResultSet rs) throws SQLException {
        Alert alert = new Alert();
        alert.setId(rs.getString("id"));
        alert.setProductId(rs.getString("product_id"));
        alert.setType(rs.getString("type"));
        alert.setSeverity(rs.getString("severity"));
        alert.setMessage(rs.getString("message"));
        alert.setRead(rs.getBoolean("is_read"));
        alert.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Product details
        alert.setProductName(rs.getString("product_name"));
        alert.setCurrentStock(rs.getInt("current_stock"));
        alert.setMinStock(rs.getInt("min_stock"));
        
        return alert;
    }
}
