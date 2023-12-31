/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package Photos71.src.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import Photos71.src.photos.Photos;
import Photos71.src.model.data.Album;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class is the controller for the non-admin user interface of the photo album application.
 * It handles operations such as opening, creating, renaming, and deleting albums, searching for
 * albums by name, and logging out.
 *
 * @author Yash Puranik, Joseph Arrigo
 */
public class NonAdminController implements LogoutController, Navigatable{ // default constructor
    //<editor-fold desc="FXML element declarations (Collapsible region)">
    @FXML
    Button openAlbum;
    @FXML
    Button createAlbum;
    @FXML
    Button renameAlbum;
    @FXML
    Button deleteAlbum;
    @FXML
    Button logout;
    @FXML
    Button searchButton;

    @FXML
    TextField SearchByString;

    @FXML
    ListView albumsList;
    @FXML
    TitledPane titledPane;

    @FXML
    Label userTitle;
    //</editor-fold>

    private ObservableList<HBox> hboxes = FXCollections.observableArrayList();
    /**
     * Initializes the controller, setting up the UI elements and their event handlers.
     */
    public void initialize(){
        refreshList();
        titledPane.setText(Photos.driver.getCurrentUser().getName() + "'s Albums: ");

        SearchByString.textProperty().addListener((observable, old, newVal) -> {
            search(newVal);
        });

        Photos.driver.getCurrentUser().lookAt("*");
    }
    /**
     * Refreshes the album list display with the current user's albums.
     */
    public void refreshList(){
        albumsList.getItems().clear();
        hboxes.clear();

        for(String name : Photos.driver.getCurrentUser().getAlbumNameList())
            hboxes.add(makeHBox(Photos.driver.getCurrentUser().getAlbum(name)));
        albumsList.setItems(hboxes);
    }
    /**
     * Creates an HBox layout containing album information.
     *
     * @param in The album to create an HBox for.
     * @return An HBox containing the album's details.
     */
    private HBox makeHBox(Album in) {
            HBox entry = new HBox();
            Text albumName = new Text();
            Text photoCount = new Text();
            Text earlyDate = new Text();
            Text lateDate = new Text();

            earlyDate.setText(" \t Earliest: n/a");
            lateDate.setText(" \t Latest: n/a");

            albumName.setText(in.getAlbumName());

            SimpleDateFormat df = new SimpleDateFormat("MM-dd-YYYY");
        try{
            photoCount.setText(" \t " + in.getPhotos().size() + " Photos");
            if(in.getEarliestPhoto() != null)
                earlyDate.setText(" \t Earliest: " + df.format(in.getEarliestPhoto().getTime()));
            if(in.getLatest() != null)
                lateDate.setText(" \t Latest: " + df.format(in.getLatest().getTime()));
        }
        catch(Exception ex){}

        entry.getChildren().addAll(albumName,photoCount,earlyDate,lateDate);
        entry.setUserData(in.getAlbumName());

        return entry;
    }
    /**
     * Opens the selected album for viewing.
     *
     * @param e The event that triggered this action.
     */
    public void openAlbum(ActionEvent e) {
        if(albumsList.getSelectionModel().getSelectedItem() == null){
            showAlert("Invalid Selection - ERROR", "There is no album selected", Alert.AlertType.ERROR);
            return;
        }

        if (!Photos.driver.getCurrentUser().lookAt((String) ((HBox) albumsList.getSelectionModel().getSelectedItem()).getUserData()))
            return;

        if(Photos.driver.getCurrentUser().getName().equals("stock"))
            if(Photos.driver.getCurrentUser().getLookAt().getAlbumName().equals("stock"))
                Photos.driver.admin.regenerateStock();

        switchScene("/Photos71/src/gui/fxml/AlbumPageUpdated.fxml", e.getSource());
    }
    /**
     * Handles the creation of a new album.
     *
     * @param e The event that triggered this action.
     */
    public void createAlbum(ActionEvent e) {
        TextInputDialog td = new TextInputDialog();

        td.setHeaderText("Enter new album name");

        Optional<String> enteredAlbumNameOpt = td.showAndWait();

        if(!enteredAlbumNameOpt.isPresent())
            return;

        if(enteredAlbumNameOpt.get().trim().equals("")){
            showAlert("Invalid Input - ERROR", "Album name must be at least 1 character", Alert.AlertType.ERROR);
            return;
        }

        String enteredAlbumName = enteredAlbumNameOpt.get().trim();

        if(Photos.driver.getCurrentUser().albumNames.contains(enteredAlbumName)){
            showAlert("Invalid Input - ERROR", "Album \"" + enteredAlbumName + "\" already exists", Alert.AlertType.ERROR);
            return;
        }

        Photos.driver.getCurrentUser().makeAlbum(enteredAlbumName);
        refreshList();
    }
    /**
     * Handles the renaming of an existing album.
     *
     * @param e The event that triggered this action.
     */
    public void renameAlbum(ActionEvent e){
        if(albumsList.getSelectionModel().getSelectedItem() == null){
            showAlert("Invalid Selection - ERROR", "There is no album selected", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog td = new TextInputDialog();

        td.setHeaderText("Enter replacement album name");

        Optional<String> enteredAlbumNameOpt = td.showAndWait();

        if(!enteredAlbumNameOpt.isPresent()){
            return;
        }

        if(enteredAlbumNameOpt.get().length() < 1) {
            showAlert("Invalid Input - ERROR", "Album name must be at least 1 character", Alert.AlertType.ERROR);
            return;
        }

        String enteredAlbumName = enteredAlbumNameOpt.get();

        if(Photos.driver.getCurrentUser().albumNames.contains(enteredAlbumName)){
            showAlert("Invalid Input - ERROR", "Album \"" + enteredAlbumName + "\" already exists", Alert.AlertType.ERROR);
            return;
        }

        Photos.driver.getCurrentUser().renameAlbum((String) ((HBox) albumsList.getSelectionModel().getSelectedItem()).getUserData(), enteredAlbumName);
        refreshList();
    }
    /**
     * Initiates a search for albums based on the user's input.
     *
     * @param e The event that triggered this action.
     */
    public void search(ActionEvent e){
        popupAndWait("/Photos71/src/gui/fxml/SearchPage.fxml", "*","Photo Search");
        switchScene("/Photos71/src/gui/fxml/NonAdminPage.fxml",e.getSource());
    }
    /**
     * Deletes the selected album.
     *
     * @param e The event that triggered this action.
     */
    public void deleteAlbum(ActionEvent e){
        if(albumsList.getSelectionModel().getSelectedItem() == null){
            showAlert("Invalid Selection - ERROR", "There is no album selected", Alert.AlertType.ERROR);
            return;
        }

        Photos.driver.getCurrentUser().deleteAlbum((String) ((HBox) albumsList.getSelectionModel().getSelectedItem()).getUserData());
        refreshList();
    }
    /**
     * Filters the list of albums based on a given search string.
     *
     * @param newVal The new value to filter the list by.
     */
    private void search(String newVal){
        if(newVal.equals("")){
            refreshList();
            return;
        }

        Stream<String> matchStream = Photos.driver.getCurrentUser().albumNames.stream().filter(
                name -> name.contains(newVal)
        );

        ObservableList<String> temp = FXCollections.observableArrayList(matchStream.toList());
        albumsList.setItems(temp);
    }
    /**
     * Logs out the current user.
     *
     * @param e The event that triggered this action.
     */
    public void logOut(ActionEvent e) {
        logMeOut(e);
    }
    /**
     * Displays an alert with a specific title and content.
     *
     * @param title   The title of the alert.
     * @param content The content of the alert.
     * @param type    The type of alert to display.
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
