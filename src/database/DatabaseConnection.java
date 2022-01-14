package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static java.sql.Connection connection;

    public DatabaseConnection()  {

        try{
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite::resource:database/palobiofarma.db";
            connection = DriverManager.getConnection(url);
        }catch (Exception e){
            System.err.println("Error");
        }

    }

    public Connection getConnection() {
        return connection;
    }
}