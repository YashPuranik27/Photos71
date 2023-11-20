/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package Photos71.src.gui.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.fxml.FXMLLoader;
import Photos71.src.model.data.Persistence;
import Photos71.src.photos.Photos;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
/**
 * The LogoutController interface defines the default behavior for controllers
 * that have the functionality to log out a user and save their data.
 *
 * @author Yash Puranik, Joseph Arrigo
 */
public interface LogoutController {
    /**
     * Logs out the user from the current session with an option to save their data.
     * If the user confirms, the application's state is saved and the user is redirected
     * to the login page.
     *
     * @param e The action event that triggered the logout.
     * @throws IOException If an I/O error occurs during scene transition or saving data.
     */
    default void logMeOut(ActionEvent e) throws IOException {
        Optional<ButtonType> confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to save and logout?", ButtonType.OK, ButtonType.CANCEL)
                .showAndWait();
        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            Persistence.save(Photos.driver);
            Parent newScene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Photos71/src/gui/fxml/LoginPage.fxml")));
            Stage appStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            appStage.setScene(new Scene(newScene));
            appStage.show();
        }
    }
}
