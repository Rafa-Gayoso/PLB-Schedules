package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static java.sql.Connection connection;

    public DatabaseConnection()  {

        try{
            String url = "jdbc:mysql://palo-db.crra9zetpa9g.eu-west-3.rds.amazonaws.com:3306/palo-db";

            String userName = "RafaGayoso";
            String password = "rafa1997.";

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, userName,
                    password);
        }catch (Exception e){
            System.err.println("Error");
        }

    }

    public Connection getConnection() {
        return connection;
    }
}