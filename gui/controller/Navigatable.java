/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package photoalbum.gui.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
/**
 * The Navigatable interface provides default methods for scene navigation and
 * popup handling within a JavaFX application. It is designed to be implemented
 * by controllers that require scene switching and popup modal functionalities.
 */
public interface Navigatable {
    /**
     * Switches the current scene to a new scene determined by the FXML path provided.
     * It also initializes the controller associated with the new FXML if it implements
     * the Initializable interface.
     *
     * @param fxmlPath The relative path to the FXML file that describes the new scene.
     * @param source   The source object of the event triggering the scene switch.
     * @throws IOException If the FXML file cannot be loaded.
     */
    //This is Yash's code, I moved it here for reusability - Joe
    default void switchScene(String fxmlPath, Object source) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlPath));
        Parent sceneManager = loader.load();
        Scene newScene = new Scene(sceneManager);
        Stage primaryStage = (Stage) ((Node) source).getScene().getWindow();
        primaryStage.setScene(newScene);
        primaryStage.show();

        if (loader.getController() instanceof Initializable) {
            ((Initializable) loader.getController()).initialize(null, null);
        }
    }
    /**
     * Creates a popup window and waits for it to be closed before returning control.
     * The popup's content is loaded from the FXML path provided, and it's given a title.
     * The userData is passed to the popup controller for use.
     *
     * @param fxmlPath The relative path to the FXML file that describes the popup content.
     * @param userData Data to be passed to the popup controller.
     * @param title    The title of the popup window.
     */
    default void popupAndWait(String fxmlPath, Object userData, String title) {
        try {
            TitledPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));

            root.setUserData(userData);

            Stage inputStage = new Stage();
            inputStage.setScene(new Scene(root));
            inputStage.setResizable(false);
            inputStage.setTitle(title);

            inputStage.showAndWait();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
