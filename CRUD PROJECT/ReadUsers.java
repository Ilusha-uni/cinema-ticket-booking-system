package cinema;

// Querying Database Example
// Displaying the contents of the users table
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ReadUsers {
    public static void main(String[] args) {
        // database URL
        final String DATABASE_URL = "jdbc:mysql://localhost:3306/cinema_db";

        Connection connection = null;
        PreparedStatement pstat = null;
        ResultSet resultSet = null;
        int i = 0;

        try {
            // establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL, "cinemadb", "b=dRn:iFr$?1G!zX.YqEF-745t+1suq&0oC$@");

            // create Prepared Statement for querying data in the table
            pstat = connection.prepareStatement(
                    "SELECT id, name, email, password, role, created_at FROM users"
            );

            // query data in the table
            resultSet = pstat.executeQuery();

            // process query results
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            System.out.println("-----------   USERS OF cinema_db  ----------:\n");

            // Print column headers
            for(i = 1; i <= numberOfColumns; i++)
                System.out.printf("%-28s",  metaData.getColumnName(i));
            System.out.println();

            // Print rows
            while(resultSet.next()) {
                for(i = 1; i <= numberOfColumns; i++)
                    System.out.printf("%-28s", resultSet.getObject(i));
                System.out.println();
            }

        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                resultSet.close();
                pstat.close();
                connection.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    } // end main
} // end class
