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


}