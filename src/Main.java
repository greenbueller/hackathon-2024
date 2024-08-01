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

        Scanner getFunction = new Scanner(System.in);
        System.out.println("Welcome to X.");
        System.out.println("What do you want to do?");
        System.out.println("1. Get the current list of Committees");
        System.out.println("2. Get the current list of Members");
        System.out.println("3. Add a new member");
        System.out.println("4. Send out an email");
        System.out.println("5. Exit");

        System.out.println("Enter your choice: ");
        int choice = getFunction.nextInt();

        try {
            switch (choice) {
                case 1:
                    getCommittees(connection);
                    break;
                case 2:
                    getMembers(connection);
                    break;
                case 3:
                    addMember(connection);
                    break;
                case 4:
                    sendEmail(connection);
                    break;
                case 5:
                    System.out.println("Have a good day.");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
        catch (Exception e) {
            System.out.println("An error occurred. See the following error: " + e);
        }
    }

    public static void getCommittees(Connection connection) {

    }

    public static void getMembers (Connection connection) {
        /*try {
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
        }*/
    }

    public static void addMember (Connection connection) {

    }

    public static void sendEmail (Connection connection) {

    }
}