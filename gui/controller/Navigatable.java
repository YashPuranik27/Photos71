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

public interface Navigatable {

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
