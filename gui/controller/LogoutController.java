// done
package photoalbum.gui.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.fxml.FXMLLoader;
import photoalbum.model.data.Persistence;
import photoalbum.Photos;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public interface LogoutController {

    default void logMeOut(ActionEvent e) throws IOException {
        Optional<ButtonType> confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to save and logout?", ButtonType.OK, ButtonType.CANCEL)
                .showAndWait();
        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            Persistence.save(Photos.driver);
            Parent newScene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/photoalbum/gui/fxml/LoginPage.fxml")));
            Stage appStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            appStage.setScene(new Scene(newScene));
            appStage.show();
        }
    }
}
