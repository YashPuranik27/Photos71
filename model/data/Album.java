package photoalbum.model.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

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

    public Calendar getLatest(){
        Photo smallest = photos.stream().max(Comparator.comparing(Photo::getDate)).get();
        return smallest.getDate();
    }

    public void addPhoto(Photo in){
        photos.add(in);
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }

    public void removePhoto(Photo in){
        photos.remove(in);
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }
}
