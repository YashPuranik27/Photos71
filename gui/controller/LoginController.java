/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package photoalbum.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

import photoalbum.Photos;
import photoalbum.model.accounts.User;

/**
 * This class serves as the controller for the login view in a JavaFX application.
 * It handles user authentication and navigation to the appropriate view based on the
 * type of user logging in (admin or non-admin).
 */
public class LoginController implements Navigatable{ // default constructor

    @FXML
    Button loginButton; // Button for initiating the login process.
    @FXML
    TextField nameInput; // TextField for user to input their username.
    /**
     * Handles the key press event on the login page. If the ENTER key is pressed,
     * it triggers the login process.
     *
     * @param e The key event that triggered this method.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    public void keyPress(KeyEvent e) throws IOException{
        if(e.getCode().equals(KeyCode.ENTER))
            login(e.getSource());
    }
    /**
     * Called when the login button is pressed, this method initiates the login process.
     *
     * @param e The action event that triggered this method.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    public void login(ActionEvent e) throws IOException{
        login(e.getSource());
    }
    /**
     * Processes the login for the user, checking the username and directing the user to the
     * appropriate view depending on whether the username is 'admin' or another user.
     *
     * @param source The source object of the event triggering login.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    private void login(Object source) throws IOException {
        String username = nameInput.getText().trim();

        if (username.isEmpty()) {
            showAlert("Empty Input", "Please enter a username", AlertType.ERROR);
            return;
        }

        if (username.equals("admin")) {
            Photos.driver.setCurrentUser(new User("admin"));
            switchScene("/photoalbum/gui/fxml/AdminPage.fxml", source);
        } else if (Photos.driver.checkUser(username)) {
            switchScene("/photoalbum/gui/fxml/NonAdminPage.fxml", source);
        } else {
            showAlert("Invalid Input - ERROR", "The entered username is invalid!", AlertType.ERROR);
        }
    }
    /**
     * Displays an alert dialog with the specified title, content, and alert type.
     *
     * @param title   The title of the alert dialog.
     * @param content The content of the alert dialog.
     * @param type    The type of the alert (e.g., ERROR, INFORMATION).
     */
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    /**
     * Initializes the controller. This method is called automatically when the login view is loaded.
     * It can be used to set any initial configurations such as resetting the current user.
     */
    public void initialize(){
        Photos.driver.setCurrentUser(null);
    }
}