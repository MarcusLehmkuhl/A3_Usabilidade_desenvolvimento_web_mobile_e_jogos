-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS estoque_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE estoque_db;

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de categorias
CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    size_id VARCHAR(36),
    packaging_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de tamanhos
CREATE TABLE IF NOT EXISTS sizes (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de embalagens
CREATE TABLE IF NOT EXISTS packagings (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de produtos
CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    min_stock INT DEFAULT 0,
    category_id VARCHAR(36),
    size_id VARCHAR(36),
    packaging_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (size_id) REFERENCES sizes(id) ON DELETE SET NULL,
    FOREIGN KEY (packaging_id) REFERENCES packagings(id) ON DELETE SET NULL
);

-- Tabela de movimentações de estoque
CREATE TABLE IF NOT EXISTS stock_movements (
    id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    type ENUM('entrada', 'saida') NOT NULL,
    quantity INT NOT NULL,
    reason VARCHAR(255),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Tabela de logs de auditoria
CREATE TABLE IF NOT EXISTS audit_logs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Tabela de alertas
CREATE TABLE IF NOT EXISTS alerts (
    id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_size ON products(size_id);
CREATE INDEX idx_products_packaging ON products(packaging_id);
CREATE INDEX idx_stock_movements_product ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_date ON stock_movements(date);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_date ON audit_logs(created_at);
CREATE INDEX idx_alerts_product ON alerts(product_id);
CREATE INDEX idx_alerts_read ON alerts(is_read);

-- Dados de exemplo (opcional)
-- Senha: senha123 (hash BCrypt)
INSERT INTO users (id, email, password, name) VALUES 
('1', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin');

INSERT INTO sizes (id, name) VALUES
('1', 'Pequeno'),
('2', 'Médio'),
('3', 'Grande');

INSERT INTO packagings (id, name) VALUES
('1', 'Caixa'),
('2', 'Saco'),
('3', 'Unidade');

INSERT INTO categories (id, name, description, size_id, packaging_id) VALUES
('1', 'Eletrônicos', 'Produtos eletrônicos', '2', '1'),
('2', 'Alimentos', 'Produtos alimentícios', '2', '2'),
('3', 'Bebidas', 'Bebidas em geral', '3', '3');

INSERT INTO products (id, name, description, price, stock, min_stock, category_id, size_id, packaging_id) VALUES
('1', 'Notebook Dell', 'Notebook Dell Inspiron 15', 3500.00, 10, 3, '1', '2', '1'),
('2', 'Arroz 5kg', 'Arroz branco tipo 1', 25.90, 50, 10, '2', '3', '2'),
('3', 'Coca-Cola 2L', 'Refrigerante Coca-Cola 2 litros', 8.50, 100, 20, '3', '3', '3');
