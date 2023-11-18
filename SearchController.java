package photoalbum;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class SearchController implements LogoutController{
    @FXML
    Button makeAlbum;
    @FXML
    Button logout;
    @FXML
    Button searchByTagButton;
    @FXML
    Button searchByDateButton;
    @FXML
    Button backButton;

    @FXML
    TextField mandatoryTag1Name;
    @FXML
    TextField mandatoryTag1Value;
    @FXML
    TextField mandatoryTag2Name;
    @FXML
    TextField mandatoryTag2Value;
    @FXML
    TextField beginningDateInput;
    @FXML
    TextField endDateInput;

    public void saveResultsAsAlbum(ActionEvent e){

    }

    public void logOut(ActionEvent e) throws IOException {
        logMeOut(e);
    }

    public void searchByTag(ActionEvent e){

    }

    public void searchByDate(ActionEvent e){

    }

    public void goBack(ActionEvent e){

    }
}
