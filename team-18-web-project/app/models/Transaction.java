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
public class Transaction extends Model {
    @Id
    public int id;
    @Constraints.Required
    public int saleItemId; // Id of item in transaction
}
