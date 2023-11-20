/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package photoalbum.model.accounts;

import photoalbum.model.data.Album;
import photoalbum.model.data.Photo;
import photoalbum.model.data.Tag;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Represents a user in the photo album application with functionality to manage personal albums,
 * search for photos by tags and date range, and maintain a viewable album.
 */
public class User implements Serializable {

    private ArrayList<Album> albums; // The albums belonging to the user

    private String username; // The username of the user

    private Album lookingAt = null; // The currently viewed album

    public ArrayList<String> albumNames = new ArrayList<String>(); // // List of album names for easy access

    public static final long serialVersionUID = 1L; // Serialization identifier
    public static final String storeDir = "info"; // Directory for storing user information
    public static final String storeFile = "info.dat"; // Data file for storing user information

    static Comparator<Album> alphabetical = (arg0, arg1) -> arg0.getAlbumName().compareTo(arg1.getAlbumName());


    static Comparator<Album> byEarliestDate = Comparator
            .comparing(Album::getEarliestPhoto, Comparator.nullsFirst(Calendar::compareTo));

    /**
     * Searches for photos across albums that match any of the given tags.
     *
     * @param searchTags The list of tags to match.
     * @param albumName  The name of the album to restrict the search to, or "*" for all albums.
     * @return An ArrayList of photos that match any of the tags.
     */
    public ArrayList<Photo> orTagSearch(ArrayList<Tag> searchTags, String albumName) {
        Album searched = findAlbumByName(albumName);
        ArrayList<Album> albumSet;
        if(searched == null){
            albumSet = albums;
        }else {
            albumSet = new ArrayList<>();
            albumSet.add(searched);
        }

        return albumSet.stream()
                .flatMap(a -> a.getPhotos().stream())
                .distinct()
                .filter(picture -> picture.getTags().stream()
                        .anyMatch(photoTag -> searchTags.stream()
                                .anyMatch(searchTag -> photoTag.tagName().equalsIgnoreCase(searchTag.tagName()) &&
                                        photoTag.tagValue().equalsIgnoreCase(searchTag.tagValue()))))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Searches for photos across albums that match all of the given tags.
     *
     * @param searchTags The list of tags to match.
     * @param albumName  The name of the album to restrict the search to, or "*" for all albums.
     * @return An ArrayList of photos that match all of the tags.
     */
    public ArrayList<Photo> andTagSearch(ArrayList<Tag> searchTags, String albumName) {
        Album searched = findAlbumByName(albumName);
        ArrayList<Album> albumSet;
        if(searched == null){
            albumSet = albums;
        }else {
            albumSet = new ArrayList<>();
            albumSet.add(searched);
        }

        return albumSet.stream()
                .flatMap(album -> album.getPhotos().stream())
                .filter(picture -> searchTags.stream().allMatch(picture::hasTag))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    /**
     * Retrieves photos within a specified date range across all albums or a single album.
     *
     * @param date1     The starting date of the range.
     * @param date2     The ending date of the range.
     * @param albumName The name of the album to restrict the search to, or "*" for all albums.
     * @return An ArrayList of photos within the specified date range.
     */
    public ArrayList<Photo> getPhotosInRange(Calendar date1, Calendar date2, String albumName) {
        Album searched = findAlbumByName(albumName);
        ArrayList<Album> albumSet;
        if(searched == null){
            albumSet = albums;
        }else {
            albumSet = new ArrayList<>();
            albumSet.add(searched);
        }

        Set<Photo> uniquePhotos = albumSet.stream()
                .flatMap(album -> album.getPhotos().stream())
                .filter(picture -> {
                    Calendar testDate = picture.getDate();
                    return !testDate.before(date1) && !testDate.after(date2);
                })
                .collect(Collectors.toSet());

        return new ArrayList<>(uniquePhotos);
    }
    /**
     * Retrieves a list of album names for the user.
     *
     * @return An ArrayList of album names.
     */
    public ArrayList<String> getAlbumNameList() {
        return albums.stream()
                .map(Album::getAlbumName)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    /**
     * Gets the username of the user.
     *
     * @return The username.
     */
    public String getName(){
        return username;
    }
    /**
     * Creates a new album for the user with the given name.
     *
     * @param name The name of the new album.
     * @return The newly created Album object.
     */
    public Album makeAlbum(String name){
        Album temp = new Album(name);
        albums.add(temp);
        albumNames.add(name);
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}

        return temp;
    }

    /**
     * Renames an existing album from its original name to a new name.
     *
     * @param original The original album name.
     * @param newName  The new album name.
     */
    public void renameAlbum(String original, String newName) {
        Album src = findAlbumByName(original);
        if(src == null)
            return;
        src.rename(newName);
        albumNames.remove(original);
        albumNames.add(newName);
        //try {Persistence.save(Photos.driver);}
        //   catch (IOException e) {System.out.println(e);}
    }
    /**
     * Deletes an album with the specified name.
     *
     * @param album The name of the album to be deleted.
     */
    public void deleteAlbum(String album){
        Album src = findAlbumByName(album);

        if(src == null)
            return;

        albumNames.remove(album);
        albums.remove(src);

        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }
    /**
     * Adds a photo to an album specified by name.
     *
     * @param in        The Photo to add.
     * @param albumName The name of the album to add the photo to.
     */
    public void addToAlbum(Photo in, String albumName){
        if(in == null)
            return;
        Album src = findAlbumByName(albumName);

        if(src == null)
            return;

        src.addPhoto(in);

        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }
    /**
     * Checks if an album contains a specific photo.
     *
     * @param in        The Photo to check.
     * @param albumName The name of the album to check for the photo.
     * @return true if the album contains the photo, false otherwise.
     */
    public Boolean doesAlbumHave(Photo in, String albumName){
        Album src = findAlbumByName(albumName);

        if(src == null)
            return true; //If this is ever null, we'll return true which will cause an alert in AlbumController

        return src.getPhotos().contains(in);
    }
    /**
     * Finds an album by its name.
     *
     * @param name The name of the album to find.
     * @return The Album object if found, null otherwise.
     */
    private Album findAlbumByName(String name){
        for(Album a : albums){
            if (a.getAlbumName() == name){
                return a;
            }
        }
        return null;
    }
    /**
     * Retrieves the Album object for a given name.
     *
     * @param name The name of the album to retrieve.
     * @return The Album object if it exists, null otherwise.
     */
    public Album getAlbum(String name){
        if(albumNames.indexOf(name) == -1)
            return null;
        return albums.get(albumNames.indexOf(name));
    }
    /**
     * Sets the album the user is currently looking at.
     *
     * @param albumName The name of the album to look at.
     * @return true if the album exists and is set to be viewed, false otherwise.
     */
    public boolean lookAt(String albumName){
        lookingAt = findAlbumByName(albumName);
        return lookingAt != null;
    }
    /**
     * Gets the album the user is currently looking at.
     *
     * @return The Album object the user is currently viewing, or null if none.
     */
    public Album getLookAt(){
        return lookingAt;
    }
    /**
     * Creates a user with a specified name.
     *
     * @param name The name of the user.
     */
    public User(String name){
        username = name;
        albums = new ArrayList<Album>();
    }
}