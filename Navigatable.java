package photoalbum;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public interface Navigatable {

    //This is Yash's code, I moved it here for reusability - Joe
    default void switchScene(String fxmlPath, ActionEvent input) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlPath));
        Parent sceneManager = loader.load();
        Scene newScene = new Scene(sceneManager);
        Stage primaryStage = (Stage) ((Node) input.getSource()).getScene().getWindow();
        primaryStage.setScene(newScene);
        primaryStage.show();

        if (loader.getController() instanceof Initializable) {
            ((Initializable) loader.getController()).initialize(null, null);
        }
    }
}
