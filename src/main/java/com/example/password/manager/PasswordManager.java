package com.example.password.manager;

import javafx.collections.ObservableMap;

import java.sql.*;

public class PasswordManager {
    private final Connection conn;

    int idTables;

    public PasswordManager() {
        ConnectionMyDB db = new ConnectionMyDB();
        this.conn = db.getConnection();
        setIdTables();
    }

    void setIdTables() {
        String sql = "SELECT seq FROM sqlite_sequence WHERE name='table_name';";
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idTables = rs.getInt(1); // получение последнего добавленного ID
                System.out.println("Last inserted ID: " + idTables);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPassword(String service, String login, String password) {
        String sql = "INSERT INTO password (service, login, password) Values (?, ?, ?)";

        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, service);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, password);

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void changePassword(int id, String service, String login, String password) {
        String query = String.format("UPDATE password SET service='%s', login='%s', password='%s' WHERE id=%d",
                service, login, password, id);
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePassword(int id) {
        String sql = "DELETE FROM password WHERE id = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableMap<Integer, Password> setData(ObservableMap<Integer, Password> data) {
        String sql = "SELECT id, service, login, password FROM password";

        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String service = resultSet.getString(2);
                String login = resultSet.getString(3);
                String password = resultSet.getString(4);

                Password passwordObj = new Password(id, service, login, password);
                data.put(id, passwordObj);
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public void clearDatabase() {
        String sql = "DELETE FROM password";
        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteDublicate() {
        String sql = "Delete FROM password WHERE rowid NOT IN (SELECT MIN(rowid) FROM password GROUP BY service, login, password)";
        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEmpty() {
        ResultSet result;
        try {
            result = conn.createStatement().executeQuery("SELECT COUNT(*) FROM password");
            if (result.getInt(1) == 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
