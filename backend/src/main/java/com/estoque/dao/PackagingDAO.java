package com.estoque.dao;

import com.estoque.models.Packaging;
import com.estoque.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PackagingDAO {
    
    public List<Packaging> findAll() {
        List<Packaging> packagings = new ArrayList<>();
        String sql = "SELECT * FROM packagings ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                packagings.add(mapResultSetToPackaging(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return packagings;
    }
    
    public Packaging findById(String id) {
        String sql = "SELECT * FROM packagings WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPackaging(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Packaging create(Packaging packaging) {
        String sql = "INSERT INTO packagings (id, name) VALUES (?, ?)";
        String id = UUID.randomUUID().toString();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, packaging.getName());
            
            stmt.executeUpdate();
            packaging.setId(id);
            return packaging;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Packaging update(String id, Packaging packaging) {
        String sql = "UPDATE packagings SET name = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, packaging.getName());
            stmt.setString(2, id);
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                packaging.setId(id);
                return packaging;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean delete(String id) {
        String sql = "DELETE FROM packagings WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Packaging mapResultSetToPackaging(ResultSet rs) throws SQLException {
        Packaging packaging = new Packaging();
        packaging.setId(rs.getString("id"));
        packaging.setName(rs.getString("name"));
        packaging.setCreatedAt(rs.getTimestamp("created_at"));
        return packaging;
    }
}
