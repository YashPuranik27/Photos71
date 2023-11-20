/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package photoalbum.gui.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import photoalbum.Photos;

import java.io.IOException;
/**
 * This class serves as the controller for the Admin view in a JavaFX application.
 * It manages the user interface interactions related to administrative tasks such as
 * adding or deleting users, refreshing the user list, and logging off.
 */
public class AdminController implements LogoutController {

    @FXML
    ListView<String> UserList; // Listview of users
    @FXML
    Button AddUserButton; // Button to add a user
    @FXML
    Button DelUserButton; // Button to delete a user
    @FXML
    Button LogOffButton; // Button to log off
    @FXML
    TextField Enteruser; // Text field to enter a username

    ObservableList<String> observableArrayList;

    /**
     * Initializes the controller and refreshes the user list. This method is called automatically
     * when the associated view is loaded.
     */
    public void start() { // This method is called when the scene is loaded
        refresh();
        if (!observableArrayList.isEmpty()) UserList.getSelectionModel().select(0);
    }
    /**
     * Adds a new user based on the input from the 'Enteruser' TextField.
     * It also checks for the validity of the username and uniqueness before adding.
     *
     * @param e The action event triggering this method.
     * @throws IOException if an I/O error occurs.
     */
    public void addUser(ActionEvent e) throws IOException {
        String userInput = Enteruser.getText().trim();

        if (userInput == null || userInput.isEmpty()) { // If user input is empty
            showAlert("Invalid Input - ERROR", "Input field is empty. Please try again.");
            return;
        }

        if (userInput.equals("admin")) { // There is only one admin and we already defined it in admin.java
            showAlert("Invalid Input", "The username admin is protected. Please try a different username!");
            return;
        }

        if (Photos.driver.admin.userExists(userInput) != -1) { // If user already exists
            showAlert("User Already Exists - ERROR", "This Username already exists. Please enter a new username.");
            return;
        }


        Photos.driver.admin.addUser(userInput);

        if(userInput.equals("stock")){
            Photos.driver.admin.regenerateStock();
        }
        //Persistence.save(Photos.driver); // Save the changes to info.dat through Persistence.java
        refresh();
        UserList.getSelectionModel().select(userInput);
        Enteruser.clear();
    }
    /**
     * Displays an alert dialog with a specified title and content.
     *
     * @param title   The title of the alert dialog.
     * @param content The content text to be displayed inside the alert.
     */
    private void showAlert(String title, String content) { // Helper method to show alerts. opens a new alert window
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Deletes a user from the list based on the selection in the 'UserList' ListView.
     *
     * @param e The action event triggering this method.
     * @throws IOException if an I/O error occurs.
     */
    public void deleteUser(ActionEvent e) throws IOException {
        int deleteIndex = UserList.getSelectionModel().getSelectedIndex(); // Get the index of the selected item
        if (deleteIndex < 0) return; // No selection made, exit early

        String selectedItem = UserList.getSelectionModel().getSelectedItem(); // Get the selected item
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Confirm you want to delete " + selectedItem + ".", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirm Deletion");

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL); // we use orElse to provide a default value

        if (result == ButtonType.OK) {
            Photos.driver.admin.deleteUser(selectedItem);
            refresh();
            //Persistence.save(Photos.driver);

            // this is to adjust the selection in the list
            int usersSize = Photos.driver.admin.getUsers().size();
            if (usersSize > 0) {
                UserList.getSelectionModel().select(Math.max(0, Math.min(deleteIndex, usersSize - 1)));
            }
        }
    }
    /**
     * Refreshes the ListView 'UserList' with the current list of users.
     * It updates the observableArrayList with the latest usernames.
     */
    public void refresh() { // Refreshes the listview
        UserList.refresh();
        observableArrayList = FXCollections.observableArrayList(Photos.driver.admin.getUsernameList()); // Gets the list of usernames
        UserList.setItems(observableArrayList);
        UserList.refresh();
    }
    /**
     * Initializes the controller class. This method is automatically called after the FXML fields have been injected.
     */
    public void initialize(){
        refresh();
    }
    /**
     * Handles the logout operation for the admin user.
     *
     * @param e The action event triggering this method.
     * @throws IOException if an I/O error occurs.
     */
    public void logOut(ActionEvent e) throws IOException { // Logs out the user
        logMeOut(e);
    }

}