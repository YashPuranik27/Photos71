package photoalbum;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

// tags must have a name and a value. Based on the writeup, value can be a string or a number
// Tags cannot have the same name/value pair.

public record Tag(String tagName, String tagValue) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // for serialization

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // checks if object is itself
        if (!(o instanceof Tag)) return false; // checks if object is of type Tag
        Tag tag = (Tag) o;
        return Objects.equals(tagName, tag.tagName) && Objects.equals(tagValue, tag.tagValue); // checks if tag name and value are equal
    }
}
