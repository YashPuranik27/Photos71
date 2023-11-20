/**
 * Authors
 *
 * @author Yash Puranik, Joseph Arrigo
 */
package Photos71.src.model.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

// tags must have a name and a value. Based on the writeup, value can be a string or a number
// Tags cannot have the same name/value pair.
/**
 * A record for a tag associated with a photo, which includes a name and a value.
 * Tags are unique based on their name/value pair combination.
 * @param tagName name of the tag
 * @param tagValue value of the tag
 *
 * @author Yash Puranik, Joseph Arrigo
 */
public record Tag(String tagName, String tagValue) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // for serialization
    /**
     * Determines whether the specified object is equal to the current Tag.
     * The result is true if and only if the argument is not null and is a Tag
     * object that has the same tag name and value as this object.
     *
     * @param o The object to compare this {@code Tag} against.
     * @return true if the given object represents a {@code Tag} equivalent to this Tag, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // checks if object is itself
        if (!(o instanceof Tag)) return false; // checks if object is of type Tag
        Tag tag = (Tag) o;
        return Objects.equals(tagName, tag.tagName) && Objects.equals(tagValue, tag.tagValue); // checks if tag name and value are equal
    }
}
