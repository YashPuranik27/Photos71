import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.*;


public class User implements Serializable {

    private ArrayList<Album> albums;

    private String username;

    public ArrayList<String> albumNames = new ArrayList<String>(); // List of album names

    public static final long serialVersionUID = 1L;
    public static final String storeDir = "info";
    public static final String storeFile = "info.dat";

    static Comparator<Album> alphabetical = (arg0, arg1) -> arg0.getAlbumName().compareTo(arg1.getAlbumName());


    static Comparator<Album> byEarliestDate = Comparator
            .comparing(Album::getEarliestPhoto, Comparator.nullsFirst(Date::compareTo));


    public ArrayList<Photo> orTagSearch(ArrayList<Tag> searchTags) {
        return albums.stream()
                .flatMap(a -> a.getPhotos().stream())
                .distinct()
                .filter(picture -> picture.getTags().stream()
                        .anyMatch(photoTag -> searchTags.stream()
                                .anyMatch(searchTag -> photoTag.tagName().equalsIgnoreCase(searchTag.tagName()) &&
                                        photoTag.tagValue().equalsIgnoreCase(searchTag.tagValue()))))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public ArrayList<Photo> andTagSearch(ArrayList<Tag> searchTags) {
        return albums.stream()
                .flatMap(album -> album.getPhotos().stream())
                .filter(picture -> searchTags.stream().allMatch(picture::hasTag))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Photo> getPhotosInRange(Date date1, Date date2) {
        Set<Photo> uniquePhotos = albums.stream()
                .flatMap(album -> album.getPhotos().stream())
                .filter(picture -> {
                    Date testDate = picture.getDate();
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
}