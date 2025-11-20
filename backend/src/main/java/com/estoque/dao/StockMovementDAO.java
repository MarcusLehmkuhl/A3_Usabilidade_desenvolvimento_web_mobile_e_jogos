package com.estoque.dao;

import com.estoque.models.StockMovement;
import com.estoque.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StockMovementDAO {
    
    public List<StockMovement> findAll() {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT sm.*, p.name AS product_name FROM stock_movements sm " +
                     "LEFT JOIN products p ON sm.product_id = p.id " +
                     "ORDER BY sm.date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                movements.add(mapResultSetToStockMovement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movements;
    }
    
    public StockMovement findById(String id) {
        String sql = "SELECT * FROM stock_movements WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStockMovement(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<StockMovement> findByProductId(String productId) {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM stock_movements WHERE product_id = ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movements.add(mapResultSetToStockMovement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movements;
    }
    
    public StockMovement create(StockMovement movement) {
        String sql = "INSERT INTO stock_movements (id, product_id, type, quantity, reason, date) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        String id = UUID.randomUUID().toString();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, movement.getProductId());
            stmt.setString(3, movement.getType());
            stmt.setInt(4, movement.getQuantity());
            stmt.setString(5, movement.getReason());
            
            stmt.executeUpdate();
            movement.setId(id);
            return movement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean delete(String id) {
        String sql = "DELETE FROM stock_movements WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<StockMovement> findByDateRange(Timestamp startDate, Timestamp endDate) {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM stock_movements WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movements.add(mapResultSetToStockMovement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movements;
    }
    
    public List<StockMovement> findByType(String type) {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM stock_movements WHERE type = ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movements.add(mapResultSetToStockMovement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movements;
    }
    
    public int countByType(String type) {
        String sql = "SELECT COUNT(*) FROM stock_movements WHERE type = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalQuantityByType(String type) {
        String sql = "SELECT SUM(quantity) FROM stock_movements WHERE type = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private StockMovement mapResultSetToStockMovement(ResultSet rs) throws SQLException {
        StockMovement movement = new StockMovement();
        movement.setId(rs.getString("id"));
        movement.setProductId(rs.getString("product_id"));
        movement.setType(rs.getString("type"));
        movement.setQuantity(rs.getInt("quantity"));
        movement.setReason(rs.getString("reason"));
        movement.setDate(rs.getTimestamp("date"));
        
        try {
            movement.setProductName(rs.getString("product_name"));
        } catch (SQLException e) {
            // Coluna product_name pode não existir em todas as queries
        }
        
        return movement;
    }
}
