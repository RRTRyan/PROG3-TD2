package org.jdbctd2;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class DBConnection {
    Dotenv dotenv = Dotenv.load();

    public Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(
                    dotenv.get("POSTGRES_URL"),
                    dotenv.get("POSTGRES_USER"),
                    dotenv.get("POSTGRES_PASSWORD")
            );
        } catch (RuntimeException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
