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
    public String name;
    public String description;
    public String street;
    public String city;
    public String state;
    public int zip;
    public double startDate;
    public double endDate;
    @Constraints.Required
    public int userCreatedId;
    @Column(columnDefinition = "integer default 0") // New sales default to inactive
    public int isActive; // Use int because boolean isn't a type in mysql
}
