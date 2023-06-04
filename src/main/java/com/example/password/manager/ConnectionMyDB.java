package com.example.password.manager;

import java.sql.*;

public class ConnectionMyDB {
    private Connection connection;

    public ConnectionMyDB() {
        connection();
    }

    public Connection getConnection() {
        return connection;
    }

    private void connection() {
        try {
//            connection = DriverManager.getConnection("jdbc:mysql://localhost/password_db?serverTimezone=Europe/Moscow&useSSL=false", "root", "root");
            connection = DriverManager.getConnection("jdbc:sqlite:passwords.db");
            System.out.println("Connected to database");

            Statement statement = connection.createStatement();

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "password", null);
            if (!tables.next()) {
                String sql = "CREATE TABLE IF NOT EXISTS password " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " service VARCHAR(50) NOT NULL, " +
                        " login VARCHAR(50) NOT NULL," +
                        " password VARCHAR(100) NOT NULL)";
                statement.executeUpdate(sql);
                System.out.println("Table created successfully");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
