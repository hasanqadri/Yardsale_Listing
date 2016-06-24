package models;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nathancheek on 6/24/16.
 */
public class Role extends Model {
    //Stores roles relating a specific user to a specific sale
    @Id
    public int id;
    @Constraints.Required
    public int saleId; // References specific Sale
    @Constraints.Required
    public int userId; // References specific User
    //@Constraints.Required


}
