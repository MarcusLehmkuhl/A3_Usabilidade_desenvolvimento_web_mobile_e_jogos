package com.estoque.dao;

import com.estoque.models.Size;
import com.estoque.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SizeDAO {
    
    public List<Size> findAll() {
        List<Size> sizes = new ArrayList<>();
        String sql = "SELECT * FROM sizes ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sizes.add(mapResultSetToSize(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sizes;
    }
    
    public Size findById(String id) {
        String sql = "SELECT * FROM sizes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSize(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Size create(Size size) {
        String sql = "INSERT INTO sizes (id, name) VALUES (?, ?)";
        String id = UUID.randomUUID().toString();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, size.getName());
            
            stmt.executeUpdate();
            size.setId(id);
            return size;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Size update(String id, Size size) {
        String sql = "UPDATE sizes SET name = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, size.getName());
            stmt.setString(2, id);
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                size.setId(id);
                return size;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean delete(String id) {
        String sql = "DELETE FROM sizes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Size mapResultSetToSize(ResultSet rs) throws SQLException {
        Size size = new Size();
        size.setId(rs.getString("id"));
        size.setName(rs.getString("name"));
        size.setCreatedAt(rs.getTimestamp("created_at"));
        return size;
    }
}
