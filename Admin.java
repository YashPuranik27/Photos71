package photoalbum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ADMIN_USERNAME = "admin"; // used by LoginController
    private List<User> users;
    private String username;

    public Admin(List<User> users) {
        this.users = users;
        this.username = DEFAULT_ADMIN_USERNAME;
    }

    public Admin() {
        this(new ArrayList<>());
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(String userName) {
        users.add(new User(userName));
    }

    public List<User> getUsers() {
        return new ArrayList<>(users); // This should return a copy to avoid external modification
    }

    public List<String> getUsernameList() {
        return users.stream().map(User::getName).collect(Collectors.toList());
    }

    public int userExists(String userName) {
        return users.stream().map(User::getName).collect(Collectors.toList()).indexOf(userName);
    }

    public void deleteUser(String userName) {
        users.removeIf(user -> user.getName().equals(userName));
    }
}
