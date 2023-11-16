package photoalbum;


import javafx.fxml.FXML;

public class AdminController implements LogoutController {

    @FXML
    ListView<String> UserList;
    @FXML
    Button AddUserButton;
    @FXML
    Button DelUserButton;
    @FXML
    Button LogOffButton;
    @FXML
    TextField Enteruser;


}