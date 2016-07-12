package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.validation.Constraints;

/**
 * Represents a sale
 * Created by portega on 6/20/2016.
 */
@Entity
@Table(name = "sales")
public class Sale extends Model {

    /**
     * Find all open sales
     * @return List of all open sales
     */
    public static List<Sale> findAllOpen() {
        return Ebean.find(Sale.class).where().eq("status", 1)
                .orderBy("id desc").findList();
    }

    /**
     * Find sale by its Id
     * @param id Id of sale
     * @return Sale if it exists
     */
    public static Sale findById(int id) {
        return Ebean.find(Sale.class).where().eq("id", id).findUnique();
    }

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
    // New sales are not open for transactions
    @Column(columnDefinition = "integer default 0")
    // 0 : sale is being built
    // 1: sale is open for transactions
    // 2: sale is closed and archived
    public int status;

    /**
     * Create an instance of Sale and set the creating user as a sale admin
     * @param name Name of sale
     * @param description Description of sale
     * @param street Street address of sale
     * @param city City address of sale
     * @param state State address of sale
     * @param zip Zip code address of sale
     * @param startDate Starting date and time of sale
     * @param endDate Ending date and time of sale
     * @param userCreatedId Id of user who created the sale
     */
    public Sale(String name, String description, String street, String city,
                String state, int zip, Timestamp startDate, Timestamp endDate,
                int userCreatedId) {
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

    /**
     * Add item to sale
     * @param name Name of item
     * @param description Description of item
     * @param price Price of item
     * @param userId User Id adding the item
     * @param quantity Quantity of item
     * @return SaleItem that was created
     */
    public SaleItem addItem(String name, String description, float price,
            int userId, int quantity) {
        return new SaleItem(name, description, price, id, userId, quantity);
    }

    /**
     * Add item to sale
     * @param name Name of item
     * @param description Description of item
     * @param priceStr Price of item as string (for handling form inputs)
     * @param userId User Id adding the item
     * @param quantityStr Quantity of item as string (for handling form inputs)
     * @return SaleItem that was created
     */
    public SaleItem addItem(String name, String description, String priceStr,
            int userId, String quantityStr) {
        float price = 0;
        int quantity = 0;
        try {
            price = Float.parseFloat(priceStr);
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return addItem(name, description, price, userId, quantity);
    }

    /**
     * Add role to the sale
     * @param name Name of role
     * @param userId User Id to associate with role
     */
    public void addRole(String name, int userId) {
        Role r = new Role(name, userId, this.id);
    }

    /**
     * Get the name of user who created the sale
     * @return Name of user who created the sale
     */
    public String getCreatedBy() {
        User u = User.findById(userCreatedId);
        if (u == null) {
            return "Deleted user";
        }
        return u.getName();
    }

    /**
     * Remove a role from the sale
     * @param userId User Id to remove from sale
     */
    public void deleteRole(int userId) {
        Role r = Role.findByIds(userId, this.id);
        r.delete();
    }

    /**
     * Get a formatted string of the end date
     * @return Formatted string of the end date
     */
    public String formatEndDate() {
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
        return f.format(this.endDate);
    }

    /**
     * Get a formatted string of the start date
     * @return Formatted string of the start date
     */
    public String formatStartDate() {
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
        return f.format(this.startDate);
    }

    /**
     * Get all items associated with the sale
     * @return Items associated with the sale
     */
    public List<SaleItem> getItems() {
        return Ebean.find(SaleItem.class).where().eq("saleId", id).findList();
    }

    /**
     * Get all roles associated with the sale
     * @return Roles associated with the sale
     */
    public List<Role> getRoles() {
        return Role.findBySaleId(this.id);
    }

    /**
     * Set City
     * @param city City address of sale
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Set Description
     * @param description Description of sale
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set endDate
     * @param endDate Ending time and date of Sale
     */
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    /**
     * Set Name
     * @param name Name of sale
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set startDate
     * @param startDate Starting time and date of Sale
     */
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    /**
     * Set State
     * @param state State address of sale
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Set Status
     * @param status Status of sale (1 of 3 stages)
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Set Street
     * @param street Street address of sale
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Set userCreatedId
     * @param userCreatedId Set Id of user who created the sale
     */
    public void setUserCreatedId(int userCreatedId) {
        this.userCreatedId = userCreatedId;
    }

    /**
     * Set Zip
     * @param zip Zip code address of sale
     */
    public void setZip(int zip) {
        this.zip = zip;
    }
}
