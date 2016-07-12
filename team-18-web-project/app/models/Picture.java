package models;

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
    @Id
    public int id;
    @Constraints.Required
    @Lob
    public byte[] image;
}
