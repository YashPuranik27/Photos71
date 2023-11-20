package photoalbum.model.data;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Photo implements Serializable {

    public static final long serialVersionUID = 1L;

    //Default date of 1/1/1970
    private Calendar dateModified = Calendar.getInstance();
    private ArrayList<Tag> tags = new ArrayList<>();
    private String caption;
    private String details = "";

    private File file;

    public Calendar getDate(){
        return dateModified;
    }

    public ArrayList<Tag> getTags(){
        return tags;
    }

    public Boolean hasTag(Tag in){
        return tags.contains(in);
    }

    public void loadImage(File fileIn, String capIn){
        file = fileIn;
        dateModified.set(Calendar.MILLISECOND, 0);
        dateModified.setTimeInMillis(file.lastModified());
        caption = capIn;
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }

    public File getFilepath(){
        return file;
    }

    public String getCaption(){
        return caption;
    }

    public void setCaption(String capIn){
        caption = capIn;
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }

    public String getDetails(){
        return details;
    }

    public void setDetails(String in){
        details = in;
        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }


    public void addTag(String name, String value){
        for(Tag t : tags){
            if(t.tagName() == name)
                return;
        }

        tags.add(new Tag(name, value));

        //try {Persistence.save(Photos.driver);}
        //    catch (IOException e) {System.out.println(e);}
    }

    public void removeTag(Tag in){
        tags.remove(in);
        //try {Persistence.save(Photos.driver);}
        //   catch (IOException e) {System.out.println(e);}
    }

    public void clearTags(){
        tags.clear();
    }

    public Photo(File fileIn){
            loadImage(fileIn, "");
    }
    public Photo(File fileIn, String capIn){
        loadImage(fileIn, capIn);
    }

    public Photo(Photo in){
        loadImage(in.getFilepath(), in.getCaption());
        tags = (ArrayList<Tag>) in.getTags().clone();
    }
}