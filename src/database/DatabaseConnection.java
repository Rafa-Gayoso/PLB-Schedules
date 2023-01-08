package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static Connection connection;

    public DatabaseConnection()  {

        try{
            String url = "jdbc:mysql://containers-us-west-182.railway.app:6865/railway";

            String userName = "root";
            String password = "xkkgTvHf1TGgmzheZq1S";

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