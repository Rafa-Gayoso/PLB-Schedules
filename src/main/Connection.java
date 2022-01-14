package main;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    private static java.sql.Connection connection;

    public Connection()  {

        try{
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite::resource:main/palobiofarma.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Conectado");
        }catch (Exception e){
            System.err.println("Error");
        }

    }

    public java.sql.Connection getConnection() {
        return connection;
    }
}