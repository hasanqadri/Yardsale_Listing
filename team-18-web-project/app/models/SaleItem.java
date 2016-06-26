package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Created by portega on 6/20/2016.
 */
@Entity
@Table(name="saleItems")
public class SaleItem extends Model {
    @Id
    public int id;
    @Constraints.Required
    public String name;
    public String description;
    public float price;
    public int pictureId; // References specific Picture
    @Constraints.Required
    public int saleId; // References specific Sale


    public SaleItem (String name, String description, float price, int saleId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.saleId = saleId;

    }


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public float getPrice() { return price; }

    public void setPrice(float price) { this.price = price; }


    public static Finder<String, SaleItem> find = new Finder<String,SaleItem>(SaleItem.class);

    public String toString() {
        return String.format("[Name: " + name + " Description: " + description + " Price: "
                + price);
    }
}
