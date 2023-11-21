/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package Photos71.src.gui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import Photos71.src.model.data.Album;
import Photos71.src.model.data.Photo;
import Photos71.src.photos.Photos;
import Photos71.src.model.data.Tag;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Controller for the search functionality within the photo album application.
 * Allows for searching photos by date range or tags and saving search results as an album.
 *
 * @author Yash Puranik, Joseph Arrigo
 */
public class SearchController { // default constructor

    @FXML
    AnchorPane anchorPane;

    @FXML
    Button makeAlbum;

    @FXML
    Button tagSwitch;

    @FXML
    TextField beginningDateInput;
    @FXML
    TextField endDateInput;
    @FXML
    TextField albumNameInput;
    @FXML
    TextField tag1;
    @FXML
    TextField tag1val;
    @FXML
    TextField tag2;
    @FXML
    TextField tag2val;

    @FXML
    TitledPane titledPane;

    @FXML
    ListView<HBox> searchResults;

    private String searchedAlbum="*";

    private ArrayList<Photo> resultPhotos = new ArrayList<>();

    private Boolean andSearch = false;

    private ObservableList<HBox> hboxes = FXCollections.observableArrayList();
    /**
     * Saves the current search results as a new album with the given name.
     *
     * @param e The action event that triggered the save.
     */
    public void saveResultsAsAlbum(ActionEvent e){
        if(albumNameInput.getText().trim().equals("")){
            showAlert("Invalid Input - ERROR", "Album name must be at least 1 character", Alert.AlertType.ERROR);
            return;
        }

        if(Photos.driver.getCurrentUser().albumNames.contains(albumNameInput.getText().trim())){
            showAlert("Invalid Input - ERROR", "Album name is already in use", Alert.AlertType.ERROR);
            return;
        }
        Album newAl = Photos.driver.getCurrentUser().makeAlbum(albumNameInput.getText().trim());
        newAl.getPhotos().addAll(resultPhotos);
    }

    /**
     * Updates the list of photos displayed in the search results after deduplication.
     *
     */
    private void updatePhotos(){
        Set<Photo> uniquePhotos = new HashSet<>(resultPhotos);
        resultPhotos.clear();
        resultPhotos.addAll(uniquePhotos);

        reloadPhotos();
    }
    /**
     * Toggles the search mode between AND and OR for tag-based searches.
     *
     * @param e The action event that triggered the toggle.
     */
    public void tagSwitch(ActionEvent e){
        if(tagSwitch.getText().equals("    ⬤")){
            andSearch = true;
            tagSwitch.setText("⬤");
        }else {
            andSearch = false;
            tagSwitch.setText("    ⬤");
        }
    }
    /**
     * Performs a search for photos based on the specified tags.
     *
     */
    public void searchByTag() {
        if((tag1val.getText().equals("") && !tag1.getText().equals("")) |
                 (tag2val.getText().equals("") && !tag2.getText().equals(""))){
            showAlert("Invalid Input - ERROR", "There is a key without a value.", Alert.AlertType.ERROR);
            return;
        }

        if((!tag1val.getText().equals("") && tag1.getText().equals("")) |
                (!tag2val.getText().equals("") && tag2.getText().equals(""))){
            showAlert("Invalid Input - ERROR", "There is a value without a key.", Alert.AlertType.ERROR);
            return;
        }

        if(tag1.getText().equals("") && tag2.getText().equals("")){
            showAlert("Invalid Input - ERROR", "There are no tags entered.", Alert.AlertType.ERROR);
            return;
        }

        resultPhotos.clear();
        ArrayList<Tag> searchTags = new ArrayList<>();
        if(!tag1.getText().equals(""))
            searchTags.add(new Tag(tag1.getText().toLowerCase(), tag1val.getText().toLowerCase()));
        if(!tag2.getText().equals(""))
            searchTags.add(new Tag(tag2.getText().toLowerCase(), tag2val.getText().toLowerCase()));

        if(andSearch)
            resultPhotos.addAll(Photos.driver.getCurrentUser().andTagSearch(searchTags, searchedAlbum));
        else
            resultPhotos.addAll(Photos.driver.getCurrentUser().orTagSearch(searchTags, searchedAlbum));


        updatePhotos();
    }
    /**
     * Performs a search for photos within a specified date range.
     *
     */
    public void searchByDate() {
        resultPhotos.clear();
        String startInput = beginningDateInput.getText().replaceAll("(/|\\|-)", "");
        String endInput = endDateInput.getText().replaceAll("(/|\\|-)", "");
        if (startInput.equals(""))
            startInput = "01011970";
        if (endInput.equals(""))
            endInput = "01013000";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
            Date start = sdf.parse(startInput);
            Date end = sdf.parse(endInput);

            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();

            if(endCal.before(startCal)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input - ERROR");
                alert.setHeaderText(null);
                alert.setContentText("End date can not be before start date.");
                alert.showAndWait();
                return;
            }

            startCal.set(Calendar.MILLISECOND, 0);
            startCal.setTimeInMillis(start.getTime());
            endCal.set(Calendar.MILLISECOND, 0);
            endCal.setTimeInMillis(end.getTime());
            endCal.add(Calendar.DAY_OF_YEAR, 1);


            resultPhotos.addAll(Photos.driver.getCurrentUser().getPhotosInRange(startCal, endCal, searchedAlbum));
        } catch (ParseException ex) {
            showAlert("Invalid Input - ERROR", "One of the entered dates is invalid", Alert.AlertType.ERROR);
        }
        updatePhotos();
    }
    /**
     * Initializes the controller and sets up the search parameters based on the current user's context.
     */
    public void initialize(){
        Platform.runLater(() -> {
            if(Photos.driver.getCurrentUser().getLookAt() == null)
                searchedAlbum = "*";
            else
                searchedAlbum = Photos.driver.getCurrentUser().getLookAt().getAlbumName();
            titledPane.setText("Searching " + Photos.driver.getCurrentUser().getName() +"'s photos" + (searchedAlbum.equals("*") ? "" : " from album: " + searchedAlbum));
        });
    }
    /**
     * Creates an HBox layout containing the photo, caption, and date for display in the search results.
     *
     * @param ph The photo to be displayed.
     * @return An HBox containing the photo's details.
     */
    private HBox makeHBox(Photo ph) {
        try{
            ImageView image = new ImageView();
            image.setImage(new Image(ph.getFilepath().toURI().toURL().toExternalForm()));
            image.setPreserveRatio(true);
            image.fitWidthProperty().bind(searchResults.widthProperty().divide(3)); // assuming you have 3 columns

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
     * Reloads the photos to be displayed in the search results.
     *
     */
    private void reloadPhotos(){
        hboxes.clear();
        searchResults.getItems().clear();

        for(Photo ph : resultPhotos){
            hboxes.add(makeHBox(ph));
        }

        searchResults.setItems(hboxes);
    }
    /**
     * Displays an alert dialog with the given title and content.
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
