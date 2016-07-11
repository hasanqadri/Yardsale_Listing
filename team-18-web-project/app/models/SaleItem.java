package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents a Sale Item
 * Created by portega on 6/20/2016.
 */
@Entity
@Table(name="saleItems")
public class SaleItem extends Model {
    /**
     * Find SaleItem by its Id
     * @param  id Id of SaleItem
     * @return SaleItem if it exists
     */
    public static SaleItem findById(int id) {
        return Ebean.find(SaleItem.class).where().eq("id", id).findUnique();
    }

    /**
     * Find SaleItem by its Id
     * @param  id Id of SaleItem
     * @return SaleItem if it exists
     */
    public static SaleItem findById(String idStr) {
        int id = 0;
        try {
          id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return findById(id);
    }

    /**
     * Find SaleItems by sale Id
     * @param  id Id of Sale
     * @return SaleItems associated with a sale
     */
    public static List<SaleItem> findBySaleId(int saleId) {
        return Ebean.find(SaleItem.class).where().eq("saleId", saleId).findList();
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
    @SequenceGenerator(name="seq", sequenceName = "local.seq_name", initialValue=1000001, allocationSize=1)
    // ^ TODO this doesn't actually work
    public int id;
    @Constraints.Required
    public String name;
    public String description;
    public float price;
    public int pictureId; // References specific Picture
    @Constraints.Required
    public int saleId; // References specific Sale
    @Constraints.Required
    public int userCreatedId; // Multiple sellers can exist per sale
    public int quantity; //quantity within sale

    /**
     * Create an instance of SaleItem and save it
     * @param name Name of SaleItem
     * @param description Description of SaleItem
     * @param price Unit price of SaleItem
     * @param saleId Sale Id of the Sale the SaleItem is associated with
     * @param userCreatedId User Id of the User who created the SaleItem
     * @param quantity Quantity of SaleItem in stock
     */
    public SaleItem (String name, String description, float price, int saleId, int userCreatedId, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.saleId = saleId;
        this.userCreatedId = userCreatedId;
        this.quantity = quantity;
        this.save();
    }

    /**
     * Format the unit price of the SaleItem
     * @return Unit price formatted to 2 decimal places
     */
    public String formatPrice() {
        return String.format("%.2f", price);
    }

    /**
     * Get the Description of the SaleItem
     * @return Description of the SaleItem
     */
    public String getDescription() { return description; }

    /**
     * Get the Id of the SaleItem
     * @return Id of the SaleItem
     */
    public int getId() { return id; }

    /**
     * Get the Name of the SaleItem
     * @return Name of the SaleItem
     */
    public String getName() { return name; }

    /**
     * Get the Price of the SaleItem
     * @return Price of the SaleItem
     */
    public float getPrice() { return price; }

    /**
     * Get the Quantity of the SaleItem
     * @return Quantity of the SaleItem
     */
    public int getQuantity() { return quantity; }

    /**
     * Get the Sale Id of the Sale the SaleItem is associated with
     * @return Sale Id of the Sale the SaleItem is associated with
     */
    public int getSaleId() { return saleId; }

    /**
     * Get the User Id of the User who created the SaleItem
     * @return User Id of the User who created the SaleItem
     */
    public int getUserCreatedId() { return userCreatedId; }

    /**
     * Set the Description of the SaleItem
     * @param description Description of the SaleItem
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Set the Name of the SaleItem
     * @param name Name of the SaleItem
     */
    public void setName(String name) { this.name = name; }

    /**
     * Set the Unit Price of the SaleItem
     * @param price Unit Price of the SaleItem
     */
    public void setPrice(float price) { this.price = price; }

    /**
     * Set the Quantity of the SaleItem in stock
     * @param quantity Quantity of the SaleItem in stock
     */
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * Set the Sale Id of the Sale the SaleItem is associated with
     * @param saleId Sale Id of the Sale the SaleItem is associated with
     */
    public void setSaleId(int saleId) { this.saleId = saleId; }

    /**
     * Set the User Id of the User who created the SaleItem
     * @param userCreatedId User Id of the User who created the SaleItem
     */
    public void setUserCreatedId(int userCreatedId) { this.userCreatedId = userCreatedId; }

    /**
     * String representation of the SaleItem object
     * @return SaleItem object description
     */
    public String toString() {
        return String.format("[Name: " + name + " Description: " + description + " Price: "
                + price);
    }
}
