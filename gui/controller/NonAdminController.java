package photoalbum.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import photoalbum.Photos;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

public class NonAdminController implements LogoutController, Navigatable{
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

    public void initialize(){
        refreshList();
        titledPane.setText(Photos.driver.getCurrentUser().getName() + "'s Albums: ");

        SearchByString.textProperty().addListener((observable, old, newVal) -> {
            search(newVal);
        });

        Photos.driver.getCurrentUser().lookAt("*");
    }

    public void refreshList(){
        ObservableList<String> temp = FXCollections.observableArrayList(Photos.driver.getCurrentUser().albumNames);
        albumsList.setItems(temp);
    }

    public void openAlbum(ActionEvent e) throws IOException{
        if(albumsList.getSelectionModel().getSelectedItem() == null){
            showAlert("Invalid Selection - ERROR", "There is no album selected", Alert.AlertType.ERROR);
            return;
        }

        if (!Photos.driver.getCurrentUser().lookAt((String) albumsList.getSelectionModel().getSelectedItem()))
            return;

        switchScene("/photoalbum/gui/fxml/AlbumPageUpdated.fxml", e.getSource());
    }

    public void createAlbum(ActionEvent e) {
        TextInputDialog td = new TextInputDialog();

        td.setHeaderText("Enter new album name");

        Optional<String> enteredAlbumNameOpt = td.showAndWait();

        if(!enteredAlbumNameOpt.isPresent()) {
            showAlert("Invalid Input - ERROR", "Album name must be at least 1 character", Alert.AlertType.ERROR);
            return;
        }

        String enteredAlbumName = enteredAlbumNameOpt.get();

        if(Photos.driver.getCurrentUser().albumNames.contains(enteredAlbumName)){
            showAlert("Invalid Input - ERROR", "Album \"" + enteredAlbumName + "\" already exists", Alert.AlertType.ERROR);
            return;
        }

        Photos.driver.getCurrentUser().makeAlbum(enteredAlbumName);
        refreshList();
    }

    public void renameAlbum(ActionEvent e){
        if(albumsList.getSelectionModel().getSelectedItem() == null){
            showAlert("Invalid Selection - ERROR", "There is no album selected", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog td = new TextInputDialog();

        td.setHeaderText("Enter replacement album name");

        Optional<String> enteredAlbumNameOpt = td.showAndWait();

        if(!enteredAlbumNameOpt.isPresent()) {
            showAlert("Invalid Input - ERROR", "Album name must be at least 1 character", Alert.AlertType.ERROR);
            return;
        }

        String enteredAlbumName = enteredAlbumNameOpt.get();

        if(Photos.driver.getCurrentUser().albumNames.contains(enteredAlbumName)){
            showAlert("Invalid Input - ERROR", "Album \"" + enteredAlbumName + "\" already exists", Alert.AlertType.ERROR);
            return;
        }

        Photos.driver.getCurrentUser().renameAlbum((String) albumsList.getSelectionModel().getSelectedItem(), enteredAlbumName);
        refreshList();
    }

    public void search(ActionEvent e) throws IOException{
        popupAndWait("/photoalbum/gui/fxml/SearchPage.fxml", "*","Photo Search");
        switchScene("/photoalbum/gui/fxml/NonAdminPage.fxml",e.getSource());
    }

    public void deleteAlbum(ActionEvent e){
        if(albumsList.getSelectionModel().getSelectedItem() == null){
            showAlert("Invalid Selection - ERROR", "There is no album selected", Alert.AlertType.ERROR);
            return;
        }

        Photos.driver.getCurrentUser().deleteAlbum((String) albumsList.getSelectionModel().getSelectedItem());
        refreshList();
    }

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

    public void logOut(ActionEvent e) throws IOException {
        logMeOut(e);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
