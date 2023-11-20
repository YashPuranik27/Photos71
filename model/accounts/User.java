package photoalbum.model.accounts;

import photoalbum.model.data.Album;
import photoalbum.model.data.Photo;
import photoalbum.model.data.Tag;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class User implements Serializable {

    private ArrayList<Album> albums;

    private String username;

    private Album lookingAt = null;

    public ArrayList<String> albumNames = new ArrayList<String>(); // List of album names

    public static final long serialVersionUID = 1L;
    public static final String storeDir = "info";
    public static final String storeFile = "info.dat";

    static Comparator<Album> alphabetical = (arg0, arg1) -> arg0.getAlbumName().compareTo(arg1.getAlbumName());


    static Comparator<Album> byEarliestDate = Comparator
            .comparing(Album::getEarliestPhoto, Comparator.nullsFirst(Calendar::compareTo));


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

    public ArrayList<String> getAlbumNameList() {
        return albums.stream()
                .map(Album::getAlbumName)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String getName(){
        return username;
    }

    public Album makeAlbum(String name){
        Album temp = new Album(name);
        albums.add(temp);
        albumNames.add(name);
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}

        return temp;
    }

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

    public void deleteAlbum(String album){
        Album src = findAlbumByName(album);

        if(src == null)
            return;

        albumNames.remove(album);
        albums.remove(src);

        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }

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

    public Boolean doesAlbumHave(Photo in, String albumName){
        Album src = findAlbumByName(albumName);

        if(src == null)
            return true; //If this is ever null, we'll return true which will cause an alert in AlbumController

        return src.getPhotos().contains(in);
    }

    private Album findAlbumByName(String name){
        for(Album a : albums){
            if (a.getAlbumName() == name){
                return a;
            }
        }
        return null;
    }

    public Album getAlbum(String name){
        if(albumNames.indexOf(name) == -1)
            return null;
        return albums.get(albumNames.indexOf(name));
    }

    public boolean lookAt(String albumName){
        lookingAt = findAlbumByName(albumName);
        return lookingAt != null;
    }

    public Album getLookAt(){
        return lookingAt;
    }

    public User(String name){
        username = name;
        albums = new ArrayList<Album>();
    }
}