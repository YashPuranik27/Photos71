/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package photoalbum.model.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
/**
 * Represents an album in the photo album application, holding a collection of photos and related details.
 */
public class Album implements Serializable {

    public static final long serialVersionUID = 1L; // Serialization identifier

    private ArrayList<Photo> photos = new ArrayList<>(); // The photos contained in the album
    private String albumName = "Untitled"; // The name of the album
    /**
     * Constructs an Album with the specified name.
     *
     * @param name The name for the new album.
     */
    public Album(String name){
        albumName = name;
    }
    /**
     * Retrieves the name of the album.
     *
     * @return The album's name.
     */
    public String getAlbumName(){
        return albumName;
    }
    /**
     * Retrieves the list of photos on the album.
     *
     * @return The ArrayList of photos.
     */
    public ArrayList<Photo> getPhotos(){
        return photos;
    }
    /**
     * Renames the album with the specified name.
     *
     * @param name The new name for the album.
     */
    public void rename(String name){
        albumName = name;
    }
    /**
     * Gets the date of the earliest photo on the album.
     *
     * @return The Calendar date of the earliest photo, or null if the album is empty.
     */
    public Calendar getEarliestPhoto(){
        Photo smallest = photos.stream().min(Comparator.comparing(Photo::getDate)).get();
        return smallest.getDate();
    }
    /**
     * Gets the date of the latest photo on the album.
     *
     * @return The Calendar date of the latest photo, or null if the album is empty.
     */
    public Calendar getLatest(){
        Photo smallest = photos.stream().max(Comparator.comparing(Photo::getDate)).get();
        return smallest.getDate();
    }
    /**
     * Adds a photo to the album.
     *
     * @param in The Photo to be added to the album.
     */
    public void addPhoto(Photo in){
        photos.add(in);
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }
    /**
     * Removes a photo from the album.
     *
     * @param in The Photo to be removed from the album.
     */
    public void removePhoto(Photo in){
        photos.remove(in);
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }
}
