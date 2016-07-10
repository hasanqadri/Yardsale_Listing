package models;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.Constraints;
import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Picture
 * Created by nathancheek on 6/24/16.
 */
//@Entity
//@Table(name="pictures")
public class Picture extends Model {
    @Id
    public int id;
    @Constraints.Required
    public Blob image;
}
