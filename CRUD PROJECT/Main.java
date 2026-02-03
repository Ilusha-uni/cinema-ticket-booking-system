package cinema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/cinema_db";
        String user = "cinemadb";
        String password = "b=dRn:iFr$?1G!zX.YqEF-745t+1suq&0oC$@";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Successfully connected to MySQL!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
