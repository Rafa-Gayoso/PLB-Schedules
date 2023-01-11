package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static Connection connection;

    public DatabaseConnection()  {

        try{
            String url = "jdbc:mysql://palobiofarmadatabase.cmh8cyovqwqt.eu-west-3.rds.amazonaws.com/palodb";

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