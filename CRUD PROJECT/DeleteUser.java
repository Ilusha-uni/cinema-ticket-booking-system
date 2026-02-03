package cinema;

// Delete Example
// Delete a User from the users table
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteUser {
    public static void main(String[] args) {
        // database URL
        final String DATABASE_URL = "jdbc:mysql://localhost:3306/cinema_db";

        long userId = 20; // user ID to delete

        Connection connection = null;
        PreparedStatement pstat = null;
        int i = 0;

        try {
            // establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL, "cinemadb", "b=dRn:iFr$?1G!zX.YqEF-745t+1suq&0oC$@");

            // create Prepared Statement for deleting data from the table
            pstat = connection.prepareStatement("DELETE FROM users WHERE id=?");
            pstat.setLong(1, userId);

            // delete data from the table
            i = pstat.executeUpdate();
            System.out.println(i + " record successfully removed from the users table.");

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

