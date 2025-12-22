package org.jdbctd2;

import java.sql.*;

public class DBConnection {
    public Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mini_dish_db",
                    "mini_dish_db_manager",
                    "mini_dish_db"
            );
        } catch (RuntimeException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
