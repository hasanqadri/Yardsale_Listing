package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.List;

/**
 * Created by nyokley on 6/28/2016.
 */
@Entity
@Table(name="lineItems")
public class LineItem extends Model {

    public static LineItem findById(int id) {
        return Ebean.find(LineItem.class).where().eq("id", id).findUnique();
    }

    public static List<LineItem> findByTransactionId(int transactionId) {
        return Ebean.find(LineItem.class).where().eq("transactionId", transactionId).findList();
    }

    public static LineItem findByItemIdTransactionId(int itemId, int transactionId) {
        return Ebean.find(LineItem.class).where().eq("saleItemId", itemId).eq("transactionId", transactionId).findUnique();
    }

    @Id
    public int id;
    @Constraints.Required
    public int saleItemId; // References specific Sale
    public int transactionId; //References specific Transaction
    public int quantity; //item quantity with transaction

    public LineItem (int saleItemId, int transactionId, int quantity) {

        this.saleItemId = saleItemId;
        this.transactionId= transactionId;
        this.quantity = quantity;
        this.save();

    }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * Get the price of item*quantity
     * @return item*quantity
     */
    public float getPrice() {

        SaleItem item = SaleItem.findById(saleItemId);

        return item.price*quantity;
    }

    /**
     * Get the name of the item
     * @return name of the item
     */
    public String getName() {
        return SaleItem.findById(saleItemId).name;
    }

}

