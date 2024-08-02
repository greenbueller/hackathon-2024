package com.greenbueller.mattalert;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

import java.sql.*;

public class Main extends Application {
    private Connection connection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Oracle Driver setup
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            showAlert("Error", "Unable to find Oracle Driver. See the following error: " + e);
            return;
        }

        // Validate connection arguments
        if (getParameters().getRaw().size() < 3) {
            showAlert("Error", "Command Line Arguments must be included.");
            return;
        }

        String USERID = getParameters().getRaw().get(0);
        String PASSWORD = getParameters().getRaw().get(1);
        String CONNECTIONSTRING = getParameters().getRaw().get(2);

        // Establish connection
        try {
            connection = DriverManager.getConnection(CONNECTIONSTRING, USERID, PASSWORD);
        } catch (SQLException e) {
            showAlert("Error", "Connection failed. See the following: " + e);
            return;
        }

        // Create UI
        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(10));

        // Create buttons and layout them horizontally
        HBox buttonBox = new HBox(10);
        Button btnGetCommittees = new Button("Get Committees");
        Button btnGetMembers = new Button("Get Members");
        Button btnAddMember = new Button("Add Member");
        Button btnSendEmail = new Button("Send Email");
        Button btnAddCommittee = new Button("Add to Committee");

        styleButton(btnGetCommittees);
        styleButton(btnGetMembers);
        styleButton(btnAddMember);
        styleButton(btnSendEmail);
        styleButton(btnAddCommittee);

        buttonBox.getChildren().addAll(btnGetCommittees, btnGetMembers, btnAddMember, btnSendEmail, btnAddCommittee);

        TextArea textArea = new TextArea();
        textArea.setPrefHeight(400);
        textArea.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px;");

        btnGetCommittees.setOnAction(e -> getCommittees(textArea));
        btnGetMembers.setOnAction(e -> getMembers(textArea));
        btnAddMember.setOnAction(e -> addMember());
        btnSendEmail.setOnAction(e -> sendEmail());
        btnAddCommittee.setOnAction(e -> addCommittee());

        vbox.getChildren().addAll(buttonBox, textArea);

        Scene scene = new Scene(vbox, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("The Activation Station");
        primaryStage.show();
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px;");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setGraphic(null);
        alert.showAndWait();
    }

    private void getCommittees(TextArea textArea) {
        try {
            Statement stmt = connection.createStatement();
            String sqlQuery = "SELECT * FROM Committee";
            ResultSet rs = stmt.executeQuery(sqlQuery);

            StringBuilder sb = new StringBuilder("");

            while (rs.next()) {
                if (rs.getString("chair") != null && rs.getString("committeeAdvisor") != null) {
                    sb.append("Name: ").append(rs.getString("committeeName"))
                            .append("\nChair: ").append(rs.getString("chair"))
                            .append("\nAdvisor: ").append(rs.getString("committeeAdvisor"))
                            .append("\nEmail Alias: ").append(rs.getString("emailAlias"))
                            .append("\n\n");
                }
                else if (rs.getString("chair") != null && rs.getString("committeeAdvisor") == null) {
                    sb.append("Name: ").append(rs.getString("committeeName"))
                            .append("\nChair: ").append(rs.getString("chair"))
                            .append("\nEmail Alias: ").append(rs.getString("emailAlias"))
                            .append("\n\n");
                }
                else if (rs.getString("chair") == null && rs.getString("committeeAdvisor") != null) {
                    sb.append("Name: ").append(rs.getString("committeeName"))
                            .append("\nAdvisor: ").append(rs.getString("committeeAdvisor"))
                            .append("\nEmail Alias: ").append(rs.getString("emailAlias"))
                            .append("\n\n");
                }
                else {
                    sb.append("Name: ").append(rs.getString("committeeName"))
                            .append("\nEmail Alias: ").append(rs.getString("emailAlias"))
                            .append("\n\n");
                }
            }

            textArea.setText(sb.toString());
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            showAlert("Error", "An error occurred. See the following error: " + e);
        }
    }

    private void getMembers(TextArea textArea) {
        // Create Yes/No buttons for specific member query
        Alert specificMemberAlert = new Alert(Alert.AlertType.NONE, "Do you want to get a specific member?", ButtonType.YES, ButtonType.NO);
        specificMemberAlert.setTitle("Get Members");
        specificMemberAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                TextInputDialog idDialog = new TextInputDialog();
                idDialog.setTitle("Get Member");
                idDialog.setHeaderText("What is their ID?");
                idDialog.setContentText("ID:");

                idDialog.showAndWait().ifPresent(id -> {
                    try {
                        Statement stmt = connection.createStatement();
                        String sqlQuery = "SELECT * FROM Members WHERE memberID = " + id;
                        ResultSet rs = stmt.executeQuery(sqlQuery);

                        StringBuilder sb = new StringBuilder("");

                        String memID, fName, lName, email, guardianEmail, memLevel, committee;
                        while (rs.next()) {
                            memID = rs.getString("memberID");
                            fName = rs.getString("firstName");
                            lName = rs.getString("lastName");
                            String name = fName + " " + lName;
                            email = rs.getString("email");
                            guardianEmail = rs.getString("guardianEmail");
                            memLevel = rs.getString("membershipLevel");
                            committee = rs.getString("committeeMembership");


                            if (!email.equals("null") && !guardianEmail.equals("null")) {
                                sb.append("ID: ").append(memID)
                                        .append("\nName: ").append(name)
                                        .append("\nEmail: ").append(email)
                                        .append("\nGuardian Email: ").append(guardianEmail)
                                        .append("\nMembership Level: ").append(memLevel)
                                        .append("\nCommittee Membership: ").append(committee)
                                        .append("\n\n");
                            }
                            else if (!email.equals("null") && guardianEmail.equals("null")) {
                                sb.append("ID: ").append(memID)
                                        .append("\nName: ").append(name)
                                        .append("\nEmail: ").append(email)
                                        .append("\nMembership Level: ").append(memLevel)
                                        .append("\nCommittee Membership: ").append(committee)
                                        .append("\n\n");
                            }
                            else if (!guardianEmail.equals("null") && email.equals("null")) {
                                sb.append("ID: ").append(memID)
                                        .append("\nName: ").append(name)
                                        .append("\nGuardian Email: ").append(guardianEmail)
                                        .append("\nMembership Level: ").append(memLevel)
                                        .append("\nCommittee Membership: ").append(committee)
                                        .append("\n\n");
                            }
                            else {
                                sb.append("ID: ").append(memID)
                                        .append("\nName: ").append(name)
                                        .append("\nMembership Level: ").append(memLevel)
                                        .append("\nCommittee Membership: ").append(committee)
                                        .append("\n\n");
                            }
                        }

                        textArea.setText(sb.toString());
                        rs.close();
                        stmt.close();
                    } catch (SQLException e) {
                        showAlert("Error", "An error occurred. See the following error: " + e);
                    }
                });
            } else {
                // Create Yes/No buttons for committee member query
                Alert committeeAlert = new Alert(Alert.AlertType.NONE, "Do you want all members of a specific committee?", ButtonType.YES, ButtonType.NO);
                committeeAlert.setTitle("Get Members");
                committeeAlert.showAndWait().ifPresent(commResponse -> {
                    if (commResponse == ButtonType.YES) {
                        TextInputDialog aliasDialog = new TextInputDialog();
                        aliasDialog.setTitle("Get Members by Committee");
                        aliasDialog.setHeaderText("What is the alias of the committee you want?");
                        aliasDialog.setContentText("Alias:");

                        aliasDialog.showAndWait().ifPresent(alias -> {
                            try {
                                String sqlQuery = "SELECT * FROM Members WHERE committeeMembership = (SELECT committeeName FROM Committee WHERE emailAlias = ?)";
                                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                                stmt.setString(1, alias);
                                ResultSet rs = stmt.executeQuery();

                                StringBuilder sb = new StringBuilder("");

                                while (rs.next()) {
                                    sb.append("ID: ").append(rs.getString("memberID"))
                                            .append("\nName: ").append(rs.getString("firstName")).append(" ").append(rs.getString("lastName"))
                                            .append("\nEmail: ").append(rs.getString("email"))
                                            .append("\nGuardian Email: ").append(rs.getString("guardianEmail"))
                                            .append("\nMembership Level: ").append(rs.getString("membershipLevel"))
                                            .append("\nCommittee Membership: ").append(rs.getString("committeeMembership"))
                                            .append("\n\n");
                                }

                                textArea.setText(sb.toString());
                                rs.close();
                                stmt.close();
                            } catch (SQLException e) {
                                showAlert("Error", "An error occurred. See the following error: " + e);
                            }
                        });
                    } else {
                        try {
                            Statement stmt = connection.createStatement();
                            String sqlQuery = "SELECT * FROM Members";
                            ResultSet rs = stmt.executeQuery(sqlQuery);

                            StringBuilder sb = new StringBuilder("Members:\n");

                            while (rs.next()) {
                                sb.append("ID: ").append(rs.getString("memberID"))
                                        .append("\nName: ").append(rs.getString("firstName")).append(" ").append(rs.getString("lastName"))
                                        .append("\nEmail: ").append(rs.getString("email"))
                                        .append("\nGuardian Email: ").append(rs.getString("guardianEmail"))
                                        .append("\nMembership Level: ").append(rs.getString("membershipLevel"))
                                        .append("\nCommittee Membership: ").append(rs.getString("committeeMembership"))
                                        .append("\n\n");
                            }

                            textArea.setText(sb.toString());
                            rs.close();
                            stmt.close();
                        } catch (SQLException e) {
                            showAlert("Error", "An error occurred. See the following error: " + e);
                        }
                    }
                });
            }
        });
    }


    private void addMember() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Member");

        // Create UI elements
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField emailField = new TextField();
        emailField.setDisable(false); // Initially enabled since the checkbox will be checked
        TextField guardianEmailField = new TextField();

        ComboBox<String> levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll("Candidate", "Ordeal", "Brotherhood", "Vigil");

        CheckBox hasEmailCheckBox = new CheckBox("Does the member have their own email?");
        hasEmailCheckBox.setSelected(true); // Set checkbox as checked by default

        // Set emailField visibility based on the checkbox state
        hasEmailCheckBox.setOnAction(event -> {
            emailField.setDisable(!hasEmailCheckBox.isSelected());
            if (!hasEmailCheckBox.isSelected()) {
                emailField.clear(); // Clear the email field if unchecked
            }
        });

        // Initially, the email field is enabled since the checkbox is checked
        VBox vbox = new VBox(10,
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                hasEmailCheckBox,
                new Label("Email:"), emailField,
                new Label("Guardian Email:"), guardianEmailField,
                new Label("Membership Level:"), levelComboBox
        );

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Call addMember method with gathered data
                return ButtonType.OK;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Insert into DB here
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String email = hasEmailCheckBox.isSelected() ? emailField.getText() : "";
                String guardianEmail = guardianEmailField.getText();
                String membershipLevel = levelComboBox.getValue();

                try {
                    String sqlQuery = "INSERT INTO Members (firstName, lastName, email, guardianEmail, membershipLevel) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setString(3, email);
                    stmt.setString(4, guardianEmail);
                    stmt.setString(5, membershipLevel);
                    stmt.executeUpdate();
                    stmt.close();

                    showAlert("Success", "Member added successfully!");
                } catch (SQLException e) {
                    showAlert("Error", "An error occurred when inserting the new member: " + e);
                }
            }
        });
    }


    private void sendEmail() {
        ArrayList<String> validAlias = new ArrayList<>();

        try {
            String sqlQuery = "SELECT emailAlias FROM Committee";
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                validAlias.add(rs.getString("emailAlias"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            showAlert("Error", "An error occurred when fetching the alias list: " + e);
            return;
        }

        // Create a dialog for sending email
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Send Email");

        ComboBox<String> aliasComboBox = new ComboBox<>();
        aliasComboBox.getItems().addAll(validAlias);

        TextArea emailBodyArea = new TextArea();
        emailBodyArea.setPromptText("Enter your message here...");

        VBox vbox = new VBox(10,
                new Label("Select an alias to send an email to:"), aliasComboBox,
                new Label("Email Body:"), emailBodyArea
        );

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return ButtonType.OK;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String selectedAlias = aliasComboBox.getValue();
                String emailBody = emailBodyArea.getText();

                if (selectedAlias != null && !emailBody.isEmpty()) {
                    // Simulate sending the email (replace this with actual email sending logic)
                    System.out.println("Email sent to " + selectedAlias + ": " + System.lineSeparator() + emailBody);
                    showAlert("Success", "Email sent successfully to " + selectedAlias + "!");
                } else {
                    showAlert("Error", "Please select an alias and enter a message.");
                }
            }
        });
    }

    private void addCommittee() {
        // Create a dialog for adding a member to a committee
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Member to Committee");

        // Create TextField for Member ID
        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Enter Member ID");

        // Create ComboBox for committees
        ComboBox<String> committeeComboBox = new ComboBox<>();
        committeeComboBox.setPromptText("Select Committee");

        // Fetch valid committees and populate the ComboBox
        try {
            ArrayList<String> validCommittees = new ArrayList<>();
            String committeeQuery = "SELECT committeeName FROM Committee";
            PreparedStatement committeeStmt = connection.prepareStatement(committeeQuery);
            ResultSet committeeRs = committeeStmt.executeQuery();

            while (committeeRs.next()) {
                validCommittees.add(committeeRs.getString("committeeName"));
            }

            committeeRs.close();
            committeeStmt.close();

            committeeComboBox.getItems().addAll(validCommittees);
        } catch (SQLException e) {
            showAlert("Error", "An error occurred while fetching committees: " + e);
            return;
        }

        // Set up the dialog content
        VBox vbox = new VBox(10,
                new Label("Enter Member ID:"),
                memberIdField,
                new Label("Select Committee:"),
                committeeComboBox
        );

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return ButtonType.OK;
            }
            return null;
        });

        // Show the dialog and handle the response
        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String memberId = memberIdField.getText();
                String selectedCommittee = committeeComboBox.getValue();

                if (memberId.isEmpty() || selectedCommittee == null) {
                    showAlert("Error", "Please enter a member ID and select a committee.");
                    return;
                }

                try {
                    // Check if the member exists
                    String memberQuery = "SELECT * FROM Members WHERE memberID = ?";
                    PreparedStatement memberStmt = connection.prepareStatement(memberQuery);
                    memberStmt.setString(1, memberId);
                    ResultSet memberRs = memberStmt.executeQuery();

                    if (!memberRs.next()) {
                        showAlert("Error", "Member ID not found.");
                        return;
                    }

                    String updateQuery = "UPDATE Members SET committeeMembership = ? WHERE memberID = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setString(1, selectedCommittee);
                    updateStmt.setString(2, memberId);
                    updateStmt.executeUpdate();

                    String getMembersName = "SELECT firstName, lastName FROM Members WHERE memberID = ?";
                    PreparedStatement getMemberStmt = connection.prepareStatement(getMembersName);
                    getMemberStmt.setString(1, memberId);
                    ResultSet rs = getMemberStmt.executeQuery();
                    String name = "";
                    if (rs.next()) {
                        name = rs.getString("firstName") + " " + rs.getString("lastName");
                    }

                    if (selectedCommittee.equals("N/A")) {
                        showAlert("Success", name + " has been unassigned from their committee.");
                    }
                    else {
                        showAlert("Success", name + " has been added to " + selectedCommittee);
                    }
                    memberRs.close();
                    memberStmt.close();
                } catch (SQLException e) {
                    showAlert("Error", "An error occurred while adding the member to the committee: " + e);
                }
            }
        });
    }

}
