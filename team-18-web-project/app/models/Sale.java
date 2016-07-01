package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.sql.Timestamp;
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
    public Timestamp startDate;
    public Timestamp endDate;
    @Constraints.Required
    public int userCreatedId;
    @Column(columnDefinition = "integer default 0") // New sales default to inactive
    public int isActive; // Use int because boolean isn't a type in mysql

    public Sale(String name, String description, String street, String city, String state, int zip, Timestamp startDate,
                Timestamp endDate, int userCreatedId) {
        this.name = name;
        this.description = description;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userCreatedId = userCreatedId;
        this.save();
        Role r = new Role("admin", userCreatedId, this.id);
    }

    public List<Role> getRoles() {
        return Role.findBySaleId(this.id);
    }

    public void addRole(String name, int userId) {
        Role r = new Role(name, userId, this.id);
    }

    public void deleteRole(int userId) {
        Role r = Role.findByIds(userId, this.id);
        r.delete();
    }

    public static Sale findById(int id) {
        return Ebean.find(Sale.class).where().eq("id", id).findUnique();
    }

    public String formatStartDate() {
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
        return f.format(this.startDate);
    }

    public String formatEndDate() {
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
        return f.format(this.endDate);
    }

    public String createdBy() {
        User u = User.findById(userCreatedId);
        if (u == null) { return "Deleted user"; }
        return u.getName();
    }

}
