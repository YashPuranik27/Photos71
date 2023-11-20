package photoalbum;

import java.io.IOException;
import java.util.Objects;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Photos extends Application {
    private static final Logger LOGGER = Logger.getLogger(Photos.class.getName());
    public static Persistence driver = new Persistence();

    @Override
    public void start(Stage initialStage) {
        try {
            StackPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginPage.fxml")));

            initialStage.setScene(new Scene(root));
            initialStage.setResizable(false);
            initialStage.setTitle("Photos Application");
            initialStage.show();

            initialStage.setOnCloseRequest(event -> {
                try {
                    if(driver.getCurrentUser() != null){
                        Optional<ButtonType> confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save before closing?", ButtonType.YES, ButtonType.NO)
                                .showAndWait();
                        if (confirm.isPresent() && confirm.get() == ButtonType.YES) {
                            Persistence.save(Photos.driver);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error: cannot save persistence data", e);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error: cannot start the application", e);
        }
    }

    public static void main(String[] args) {
        try {
            driver = Persistence.load();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error: cannot load persistence data", e);
        }
        launch(args);
    }
}