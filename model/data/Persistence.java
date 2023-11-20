package photoalbum.model.data;

import photoalbum.Photos;
import photoalbum.model.accounts.Admin;
import photoalbum.model.accounts.User;

import java.io.*;
import java.util.ArrayList;

public class Persistence implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String STORE_DIR = "data/savedData";
    public static final String STORE_FILE = "info.dat";

    public Admin admin = new Admin();
    private boolean loggedIn;
    private User currentUser;
    private Album currentAlbum;

    public Album getCurrentAlbum() {
        return currentAlbum;
    }

    public void setCurrentAlbum(Album album) {
        currentAlbum = album;
    }

    public boolean checkUser(String username) {
        int res = admin.userExists(username);
        if (res >= 0) {
            setCurrentUser(admin.getUsers().get(res));
            loggedIn = true;
        }
        return res >= 0;
    }

    public int getUserIndex() {
        return admin.getUsers().indexOf(Photos.driver.getCurrentUser());
    }

    public User getUser(String username) {
        int index = admin.userExists(username);
        return index >= 0 ? admin.getUsers().get(index) : null;
    }

    public void setUsers(ArrayList<User> users) {
        admin.setUsers(users);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User curr) {
        currentUser = curr;
    }

    public static void save(Persistence pdApp) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORE_DIR + File.separator + STORE_FILE))) {
            oos.writeObject(pdApp);
        }
    }

    public static Persistence load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORE_DIR + File.separator + STORE_FILE))) {
            return (Persistence) ois.readObject();
        }
    }
}
