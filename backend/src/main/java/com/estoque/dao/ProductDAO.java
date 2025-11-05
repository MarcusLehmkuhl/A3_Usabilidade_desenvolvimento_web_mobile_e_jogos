package com.estoque.dao;

import com.estoque.models.Product;
import com.estoque.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDAO {
    
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public Product findById(String id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Product create(Product product) {
        String sql = "INSERT INTO products (id, name, description, price, stock, min_stock, category_id, size_id, packaging_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String id = UUID.randomUUID().toString();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setInt(5, product.getStock() != null ? product.getStock() : 0);
            stmt.setInt(6, product.getMinStock() != null ? product.getMinStock() : 0);
            stmt.setString(7, product.getCategoryId());
            stmt.setString(8, product.getSizeId());
            stmt.setString(9, product.getPackagingId());
            
            stmt.executeUpdate();
            product.setId(id);
            return product;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Product update(String id, Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock = ?, min_stock = ?, category_id = ?, size_id = ?, packaging_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStock() != null ? product.getStock() : 0);
            stmt.setInt(5, product.getMinStock() != null ? product.getMinStock() : 0);
            stmt.setString(6, product.getCategoryId());
            stmt.setString(7, product.getSizeId());
            stmt.setString(8, product.getPackagingId());
            stmt.setString(9, id);
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                product.setId(id);
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean delete(String id) {
        String sql = "DELETE FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateStock(String id, int quantity) {
        String sql = "UPDATE products SET stock = stock + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setString(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Product> findLowStock() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock <= min_stock ORDER BY stock ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public int countLowStock() {
        String sql = "SELECT COUNT(*) FROM products WHERE stock <= min_stock";
        
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
    
    public int count() {
        String sql = "SELECT COUNT(*) FROM products";
        
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
    
    public double getTotalValue() {
        String sql = "SELECT SUM(price * stock) as total_value FROM products";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getString("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStock(rs.getInt("stock"));
        product.setMinStock(rs.getInt("min_stock"));
        product.setCategoryId(rs.getString("category_id"));
        product.setSizeId(rs.getString("size_id"));
        product.setPackagingId(rs.getString("packaging_id"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setUpdatedAt(rs.getTimestamp("updated_at"));
        return product;
    }
}
