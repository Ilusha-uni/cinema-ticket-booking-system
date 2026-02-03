package cinema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertUser {
    public static void main(String[] args) {
        // database URL
        final String DATABASE_URL = "jdbc:mysql://localhost:3306/cinema_db";

        Connection connection = null;

        PreparedStatement pstat = null;

        // New user's data
        String name = "userNAME";
        String email = "user24gs@gmail.com";
        String password = "user24gsPass1";
        String role = "USER";

        int i = 0;

        try {
            // establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL, "cinemadb", "b=dRn:iFr$?1G!zX.YqEF-745t+1suq&0oC$@");

            // create Prepared Statement for inserting data into table
            String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            pstat = connection.prepareStatement(sql);

            pstat.setString(1, name);
            pstat.setString(2, email);
            pstat.setString(3, password);
            pstat.setString(4, role);

            // insert data into table
            i = pstat.executeUpdate();
            System.out.println(i + " record successfully added to the table");

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                if (pstat != null) pstat.close();
                if (connection != null) connection.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}

