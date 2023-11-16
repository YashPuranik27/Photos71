package photoalbum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private List<User> users;
    private String username;
    
    }