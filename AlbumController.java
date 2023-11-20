package photoalbum;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

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
    Button displayImage;
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

    public void initialize() throws MalformedURLException{
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

    public void addTag(ActionEvent e){
        if(tag1.getText().equals("") || tag1val.getText().equals("")){
            showAlert("Invalid Input - ERROR", "Either the new tag's key or value is empty", Alert.AlertType.ERROR);
            return;
        }
        selectedPhoto.addTag(tag1.getText(), tag1val.getText());
    }

    public void removeTag(ActionEvent e){
        if(tagList.getSelectionModel().getSelectedItem() == null){
            showAlert("Invalid Selection - ERROR", "There is no tag selected in the list", Alert.AlertType.ERROR);
            return;
        }
        String tag[] = tagList.getSelectionModel().getSelectedItem().getText().split(" : ");
        selectedPhoto.removeTag(new Tag(tag[0], tag[1]));
    }

    public void moveUp(ActionEvent e) throws MalformedURLException{
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
        try {Persistence.save(Photos.driver);}
            catch (IOException ex) {}
    }

    public void moveDown(ActionEvent e) throws MalformedURLException{
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

    private void setSelectedPhoto(Photo newPhoto) {
        try {
            if(newPhoto == null){
                //I really don't know why I had to go through this filepath conversion
                //nonsense to give image a path it likes.
                File blankImg = new File("data/gui/blank.jpg");
                String currentRunDirectory = System.getProperty("user.dir");
                photoDisplay.setImage(new Image(blankImg.toURI().toURL().toExternalForm()));

                captionInput.setText("");
                tagList.setItems(null);
                detailTextArea.setText("");
                selectedPhoto = null;
                return;
            }

            photoDisplay.setImage(new Image(newPhoto.getFilepath().toURI().toURL().toExternalForm()));
            captionInput.setText(newPhoto.getCaption());
            ArrayList<Tag> tags = newPhoto.getTags();
            ObservableList<Text> tagsObs = FXCollections.observableArrayList();
            if(!tags.isEmpty()){
                for(Tag t: tags){
                    Text temp = new Text();
                    temp.setText(t.tagName() + " : " + t.tagValue());
                    tagsObs.add(temp);
                }
            }
            tagList.setItems(tagsObs);
            detailTextArea.setText(newPhoto.getDetails());
            selectedPhoto = newPhoto;
        }catch(Exception ex){
        }
    }

    public void addPhoto(ActionEvent e) throws  IOException, MalformedURLException{
        File selected = chooser.showOpenDialog(new Stage());

        if (selected == null)
            return;

        Photo photo = new Photo(selected);

        beingDisplayed.addPhoto(photo);

        //reloadPhotos();
        HBox toAdd = makeHBox(photo);
        hboxes.add(toAdd);

        imageListview.getSelectionModel().select(hboxes.indexOf(toAdd));
    }

    private HBox makeHBox(Photo ph) {
        try{
        ImageView image = new ImageView();
        image.setImage(new Image(ph.getFilepath().toURI().toURL().toExternalForm()));
        image.setPreserveRatio(true);
        image.fitWidthProperty().bind(imageListview.widthProperty().divide(3)); // assuming you have 3 columns

        HBox entry = new HBox();
        Text caption = new Text();
        Text date = new Text();

        caption.setText(ph.getCaption());

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

    private void reloadPhotos() throws MalformedURLException {
        imageList.clear();
        hboxes.clear();
        setSelectedPhoto(null);
        for(Photo ph : beingDisplayed.getPhotos()){
            ImageView image = new ImageView();

            image.setImage(new Image(ph.getFilepath().toURI().toURL().toExternalForm()));
            image.setPreserveRatio(true);
            image.fitWidthProperty().bind(imageListview.widthProperty().divide(3)); // assuming you have 3 columns

            HBox entry = makeHBox(ph);
            imageList.add(image);
            hboxes.add(entry);
        }

        imageListview.setItems(hboxes);

    }

    public void removePhoto(ActionEvent e) throws MalformedURLException{
        if(selectedPhoto == null)
            return;
        beingDisplayed.removePhoto(selectedPhoto);
        reloadPhotos();

        if(imageList.size() > 0){
            imageListview.getSelectionModel().select(0);
        }
    }

    public void addCaption(ActionEvent e) throws MalformedURLException{
        if(selectedPhoto == null)
            return;
        Text temp = (Text) imageListview.getSelectionModel().getSelectedItem().getChildren().get(1);
        temp.setText(captionInput.getText());

        Photo ph = selectedPhoto;
        ph.setCaption(captionInput.getText());
    }

    public void movePhoto(ActionEvent e) throws MalformedURLException{
        if(copyPhoto(null))
            removePhoto(null);
    }

    public Boolean copyPhoto(ActionEvent e){
        if(selectedPhoto == null)
            return false;
        ArrayList<String> userAlbums = (ArrayList<String>) Photos.driver.getCurrentUser().getAlbumNameList().clone();
        userAlbums.remove(beingDisplayed.getAlbumName());

        ChoiceDialog d = new ChoiceDialog(userAlbums.get(0), userAlbums);
        d.setHeaderText("Which album is this photo being moved/copied to?");

        if(userAlbums.size() < 1){
            showAlert("Invalid List - ERROR", "You do not have enough albums to move or copy this photo", Alert.AlertType.ERROR);
            return false;
        }

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

    public void nextPhoto(ActionEvent e){
        if(imageListview.getSelectionModel().getSelectedIndices().size() == 0)
            return;

        int currInd = imageListview.getSelectionModel().getSelectedIndices().get(0);
        int nextInd;
        if (currInd >= imageList.size() - 1)
            nextInd = 0;
        else
            nextInd = currInd+1;

        imageListview.getSelectionModel().select(nextInd);
    }

    public void previousPhoto(ActionEvent e){
        if(imageListview.getSelectionModel().getSelectedIndices().size() == 0)
            return;
        int currInd = imageListview.getSelectionModel().getSelectedIndices().get(0);
        int nextInd;
        if (currInd <= 0)
            nextInd = imageList.size()-1;
        else
            nextInd = currInd-1;

        imageListview.getSelectionModel().select(nextInd);
    }

    public void logOut(ActionEvent e) throws IOException{
        logMeOut(e);
    }

    public void search(ActionEvent e)  {
        popupAndWait("SearchPage.fxml",e,beingDisplayed.getAlbumName(),"Photo Search");
    }

    public void goBack(ActionEvent e) throws IOException{
        Photos.driver.getCurrentUser().lookAt("");
        switchScene("/photoalbum/NonAdminPage.fxml", e);
    }

    public void displayPhoto(ActionEvent e) throws IOException {
        if(selectedPhoto == null)
            return;
        Desktop desktop = Desktop.getDesktop();
        desktop.open(selectedPhoto.getFilepath());
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
