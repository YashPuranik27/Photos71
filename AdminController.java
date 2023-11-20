package photoalbum;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.io.IOException;

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

    public void start() { // This method is called when the scene is loaded
        refresh();
        if (!observableArrayList.isEmpty()) UserList.getSelectionModel().select(0);
    }

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
        //Persistence.save(Photos.driver); // Save the changes to info.dat through Persistence.java
        refresh();
        UserList.getSelectionModel().select(userInput);
        Enteruser.clear();
    }

    private void showAlert(String title, String content) { // Helper method to show alerts. opens a new alert window
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


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

    public void refresh() { // Refreshes the listview
        UserList.refresh();
        observableArrayList = FXCollections.observableArrayList(Photos.driver.admin.getUsernameList()); // Gets the list of usernames
        UserList.setItems(observableArrayList);
        UserList.refresh();
    }

    public void initialize(){
        refresh();
    }

    public void logOut(ActionEvent e) throws IOException { // Logs out the user
        logMeOut(e);
    }

}