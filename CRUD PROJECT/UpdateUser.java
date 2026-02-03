package cinema;

// Update Example
// Update a User in the users table
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateUser {
    public static void main(String[] args) {
        // database URL
        final String DATABASE_URL = "jdbc:mysql://localhost:3306/cinema_db";

        String newName = "Jane Doe";
        String newEmail = "jane@example.com";
        String newPassword = "newpassword123";
        String newRole = "ADMIN";
        long userId = 1;

        Connection connection = null;
        PreparedStatement pstat = null;
        int i = 0;

        try {
            // establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL, "cinemadb", "b=dRn:iFr$?1G!zX.YqEF-745t+1suq&0oC$@");

            // create Prepared Statement for updating data in the table
            pstat = connection.prepareStatement(
                    "UPDATE users SET name=?, email=?, password=?, role=? WHERE id=?"
            );
            pstat.setString(1, newName);
            pstat.setString(2, newEmail);
            pstat.setString(3, newPassword);
            pstat.setString(4, newRole);
            pstat.setLong(5, userId);

            // update data in the table
            i = pstat.executeUpdate();
            System.out.println(i + " record successfully updated in the users table.");

        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                pstat.close();
                connection.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    } // end main
} // end class
