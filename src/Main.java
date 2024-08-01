import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

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
        System.out.println("Welcome to The Activation Station.");
        System.out.println();
        System.out.println("What do you want to do?");
        System.out.println("1. Get the current list of Committees");
        System.out.println("2. Get the current list of Members");
        System.out.println("3. Add a new member");
        System.out.println("4. Send out an email");
        System.out.println("5. Add member to committee");
        System.out.println("6. Exit");

        System.out.println("Enter your choice: " + System.lineSeparator());
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
                    addCommittee(connection);
                    break;
                case 6:
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

    /*
        Database Structure:

        Committee[committeeName (varchar 50), chair (varchar 50), advisor (varchar 50), emailAlias (varchar 30)]
        Members[firstName (varchar 20), lastName (varchar 40), email (varchar 100), guardianEmail (varchar 100),
            membershipLevel (varchar 20), committeeMembership (varchar 50 -> Linked via committeeName)]
     */

    public static void getCommittees(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            String sqlQuery = "SELECT * FROM Committee";
            ResultSet rs = stmt.executeQuery(sqlQuery);

            System.out.println("Committees: " + System.lineSeparator());

            String cName, chair, advisor, alias;
            while (rs.next()) {
                cName = rs.getString("committeeName");
                chair = rs.getString("chair");
                advisor = rs.getString("committeeAdvisor");
                alias = rs.getString("emailAlias");;
                if (chair != null) {
                    if (advisor != null) {
                        System.out.println(
                                System.lineSeparator() + "Name: " + cName +
                                        System.lineSeparator() + "Chair: " + chair +
                                        System.lineSeparator() + "Advisor: " + advisor +
                                        System.lineSeparator() + "Email Alias: " + alias);
                    }
                    else {
                        System.out.println(
                                System.lineSeparator() + "Name: " + cName +
                                        System.lineSeparator() + "Chair: " + chair +
                                        System.lineSeparator() + "Email Alias: " + alias);
                    }
                }
                else {
                    System.out.println(System.lineSeparator() + "Name: " + cName + System.lineSeparator() + "Email Alias: " + alias);
                }
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("An error occurred. See the following error: " + e);
        }
    }

    public static void getMembers (Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            String sqlQuery = "SELECT * FROM Members";
            ResultSet rs = stmt.executeQuery(sqlQuery);

            System.out.println("Members: " + System.lineSeparator());

            String fName, lName, memEmail, oldEmail, memLevel, memCommittee;
            while (rs.next()) {
                fName = rs.getString("firstName");
                lName = rs.getString("lastName");
                memEmail = rs.getString("email");
                oldEmail = rs.getString("guardianEmail");
                memLevel = rs.getString("membershipLevel");
                memCommittee = rs.getString("committeeMembership");
                if (memEmail != null) {
                    if (oldEmail != null) {
                        System.out.println(
                                System.lineSeparator() + "Name: " + fName + " " + lName +
                                        System.lineSeparator() + "Email: " + memEmail +
                                        System.lineSeparator() + "Guardian Email: " + oldEmail +
                                        System.lineSeparator() + "Membership Level: " + memLevel +
                                        System.lineSeparator() + "Committee Membership: " + memCommittee);
                    } else {
                        System.out.println(
                                System.lineSeparator() + "Name: " + fName + " " + lName +
                                        System.lineSeparator() + "Email: " + memEmail +
                                        System.lineSeparator() + "Membership Level: " + memLevel +
                                        System.lineSeparator() + "Committee Membership: " + memCommittee);
                    }
                } else if (oldEmail != null) {
                    System.out.println(
                            System.lineSeparator() + "Name: " + fName + " " + lName +
                                    System.lineSeparator() + "Guardian Email: " + oldEmail +
                                    System.lineSeparator() + "Membership Level: " + memLevel +
                                    System.lineSeparator() + "Committee Membership: " + memCommittee);
                }
                else {
                    System.out.println(
                            System.lineSeparator() + "Name: " + fName + " " + lName +
                                    System.lineSeparator() + "Membership Level: " + memLevel +
                                    System.lineSeparator() + "Committee Membership: " + memCommittee);
                }
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("An error occurred. See the following error: " + e);
        }
    }

    public static void addMember (Connection connection) {
        Scanner memberScanner = new Scanner(System.in);
        System.out.println("Enter the first name of the member: ");

        String first = memberScanner.nextLine();

        System.out.println("Enter the last name of the member: ");
        String last = memberScanner.nextLine();

        System.out.println("Does the user have their own email? Yes or No");
        String hasEmail = memberScanner.nextLine();
        String email = "";
        if (hasEmail.equalsIgnoreCase("yes")) {
            System.out.println("Enter the member's email: ");
            email = memberScanner.nextLine();
        }
        System.out.println("Enter the guardians email: ");
        String emailGuardian = memberScanner.nextLine();

        boolean validLevel = false;
        String level;
        do {
            System.out.println("What level is the member? (Candidate, Ordeal, Brotherhood, Vigil");
            level = memberScanner.nextLine();
            switch (level.toLowerCase()) {
                case "ordeal":
                    validLevel = true;
                    break;
                case "candidate":
                    validLevel = true;
                    break;
                case "brotherhood":
                    validLevel = true;
                    break;
                case "vigil":
                    validLevel = true;
                    break;
            }
        } while (!validLevel);
        try {
            String sqlQuery = "INSERT INTO Members (firstName, lastName, email, guardianEmail, membershipLevel) " +
                    "VALUES ('" + first + "', '" + last + "' + '" + email + "' + '" + emailGuardian + "' + '" + level + "')";
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.executeUpdate();

            memberScanner.close();
        }
        catch (SQLException e) {
            System.out.println("An error occurred when inserting a new member. See the following: " + e);
        }
    }

    public static void sendEmail (Connection connection) {
        ArrayList<String> validAlias = new ArrayList<String>();

        try {
            String sqlQuery = "SELECT emailAlias FROM Committee";
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);

            ResultSet rs = stmt.executeQuery(sqlQuery);

            String alias;

            while (rs.next()) {
                alias = rs.getString("emailAlias");
                validAlias.add(alias);
            }
        }
        catch (SQLException e) {
            System.out.println("An error occurred when fetching the alias list. See the following: " + e);
        }

        Scanner emailScanner = new Scanner(System.in);
        System.out.println("Select an alias to send an email to:");
        String alias = emailScanner.nextLine();

        if(validAlias.contains(alias)) {
            System.out.println("What do you want to send?");
            String body = emailScanner.nextLine();

            System.out.println("Email sent to " + alias + ": " + System.lineSeparator() + body);
        }
        else {
            System.out.println("Invalid alias.");
        }
    }

    public static void addCommittee(Connection connection) {
        Scanner memberScanner = new Scanner(System.in);
        System.out.println("Enter the first name of the member: ");

        String first = memberScanner.nextLine();

        System.out.println("Enter the last name of the member: ");
        String last = memberScanner.nextLine();

        System.out.println("Does the user have their own email? Yes or No");
        String hasEmail = memberScanner.nextLine();
        String email = "";
        if (hasEmail.equalsIgnoreCase("yes")) {
            System.out.println("Enter the member's email: ");
            email = memberScanner.nextLine();
        }
        System.out.println("Enter the guardians email: ");
        String emailGuardian = memberScanner.nextLine();

        boolean validLevel = false;
        String level;
        do {
            System.out.println("What level is the member? (Candidate, Ordeal, Brotherhood, Vigil");
            level = memberScanner.nextLine();
            switch (level.toLowerCase()) {
                case "ordeal":
                    validLevel = true;
                    break;
                case "candidate":
                    validLevel = true;
                    break;
                case "brotherhood":
                    validLevel = true;
                    break;
                case "vigil":
                    validLevel = true;
                    break;
            }
        } while (!validLevel);

        System.out.println(System.lineSeparator() + "What committee is the member joining?");
        System.out.println("Valid options: ");

        ArrayList<String> validCommittees = new ArrayList<String>();

        try {
            String sqlQuery = "SELECT committeeName FROM Committee";
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);

            ResultSet rs = stmt.executeQuery(sqlQuery);

            String alias;

            while (rs.next()) {
                alias = rs.getString("committeeName");
                validCommittees.add(alias);
            }

            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            System.out.println("An error occurred when fetching the alias list. See the following: " + e);
        }

        for(String committee : validCommittees) {
            System.out.println(committee);
        }
        System.out.println();

        String committeeInsert = memberScanner.nextLine();
        if (!validCommittees.contains(committeeInsert)) {
            System.out.println("Invalid committee.");
        }
        else {
            try {
                String sqlQuery = "UPDATE Members SET committeeMembership = (SELECT committeeName FROM Committee WHERE committeeName = ?) " +
                        "WHERE firstName = ? AND lastName = ? AND membershipLevel = ? AND (email = ? OR guardianEmail = ?)";

                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, committeeInsert);
                stmt.setString(2, first);
                stmt.setString(3, last);
                stmt.setString(4, level);
                stmt.setString(5, email);
                stmt.setString(6, emailGuardian);
                stmt.executeUpdate();

                stmt.close();
                memberScanner.close();
            } catch (SQLException e) {
                System.out.println("An error occurred when inserting a new member. See the following: " + e);
            }
        }
    }
}