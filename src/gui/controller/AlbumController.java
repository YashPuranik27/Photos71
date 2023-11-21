/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package Photos71.src.gui.controller;

import Photos71.src.photos.Photos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Photos71.src.model.data.Album;
import Photos71.src.model.data.Persistence;
import Photos71.src.model.data.Photo;
import Photos71.src.model.data.Tag;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Optional;
/**
 * Controller for the album view in the photo album application. This controller
 * handles the interactions within the album view, such as adding and removing photos,
 * navigating between photos, and managing photo tags and details.
 *
 * @author Yash Puranik, Joseph Arrigo
 */
public class AlbumController implements Navigatable, LogoutController{

    //<editor-fold desc="FXML element declarations (Collapsible region)">
    @FXML
    Button addPhoto;
    @FXML
    Button removePhoto;
    @FXML
    Button copyPhoto;
    @FXML
    Button movePhoto;
    @FXML
    Button nextPhoto;
    @FXML
    Button previousPhoto;
    @FXML
    Button logout;
    @FXML
    Button setCaption;
    @FXML
    Button backButton;
    @FXML
    Button filterSelector;

    @FXML
    TextField captionInput;
    @FXML
    TextField tag1;
    @FXML
    TextField tag1val;

    @FXML
    ImageView photoDisplay;
    @FXML
    TextArea detailTextArea;

    @FXML
    ListView<HBox> imageListview;
    @FXML
    ListView<Text> tagList;
    @FXML
    TitledPane titledPane;

    //</editor-fold>

    private Album beingDisplayed;
    private FileChooser chooser = new FileChooser();
    private ArrayList<ImageView> imageList = new ArrayList<>();
    private ObservableList<HBox> hboxes = FXCollections.observableArrayList();
    private Photo selectedPhoto;
    /**
     * Initializes the AlbumController with default settings for the file chooser.
     */
    public AlbumController(){
        chooser.setTitle("Choose image file");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.bmp", "*.gif", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        chooser.setInitialDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
    }
    /**
     * Initializes the controller class. Called after the FXML fields have been injected.
     * Prepares the album view with the photos and sets up listeners for UI interactions.
     *
     */
    public void initialize(){
        beingDisplayed = Photos.driver.getCurrentUser().getLookAt();
        titledPane.setText(Photos.driver.getCurrentUser().getName() + "'s Album: " + beingDisplayed.getAlbumName());

        imageListview.getSelectionModel().selectedItemProperty().addListener((observable, old, newVal) -> {
            if(newVal == null)
                return;

            setSelectedPhoto((Photo) newVal.getUserData());
        });

        reloadPhotos();

        if(imageListview.getItems().size() != 0)
            imageListview.getSelectionModel().select(0);

        detailTextArea.textProperty().addListener((observable, old, newVal) -> {
            if(newVal == null || selectedPhoto == null)
                return;
            Photo ph = (Photo) imageListview.getSelectionModel().getSelectedItem().getUserData();
            if(ph.getDetails().equals(newVal))
                return;
            ph.setDetails(newVal);
        });
    }
    /**
     * Handles the action of adding a tag to a photo.
     *
     * @param e The event that triggered the action.
     */
    public void addTag(ActionEvent e){
        if(selectedPhoto == null){
            showAlert("Invalid Selection - ERROR", "There is no photo selected", Alert.AlertType.ERROR);
            return;
        }
        if(tag1.getText().equals("") || tag1val.getText().equals("")){
            showAlert("Invalid Input - ERROR", "Either the new tag's key or value is empty", Alert.AlertType.ERROR);
            return;
        }

        if(selectedPhoto.hasTag(new Tag(tag1.getText().toLowerCase(), tag1val.getText().toLowerCase()))){
            showAlert("Invalid Input - ERROR", "This key-value pair already exists for this photo", Alert.AlertType.ERROR);
            return;
        }

        selectedPhoto.addTag(tag1.getText().toLowerCase(), tag1val.getText().toLowerCase());
        Text text = new Text();
        text.setText(tag1.getText().toLowerCase() + " : " + tag1val.getText().toLowerCase());
        tagList.getItems().add(text);
    }
    /**
     * Handles the action of removing a tag from a photo.
     *
     * @param e The event that triggered the action.
     */
    public void removeTag(ActionEvent e){
        if(selectedPhoto == null){
            showAlert("Invalid Selection - ERROR", "There is no photo selected", Alert.AlertType.ERROR);
            return;
        }
        if(tagList.getSelectionModel().getSelectedItem() == null){
            showAlert("Invalid Selection - ERROR", "There is no tag selected in the list", Alert.AlertType.ERROR);
            return;
        }
        String tag[] = tagList.getSelectionModel().getSelectedItem().getText().split(" : ");
        selectedPhoto.removeTag(new Tag(tag[0], tag[1]));
        tagList.getItems().remove(tagList.getSelectionModel().getSelectedItem().getText());
        reloadTags();
    }
    /**
     * Moves the selected photo up on the album's photo list.
     *
     * @param e The event that triggered the action.
     */
    public void moveUp(ActionEvent e) {
        if(selectedPhoto == null)
            return;
        int ind = beingDisplayed.getPhotos().indexOf(selectedPhoto);
        if(ind <= 0)
            return;

        Photo temp = beingDisplayed.getPhotos().get(ind-1);
        beingDisplayed.getPhotos().add(beingDisplayed.getPhotos().size(), temp);

        beingDisplayed.getPhotos().set(ind-1, selectedPhoto);
        beingDisplayed.getPhotos().set(ind,beingDisplayed.getPhotos().get(beingDisplayed.getPhotos().size()-1));
        beingDisplayed.getPhotos().remove(beingDisplayed.getPhotos().size()-1);

        reloadPhotos();

        imageListview.getSelectionModel().select(ind-1);
    }
    /**
     * Moves the selected photo down in the album's list view.
     * If the selected photo is the last one, it won't do anything.
     *
     * @param e The event that triggered the action.
     */
    public void moveDown(ActionEvent e){
        if(selectedPhoto == null)
            return;
        int ind = beingDisplayed.getPhotos().indexOf(selectedPhoto);
        if(ind >= beingDisplayed.getPhotos().size() - 1) {
            return;
        }
        Photo temp = beingDisplayed.getPhotos().get(ind+1);
        beingDisplayed.getPhotos().add(beingDisplayed.getPhotos().size(), temp);

        beingDisplayed.getPhotos().set(ind+1, selectedPhoto);
        beingDisplayed.getPhotos().set(ind,beingDisplayed.getPhotos().get(beingDisplayed.getPhotos().size()-1));
        beingDisplayed.getPhotos().remove(beingDisplayed.getPhotos().size()-1);

        reloadPhotos();

        imageListview.getSelectionModel().select(ind+1);

        //try {Persistence.save(Photos.driver);}
        //    catch (IOException ex) {}
    }
    /**
     * Sets the selected photo in the album's view and updates the display accordingly.
     * If the new photo is null, it will clear the photo display and associated details.
     *
     * @param newPhoto The new photo to be set as selected.
     */
    private void setSelectedPhoto(Photo newPhoto) {
        try {
            if(newPhoto == null){
                selectedPhoto = null;
                //I really don't know why I had to go through this filepath conversion
                //nonsense to give image a path it likes.
                File blankImg = new File("data/gui/blank.jpg");
                String currentRunDirectory = System.getProperty("user.dir");
                photoDisplay.setImage(new Image(blankImg.toURI().toURL().toExternalForm()));

                captionInput.setText("");
                tagList.setItems(null);
                detailTextArea.setText("");
                return;
            }

            photoDisplay.setImage(new Image(newPhoto.getFilepath().toURI().toURL().toExternalForm()));
            captionInput.setText(newPhoto.getCaption());
            detailTextArea.setText(newPhoto.getDetails());
            selectedPhoto = newPhoto;
            reloadTags();
        }catch(Exception ex){
        }
    }
    /**
     * Reloads the tags associated with the currently selected photo and updates the tag list view.
     */
    private void reloadTags(){
        ArrayList<Tag> tags = selectedPhoto.getTags();
        ObservableList<Text> tagsObs = FXCollections.observableArrayList();
        if(!tags.isEmpty()){
            for(Tag t: tags){
                Text temp = new Text();
                temp.setText(t.tagName() + " : " + t.tagValue());
                tagsObs.add(temp);
            }
        }
        tagList.setItems(tagsObs);
    }
    /**
     * Adds a new photo to the album. The user is prompted to choose a file, and if a file is selected,
     * it is added to the album and the album view is updated.
     *
     * @param e The event that triggered the action.
     */
    public void addPhoto(ActionEvent e){
        File selected = chooser.showOpenDialog(new Stage());

        if (selected == null)
            return;

        Photo photo = Photos.driver.hasFilepath(selected.toString());

        if(photo == null) {
            photo = new Photo(selected);
            Photos.driver.addFilepath(photo.getFilepath().toString(), photo);
        }
        beingDisplayed.addPhoto(photo);

        //reloadPhotos();
        HBox toAdd = makeHBox(photo);
        hboxes.add(toAdd);

        imageListview.getSelectionModel().select(hboxes.indexOf(toAdd));
    }
    /**
     * Creates an HBox containing the photo's image view and details such as caption and date.
     * This HBox is used to display the photo in the list view.
     *
     * @param ph The photo to create an HBox for.
     * @return The created HBox with the photo's details.
     */
    private HBox makeHBox(Photo ph) {
        try{
        ImageView image = new ImageView();
        image.setImage(new Image(ph.getFilepath().toURI().toURL().toExternalForm()));
        image.setPreserveRatio(true);
        image.fitWidthProperty().bind(imageListview.widthProperty().divide(3)); // assuming you have 3 columns

        HBox entry = new HBox();
        Text caption = new Text();
        Text date = new Text();

        caption.setText(ph.getCaption() + " | ");

        SimpleDateFormat df = new SimpleDateFormat("MM-dd-YYYY");
        String dateFormatted = df.format(ph.getDate().getTime());

        date.setText(dateFormatted);

        entry.getChildren().addAll(image,caption,date);
        entry.setUserData(ph);

        return entry;
        }
        catch(Exception ex){}
        return null;
    }
    /**
     * Reloads all the photos from the currently being displayed album into the list view.
     *
     */
    private void reloadPhotos(){
        imageList.clear();
        hboxes.clear();
        setSelectedPhoto(null);
        for(Photo ph : beingDisplayed.getPhotos()){
            ImageView image = new ImageView();

            try {
                image.setImage(new Image(ph.getFilepath().toURI().toURL().toExternalForm()));
                image.setPreserveRatio(true);
                image.fitWidthProperty().bind(imageListview.widthProperty().divide(3)); // assuming you have 3 columns
            }catch(MalformedURLException ex){}
            HBox entry = makeHBox(ph);
            imageList.add(image);
            hboxes.add(entry);
        }

        imageListview.setItems(hboxes);

    }
    /**
     * Removes the currently selected photo from the album and updates the list view.
     *
     * @param e The event that triggered the action.
     */
    public void removePhoto(ActionEvent e){
        if(selectedPhoto == null){
            showAlert("Invalid Selection - ERROR", "There is no photo selected", Alert.AlertType.ERROR);
            return;
        }
        beingDisplayed.removePhoto(selectedPhoto);
        reloadPhotos();

        if(imageList.size() > 0){
            imageListview.getSelectionModel().select(0);
        }
    }
    /**
     * Adds a caption to the selected photo based on the user input from the caption text field.
     *
     * @param e The event that triggered the action.
     */
    public void addCaption(ActionEvent e){
        if(selectedPhoto == null){
            showAlert("Invalid Selection - ERROR", "There is no photo selected", Alert.AlertType.ERROR);
            return;
        }
        Text temp = (Text) imageListview.getSelectionModel().getSelectedItem().getChildren().get(1);
        temp.setText(captionInput.getText() + " | ");

        Photo ph = selectedPhoto;
        ph.setCaption(captionInput.getText());
    }
    /**
     * Moves the selected photo to another album. If the copy operation is successful,
     * the photo is then removed from the current album.
     *
     * @param e The event that triggered the action.
     */
    public void movePhoto(ActionEvent e) {
        if(copyPhoto(null))
            removePhoto(null);
    }
    /**
     * Copies the currently selected photo to another album chosen by the user.
     * If the photo already exists in the target album, an error is displayed and no action is taken.
     *
     * @param e The event that triggered the action.
     * @return True if the photo was successfully copied, false otherwise.
     */
    public Boolean copyPhoto(ActionEvent e){
        if(selectedPhoto == null){
            showAlert("Invalid Selection - ERROR", "There is no photo selected", Alert.AlertType.ERROR);
            return false;
        }

        ArrayList<String> userAlbums = (ArrayList<String>) Photos.driver.getCurrentUser().getAlbumNameList().clone();
        userAlbums.remove(beingDisplayed.getAlbumName());

        if(userAlbums.size() < 1){
            showAlert("Invalid List - ERROR", "You do not have enough albums to move or copy this photo", Alert.AlertType.ERROR);
            return false;
        }

        ChoiceDialog d = new ChoiceDialog(userAlbums.get(0), userAlbums);
        d.setHeaderText("Which album is this photo being moved/copied to?");

        Optional<String> chosen = d.showAndWait();

        if(!chosen.isPresent()){
            showAlert("Invalid Selection - ERROR", "Invalid album selected", Alert.AlertType.ERROR);
            return false;
        }

        if(Photos.driver.getCurrentUser().doesAlbumHave(selectedPhoto,chosen.get())){
            showAlert("Invalid Selection - ERROR", "Album " + chosen.get() + " already has this photo", Alert.AlertType.ERROR);
            return false;
        }
        Photos.driver.getCurrentUser().addToAlbum(selectedPhoto, chosen.get());
        return true;
    }
    /**
     * Selects the next photo in the album's photo list view.
     *
     * @param e The event that triggered the action.
     */
    public void nextPhoto(ActionEvent e){
        if(imageListview.getSelectionModel().getSelectedIndices().size() == 0){
            showAlert("Invalid Selection - ERROR", "There is no photo selected", Alert.AlertType.ERROR);
            return;
        }

        int currInd = imageListview.getSelectionModel().getSelectedIndices().get(0);
        int nextInd;
        if (currInd >= imageList.size() - 1)
            nextInd = 0;
        else
            nextInd = currInd+1;

        imageListview.getSelectionModel().select(nextInd);
    }
    /**
     * Selects the previous photo in the album's photo list view.
     *
     * @param e The event that triggered the action.
     */
    public void previousPhoto(ActionEvent e){
        if(imageListview.getSelectionModel().getSelectedIndices().size() == 0){
            showAlert("Invalid Selection - ERROR", "There is no photo selected", Alert.AlertType.ERROR);
            return;
        }
        int currInd = imageListview.getSelectionModel().getSelectedIndices().get(0);
        int nextInd;
        if (currInd <= 0)
            nextInd = imageList.size()-1;
        else
            nextInd = currInd-1;

        imageListview.getSelectionModel().select(nextInd);
    }

    /**
     * Logs out the current user and returns to the login view.
     *
     * @param e The event that triggered the action.
     */
    public void logOut(ActionEvent e){
        logMeOut(e);
    }
    /**
     * Initiates a photo search within the current album.
     *
     * @param e The event that triggered the action.
     */
    public void search(ActionEvent e)  {
        popupAndWait("/Photos71/src/gui/fxml/SearchPage.fxml", beingDisplayed.getAlbumName(),"Photo Search");
    }
    /**
     * Returns to the previous view, typically the non-admin user's album list view.
     *
     * @param e The event that triggered the action.
     */
    public void goBack(ActionEvent e){
        Photos.driver.getCurrentUser().lookAt("");
        switchScene("/Photos71/src/gui/fxml/NonAdminPage.fxml", e.getSource());
    }
    /**
     * Adds the input tag's key to a user-specific list of preset keys to quickly add later.
     *
     * @param e The event that triggered the action.
     */
    public void addKeyToPreset(ActionEvent e){
        if(tag1.getText() == null || tag1.getText().equals("")){
            showAlert("Invalid Input - ERROR", "There is no text in the Tag Key text field", Alert.AlertType.ERROR);
            return;
        }

        Optional<ButtonType> confirm = new Alert(Alert.AlertType.CONFIRMATION, "Add key " + tag1.getText() + " to Preset Keys List?", ButtonType.YES, ButtonType.CANCEL)
                .showAndWait();
        if (confirm.isPresent() && confirm.get() == ButtonType.YES) {
            Photos.driver.getCurrentUser().presetKeyList.add(tag1.getText());
        }
    }
    /**
     * Prompts the user with a Choice Dialog to determine which of their preset keys they would like to add to
     * the currently selected photo's key input.
     *
     * @param e The event that triggered the action.
     */
    public void addKeyFromPreset(ActionEvent e){
        ChoiceDialog d = new ChoiceDialog(Photos.driver.getCurrentUser().presetKeyList.get(0), Photos.driver.getCurrentUser().presetKeyList);
        d.setHeaderText("Which predefined key would you like to add to this photo?");

        Optional<String> chosen = d.showAndWait();

        if(!chosen.isPresent()){
            return;
        }

        tag1.setText(chosen.get());
    }

    /**
     * Displays an alert dialog with the specified title and content.
     *
     * @param title   The title of the alert.
     * @param content The content of the alert.
     * @param type    The type of alert to display (e.g., ERROR).
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
