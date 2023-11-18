package photoalbum;

import javafx.scene.image.Image;
import javafx.util.Pair;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class Album implements Serializable {

    public static final long serialVersionUID = 1L;

    private ArrayList<Photo> photos = new ArrayList<>();
    private String albumName = "Untitled";

    public Album(String name){
        albumName = name;
    }

    public String getAlbumName(){
        return albumName;
    }

    public ArrayList<Photo> getPhotos(){
        return photos;
    }

    public void rename(String name){
        albumName = name;
    }

    public Calendar getEarliestPhoto(){
        Photo smallest = photos.stream().min(Comparator.comparing(Photo::getDate)).get();
        return smallest.getDate();
    }

    public void addPhoto(Photo in){
        photos.add(in);
        try {Persistence.save(Photos.driver);}
            catch (IOException e) {System.out.println(e);}
    }

    public void removePhoto(Photo in){
        photos.remove(in);
        try {Persistence.save(Photos.driver);}
            catch (IOException e) {System.out.println(e);}
    }
}
