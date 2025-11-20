package com.estoque.dao;

import com.estoque.models.Category;
import com.estoque.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryDAO {
    
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.*, s.name AS size_name, p.name AS packaging_name " +
                     "FROM categories c " +
                     "LEFT JOIN sizes s ON c.size_id = s.id " +
                     "LEFT JOIN packagings p ON c.packaging_id = p.id " +
                     "ORDER BY c.name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    public Category findById(String id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Category create(Category category) {
        String sql = "INSERT INTO categories (id, name, description, size_id, packaging_id) VALUES (?, ?, ?, ?, ?)";
        String id = UUID.randomUUID().toString();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, category.getName());
            stmt.setString(3, category.getDescription());
            stmt.setString(4, category.getSizeId());
            stmt.setString(5, category.getPackagingId());
            
            stmt.executeUpdate();
            category.setId(id);
            return category;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Category update(String id, Category category) {
        String sql = "UPDATE categories SET name = ?, description = ?, size_id = ?, packaging_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setString(3, category.getSizeId());
            stmt.setString(4, category.getPackagingId());
            stmt.setString(5, id);
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                category.setId(id);
                return category;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean delete(String id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getString("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setSizeId(rs.getString("size_id"));
        category.setPackagingId(rs.getString("packaging_id"));
        category.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Adicionar nomes de tamanho e embalagem se existirem no ResultSet
        try {
            category.setSizeName(rs.getString("size_name"));
        } catch (SQLException e) {
            // Coluna não existe no ResultSet
        }
        
        try {
            category.setPackagingName(rs.getString("packaging_name"));
        } catch (SQLException e) {
            // Coluna não existe no ResultSet
        }
        
        return category;
    }
}
