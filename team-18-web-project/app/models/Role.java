package models;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores roles relating a specific user to a specific sale
 * Created by nathancheek on 6/24/16.
 */
//@Entity
public class Role extends Model {
    //todo get this working
    /*@EmbeddedId
    public RoleKey key;
    @Constraints.Required
    public String userRole; // I'd rather use ENUM here but I'm not sure how

    @Embeddable
    public class RoleKey { // Primary key is made up of saleId and userId
        @Constraints.Required
        public int saleId; // References specific Sale
        @Constraints.Required
        public int userId; // References specific User
    }*/

}
