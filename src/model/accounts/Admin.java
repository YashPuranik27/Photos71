/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package Photos71.src.model.accounts;

import Photos71.src.photos.Photos;
import Photos71.src.model.data.Album;
import Photos71.src.model.data.Photo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Represents the administrator of the photo album application with the ability to manage users and regenerate
 * the stock album with default photos.
 *
 * @author Yash Puranik, Joseph Arrigo
 */
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ADMIN_USERNAME = "admin"; // used by LoginController
    private List<User> users; // List of users managed by the admin
    private String username; // Username of the admin: "admin"
    /**
     * Constructs an Admin instance with a predefined list of users.
     *
     * @param users List of existing users to be managed by the admin.
     */
    public Admin(List<User> users) {
        this.users = users;
        this.username = DEFAULT_ADMIN_USERNAME;
    }
    /**
     * Constructs an Admin instance with an empty list of users.
     */
    public Admin() {
        this(new ArrayList<>());
    }
    /**
     * Sets the list of users managed by the admin.
     *
     * @param users The new list of users.
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }
    /**
     * Adds a user to the admin's list of users.
     *
     * @param userName The username of the new user.
     */
    public void addUser(String userName) {
        users.add(new User(userName));
    }
    /**
     * Gets a copy of the list of users managed by the admin to prevent external modification.
     *
     * @return A new list containing the users.
     */
    public List<User> getUsers() {
        return new ArrayList<>(users); // This should return a copy to avoid external modification
    }
    /**
     * Gets a list of usernames of all users managed by the admin.
     *
     * @return A list of usernames.
     */
    public List<String> getUsernameList() {
        return users.stream().map(User::getName).collect(Collectors.toList());
    }
    /**
     * Checks if a user exists in the admin's list of users based on the username.
     *
     * @param userName The username to check.
     * @return The index of the user in the list, or -1 if the user does not exist.
     */
    public int userExists(String userName) {
        return users.stream().map(User::getName).collect(Collectors.toList()).indexOf(userName);
    }
    /**
     * Deletes a user from the admin's list based on the username.
     *
     * @param userName The username of the user to delete.
     */
    public void deleteUser(String userName) {
        users.removeIf(user -> user.getName().equals(userName));
    }
    /**
     * Regenerates the stock album with photos from the "data/stock" directory,
     * adding new photos that do not already exist in the album.
     */
    public void regenerateStock(){
        User stock;
        if(!Photos.driver.checkUser("stock")){
            stock = new User("stock");
            users.add(stock);
        }
        stock = Photos.driver.getUser("stock");

        Album stockAlbum;
        if(stock.getAlbum("stock") == null)
            stock.makeAlbum("stock");

        stockAlbum = stock.getAlbum("stock");
        File directory = new File("data/stock");
        for (File file : directory.listFiles()) {
            if (file.getName().split("\\.")[1].equalsIgnoreCase("bmp")
                    | file.getName().split("\\.")[1].equalsIgnoreCase("jpg")
                    | file.getName().split("\\.")[1].equalsIgnoreCase("png")
                    | file.getName().split("\\.")[1].equalsIgnoreCase("gif")) {

                if(stockAlbum.getPhotos().stream().anyMatch((ph) -> {
                    return ph.getFilepath().equals(file);
                }))
                    continue;

                Photo newPhoto = new Photo(file);
                Photos.driver.addFilepath(file.getAbsoluteFile().toString(), newPhoto);
                stockAlbum.addPhoto(newPhoto);
            }
        }
    }
}
