/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package Photos71.src.model.data;

import java.io.File;
import java.io.Serializable;
import java.util.*;
/**
 * Represents a photograph in the photo album application, including its metadata such as tags and caption.
 *
 * @author Yash Puranik, Joseph Arrigo
 */
public class Photo implements Serializable {

    public static final long serialVersionUID = 1L; // Serialization identifier

    //Default date of 1/1/1970
    private Calendar dateModified = Calendar.getInstance(); // Date the photo was last modified
    private ArrayList<Tag> tags = new ArrayList<>(); // List of tags associated with the photo
    private String caption; // The caption of the photo
    private String details = ""; // Additional details about the photo

    private File file; // File reference to the photo
    /**
     * Gets the date the photo was last modified.
     *
     * @return The date of last modification.
     */
    public Calendar getDate(){
        return dateModified;
    }
    /**
     * Gets the list of tags associated with the photo.
     *
     * @return The list of tags.
     */
    public ArrayList<Tag> getTags(){
        return tags;
    }
    /**
     * Checks if the photo has a specific tag.
     *
     * @param in The tag to check for.
     * @return True if the tag is present, false otherwise.
     */
    public Boolean hasTag(Tag in){
        return tags.contains(in);
    }
    /**
     * Loads the image from a file and sets the caption.
     *
     * @param fileIn The file from which to load the photo.
     * @param capIn  The caption of the photo.
     */
    public void loadImage(File fileIn, String capIn){
        file = fileIn;
        dateModified.set(Calendar.MILLISECOND, 0);
        dateModified.setTimeInMillis(file.lastModified());
        caption = capIn;
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }
    /**
     * Gets the file path to the photo.
     *
     * @return The file path.
     */
    public File getFilepath(){
        return file;
    }
    /**
     * Gets the caption of the photo.
     *
     * @return The caption.
     */
    public String getCaption(){
        return caption;
    }
    /**
     * Sets the caption of the photo.
     *
     * @param capIn The new caption.
     */
    public void setCaption(String capIn){
        caption = capIn;
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }
    /**
     * Gets additional details about the photo.
     *
     * @return The details.
     */
    public String getDetails(){
        return details;
    }
    /**
     * Sets additional details about the photo.
     *
     * @param in The new details.
     */
    public void setDetails(String in){
        details = in;
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }

    /**
     * Adds a tag to the photo, if it does not already exist.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public void addTag(String name, String value){
        for(Tag t : tags){
            if(t.tagName() == name)
                return;
        }

        tags.add(new Tag(name, value));

        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }
    /**
     * Removes a specified tag from the photo.
     *
     * @param in The tag to be removed.
     */
    public void removeTag(Tag in){
        tags.remove(in);
        //try {Persistence.save(Photos.driver);}
        //   catch (IOException e) {System.out.println(e);}
    }
    /**
     * Clears all tags from the photo.
     */
    public void clearTags(){
        tags.clear();
    }
    /**
     * Constructs a Photo object from a file with an empty caption.
     *
     * @param fileIn The file of the photo.
     */
    public Photo(File fileIn){
            loadImage(fileIn, "");
    }
    /**
     * Constructs a Photo object from a file with the specified caption.
     *
     * @param fileIn The file of the photo.
     * @param capIn  The caption of the photo.
     */
    public Photo(File fileIn, String capIn){
        loadImage(fileIn, capIn);
    }
    /**
     * Constructs a Photo object by copying another Photo object.
     *
     * @param in The Photo object to copy.
     */
    public Photo(Photo in){
        loadImage(in.getFilepath(), in.getCaption());
        tags = (ArrayList<Tag>) in.getTags().clone();
    }
}
