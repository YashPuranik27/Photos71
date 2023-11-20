/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package photoalbum.model.data;

import photoalbum.Photos;
import photoalbum.model.accounts.Admin;
import photoalbum.model.accounts.User;

import java.io.*;
import java.util.ArrayList;
/**
 * Handles the persistence layer of the photo album application, managing the saving and loading
 * of user and album data to and from the disk.
 */
public class Persistence implements Serializable { // default constructor

    private static final long serialVersionUID = 1L; // Serialization identifier
    public static final String STORE_DIR = "data/savedData"; // Directory for storing serialized data
    public static final String STORE_FILE = "info.dat"; // File name for storing serialized data

    public Admin admin = new Admin(); // Admin instance managing users
    private boolean loggedIn; // Flag to indicate if a user is logged in
    private User currentUser; // Reference to the currently logged-in user
    private Album currentAlbum; // Reference to the currently viewed album
    /**
     * Retrieves the currently viewed album.
     *
     * @return The current album.
     */
    public Album getCurrentAlbum() {
        return currentAlbum;
    }
    /**
     * Sets the currently viewed album.
     *
     * @param album The album to be set as the current album.
     */
    public void setCurrentAlbum(Album album) {
        currentAlbum = album;
    }
    /**
     * Checks if a user exists and sets them as the current user if they do.
     *
     * @param username The username to check.
     * @return True if the user exists, false otherwise.
     */
    public boolean checkUser(String username) {
        int res = admin.userExists(username);
        if (res >= 0) {
            setCurrentUser(admin.getUsers().get(res));
            loggedIn = true;
        }
        return res >= 0;
    }
    /**
     * Gets the index of the current user in the admin's user list.
     *
     * @return The index of the current user.
     */
    public int getUserIndex() {
        return admin.getUsers().indexOf(Photos.driver.getCurrentUser());
    }
    /**
     * Retrieves a User object by username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if the user exists, null otherwise.
     */
    public User getUser(String username) {
        int index = admin.userExists(username);
        return index >= 0 ? admin.getUsers().get(index) : null;
    }
    /**
     * Sets the list of users managed by the admin.
     *
     * @param users The new list of users.
     */
    public void setUsers(ArrayList<User> users) {
        admin.setUsers(users);
    }
    /**
     * Retrieves the currently logged-in user.
     *
     * @return The current user.
     */
    public User getCurrentUser() {
        return currentUser;
    }
    /**
     * Sets the currently logged-in user.
     *
     * @param curr The user to set as the current user.
     */
    public void setCurrentUser(User curr) {
        currentUser = curr;
    }
    /**
     * Saves the current state of the application to a file.
     *
     * @param pdApp The Persistence object containing the application's state.
     * @throws IOException If an I/O error occurs during saving.
     */
    public static void save(Persistence pdApp) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORE_DIR + File.separator + STORE_FILE))) {
            oos.writeObject(pdApp);
        }
    }
    /**
     * Loads the application's state from a file.
     *
     * @return The Persistence object representing the loaded application's state.
     * @throws IOException            If an I/O error occurs during loading.
     * @throws ClassNotFoundException If the class of a serialized object cannot be found.
     */
    public static Persistence load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORE_DIR + File.separator + STORE_FILE))) {
            return (Persistence) ois.readObject();
        }
    }
}
