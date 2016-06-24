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
 * Created by portega on 6/20/2016.
 */
@Entity
@Table(name="sales")
public class Sale extends Model {
    @Id
    public int id;
    @Constraints.Required
    public String city;
    @Constraints.Required
    public String state;
    @Constraints.Required
    public int userCreatedId;
    @Column(columnDefinition = "integer default 0") // New sales default to closed
    public int isOpen; // Use int because boolean isn't a type in mysql
    //public List<SaleItem> items = new ArrayList<>();

}
