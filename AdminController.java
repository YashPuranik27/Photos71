package photoalbum;


import javafx.fxml.FXML;

public class AdminController implements LogoutController {

    @FXML
    ListView<String> UserList; // Listview of users
    @FXML
    Button AddUserButton; // Button to add a user
    @FXML
    Button DelUserButton; // Button to delete a user
    @FXML
    Button LogOffButton; // Button to log off
    @FXML
    TextField Enteruser; // Text field to enter a username

    ObservableList<String> observableArrayList;

    public void start() { // This method is called when the scene is loaded
        refresh();
        if (!observableArrayList.isEmpty()) UserList.getSelectionModel().select(0);
    }
    
    public void refresh() { // Refreshes the listview
        UserList.refresh();
        observableArrayList = FXCollections.observableArrayList(Photos.driver.admin.getUsernameList()); // Gets the list of usernames
        UserList.setItems(observableArrayList);
        UserList.refresh();
    }

    public void logOut(ActionEvent e) throws IOException { // Logs out the user
        logMeOut(e);
    }

}