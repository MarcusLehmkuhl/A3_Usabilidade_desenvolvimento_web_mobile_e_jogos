package com.estoque.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER;

    static {
        try {
            String dbUrl = System.getenv("DB_URL");
            String dbUsername = System.getenv("DB_USERNAME");
            String dbPassword = System.getenv("DB_PASSWORD");
            
            if (dbUrl != null && dbUsername != null && dbPassword != null) {
                URL = dbUrl;
                USERNAME = dbUsername;
                PASSWORD = dbPassword;
                DRIVER = "com.mysql.cj.jdbc.Driver";
            } else {
                Properties props = new Properties();
                InputStream input = DatabaseConnection.class.getClassLoader()
                        .getResourceAsStream("database.properties");
                
                if (input != null) {
                    props.load(input);
                    URL = props.getProperty("db.url");
                    USERNAME = props.getProperty("db.username");
                    PASSWORD = props.getProperty("db.password");
                    DRIVER = props.getProperty("db.driver");
                } else {
                    throw new IOException("Arquivo database.properties não encontrado");
                }
            }
            
            Class.forName(DRIVER);
            System.out.println("✓ Banco de dados configurado: " + URL);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao carregar configurações do banco de dados", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
