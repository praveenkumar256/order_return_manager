package Order_return_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/order_return_db";
    private static final String USER = "root";
    private static final String PASSWORD = "praveen@123";

    public static Connection getConnection1() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
