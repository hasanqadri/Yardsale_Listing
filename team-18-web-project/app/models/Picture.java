package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import java.sql.Blob;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import play.data.validation.Constraints;

/**
 * Represents a Picture - Work in progress
 * Created by nathancheek on 6/24/16.
 */
@Entity
@Table(name = "pictures")
public class Picture extends Model {
    /**
     * Find a Picture by its id
     * @param id Id of Picture
     * @return Picture if it exists
     */
    public static Picture findById(int id) {
        return Ebean.find(Picture.class).where().eq("id", id).findUnique();
    }

    @Id
    public int id;
    @Constraints.Required
    @Lob
    public byte[] image;

    /**
     * Create an instance of Picture
     * @param image Picture as byte array
     */
    public Picture(byte[] image) {
        this.image = image;
        this.save();
    }
}
