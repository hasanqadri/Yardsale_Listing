package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * Created by portega on 6/20/2016.
 */
@Entity
@Table(name="saleItems")
public class SaleItem extends Model {

    public static SaleItem findById(int id) {
        return Ebean.find(SaleItem.class).where().eq("id", id).findUnique();
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
    public SaleItem (String name, String description, float price, int saleId, int userCreatedId, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.saleId = saleId;
        this.userCreatedId = userCreatedId;
        this.quantity = quantity;
        this.save();
    }


    public int getId() {

        return id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public float getPrice() { return price; }

    public void setPrice(float price) { this.price = price; }

    public int getSaleId() { return saleId; }

    public void setSaleId(int saleId) { this.saleId = saleId; }

    public int getUserCreatedId() { return userCreatedId; }

    public void setUserCreatedId(int userCreatedId) { this.userCreatedId = userCreatedId; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }


    public String toString() {
        return String.format("[Name: " + name + " Description: " + description + " Price: "
                + price);
    }

    public String formatPrice() {
        return String.format("%.2f", price);
    }
}
