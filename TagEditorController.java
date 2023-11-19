package photoalbum;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.converter.DefaultStringConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

public class TagEditorController {
    @FXML
    public TableView tagTable;

    @FXML
    public TitledPane titledPane;

    @FXML
    public ImageView imagePreview;

    @FXML
    public Button saveButton;

    private ObservableList<String> keyNames = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> valueNames = FXCollections.observableArrayList();

    private Photo displayedPhoto = null;

    private TableColumn keyCol = new TableColumn();
    private ArrayList<TableColumn<Tag, String>> valueCols = new ArrayList<>();

    public void initialize(){
        Platform.runLater(() -> {
            displayedPhoto = (Photo) titledPane.getUserData();
            loadTags();
            try {
                imagePreview.setImage(new Image(displayedPhoto.getFilepath().toURI().toURL().toExternalForm()));
            }catch(Exception ex){}

            titledPane.setText("Tag Editor: " + displayedPhoto.getCaption() + " by " + Photos.driver.getCurrentUser().getName());
        });
    }

    public void clearSelectedRow(ActionEvent e){
        ObservableList<String> rowSelected = (ObservableList<String>) tagTable.getSelectionModel().getSelectedItem();
        if(rowSelected == null)
            return;
        tagTable.getItems().remove(rowSelected);
        ObservableList<String> emptyRow = FXCollections.observableArrayList(Collections.nCopies(64, ""));
        tagTable.getItems().add(emptyRow);

        tagTable.refresh();
    }

    public void saveChanges(ActionEvent e) {
        if(displayedPhoto == null)
            return;

        displayedPhoto.clearTags();


        //Read table into Tag array for Photo
        for(int i = 0; i < 64; i++){
            String tagName = "", tagConcat = "";

            ObservableList<String> row = (ObservableList<String>) tagTable.getItems().get(i);

            tagName = row.get(0);
            if(row.size() == 1)
                continue;

            tagConcat = String.join("; ", row.subList(1,row.size()));

            tagConcat = tagConcat.replaceAll("; .( ;)+", ";");
            tagConcat = tagConcat.replaceAll("^; ", "");
            tagConcat = tagConcat.replaceAll("^;", "");

            if (tagName.equals("") | tagName == null | tagConcat.equals("")
                    | tagConcat.replace(";","") == null)
                continue;


            displayedPhoto.addTag(tagName, tagConcat);
        }

        loadTags();
    }

    private void loadTags(){
        if(displayedPhoto == null)
            return;

        tagTable.getItems().clear();
        tagTable.getColumns().clear();
        keyNames.clear();
        valueNames.clear();

        ArrayList<Tag> photoTags = displayedPhoto.getTags();

        int maxSize = 0;

        for(Tag t : photoTags){
            ObservableList<String> values = FXCollections.observableArrayList();
            values.addAll(t.tagValue().split("; "));

            keyNames.add(t.tagName());

            valueNames.add(values);
            maxSize = Math.min(64, Math.max(maxSize,values.size()));
        }

        tagTable.getColumns().clear();

        TableColumn<ObservableList<String>, String> keyCol = new TableColumn<>("Key");
        keyCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));

        keyCol = columnConfigurer(keyCol);
        tagTable.getColumns().add(keyCol);

        for(int i = 0; i < 64; i++) {
            final int index = i+1;
            TableColumn<ObservableList<String>, String> valCol = new TableColumn<>("Value " + (i + 1));
            valCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().size() > index ? param.getValue().get(index) : ""));

            valCol = columnConfigurer(valCol);

            tagTable.getColumns().add(valCol);
        }

        for (int i = 0; i < 64; i++) {
            if(keyNames.size() <= i | valueNames.size() <= i){

                ObservableList<String> emptyRow = FXCollections.observableArrayList(Collections.nCopies(64, ""));
                tagTable.getItems().add(emptyRow);
                continue;
            }

            String name = keyNames.get(i);
            ObservableList<String> values = valueNames.get(i);
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(name);
            row.addAll(values);
            tagTable.getItems().add(row);
        }

        tagTable.refresh();
    }

    private TableColumn<ObservableList<String>, String> columnConfigurer(TableColumn<ObservableList<String>, String> in){
        in.setCellFactory(column -> {

            TextFieldTableCell<ObservableList<String>, String> cell = new TextFieldTableCell<>(new DefaultStringConverter());

            cell.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (isNowFocused) {
                    Platform.runLater(cell::startEdit);
                } else {
                    cell.commitEdit(cell.getConverter().fromString(((TextField) cell.getGraphic()).getText()));
                }
            });

            return cell;

        });

        in.setOnEditCommit(event -> {

            ObservableList<String> row = event.getRowValue();
            int index = event.getTablePosition().getColumn();
            row.set(index, event.getNewValue());

        });

        return in;
    }

    public TagEditorController(){
    }

}
