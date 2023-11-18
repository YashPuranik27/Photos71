// done
package photoalbum;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class LoginController implements Navigatable{

    @FXML
    Button loginButton;
    @FXML
    TextField nameInput;

    public void login(ActionEvent input) throws IOException {
        String username = nameInput.getText().trim();

        if (username.isEmpty()) {
            showAlert("Empty Input", "Please enter a username", AlertType.ERROR);
            return;
        }

        if (username.equals("admin")) {
            switchScene("/photoalbum/AdminPage.fxml", input);
        } else if (Photos.driver.checkUser(username)) {
            switchScene("/photoalbum/NonAdminPage.fxml", input);
        } else {
            showAlert("Invalid Input - ERROR", "The entered username is invalid!", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void addToUserData(){

    }
}