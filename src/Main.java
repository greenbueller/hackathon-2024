import java.sql.*;
import java.util.Scanner;

// Nick Smith
public class Main {
    public static void main(String[] args) {
        String USERID, PASSWORD, CONNECTIONSTRING;

        // Establish the Oracle Driver
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to find Oracle Driver. See the following error: " + e);
            return;
        }

        // Validate connection arguments
        try {
            USERID = args[0];
            PASSWORD = args[1];
            CONNECTIONSTRING = args[2];
        } catch (Exception e) {
            System.out.println("Error: Command Line Arguments must be included. See the following: " + e);
            return;
        }

        Connection connection;

        // Attempt to make connection
        try {
            connection = DriverManager.getConnection(CONNECTIONSTRING, USERID, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection failed. See the following: " + e);
            return;
        }

        System.out.println("Connected.");

    }

    public static void locations (Connection connection) {
        Scanner locationIDScanner = new Scanner(System.in);
        System.out.println("Enter Location ID: ");

        String locationID = locationIDScanner.nextLine();
        try {
            Statement stmt = connection.createStatement();
            String sqlQuery = "SELECT * FROM Locations WHERE locationID = '" + locationID + "'";
            ResultSet rs = stmt.executeQuery(sqlQuery);

            String lID, lName, lType, lFloor;
            int lXCoord, lYCoord;

            while (rs.next()) {
                lID = rs.getString("locationID");
                lName = rs.getString("locationName");
                lType = rs.getString("locationType");
                lXCoord = rs.getInt("xcoord");
                lYCoord = rs.getInt("ycoord");
                lFloor = rs.getString("mapFloor");
                System.out.println("Location Information " + System.lineSeparator() +
                        System.lineSeparator() + "Location ID: " + lID +
                        System.lineSeparator() + "Location Name: " + lName +
                        System.lineSeparator() + "Location Type: " + lType +
                        System.lineSeparator() + "X-Coordinate: " + lXCoord +
                        System.lineSeparator() + "Y-Coordinate: " + lYCoord +
                        System.lineSeparator() + "Floor: " + lFloor);
            }

            rs.close();
            stmt.close();
            locationIDScanner.close();
        }
        catch (SQLException e) {
            System.out.println("An error occurred when selecting the location input. See the following: " + e);
        }
    }
}