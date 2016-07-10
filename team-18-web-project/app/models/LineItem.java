package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.List;

/**
 * Represents a Line Item in a transaction
 * Created by nyokley on 6/28/2016.
 */
@Entity
@Table(name="lineItems")
public class LineItem extends Model {
    /**
     * Find LineItem by its Id
     * @param id Id of LineItem
     * @return LineItem if it exists
     */
    public static LineItem findById(int id) {
        return Ebean.find(LineItem.class).where().eq("id", id).findUnique();
    }

    /**
     * Find all LineItems associated with a Transaction
     * @param transactionId Id of Transaction
     * @return LineItems associated with a Transaction
     */
    public static List<LineItem> findByTransactionId(int transactionId) {
        return Ebean.find(LineItem.class).where().eq("transactionId", transactionId).findList();
    }

    /**
     * Find a LineItem associated with a SaleItem and a Transaction
     * @param itemId Id of SaleItem
     * @param transactionId Id of Transaction
     * @return LineItem associated with a SaleItem and a Transaction
     */
    public static LineItem findByItemIdTransactionId(int itemId, int transactionId) {
        return Ebean.find(LineItem.class).where().eq("saleItemId", itemId).eq("transactionId", transactionId).findUnique();
    }

    @Id
    public int id;
    @Constraints.Required
    public int saleItemId; // References specific Sale
    public int transactionId; //References specific Transaction
    public int quantity; //item quantity with transaction

    /**
     * Create an instance of LineItem
     * @param saleItemId Id of SaleItem
     * @param transactionId Id of Transaction
     * @param quantity Quantity of SaleItem
     */
    public LineItem (int saleItemId, int transactionId, int quantity) {
        this.saleItemId = saleItemId;
        this.transactionId= transactionId;
        this.quantity = quantity;
        this.save();
    }

    /**
     * Get a formatted price of item*quantity
     * @return item*quantity to 2 decimal places
     */
    public String formatPrice() {
        float price = getPrice();
        return String.format("%.2f", price);
    }

    /**
     * Get the description of the SaleItem
     * @return description of the SaleItem
     */
    public String getDescription() {
        return SaleItem.findById(saleItemId).description;
    }

    /**
     * Get the id of this LineItem
     * @return id of this LineItem
     */
    public int getId() { return id; }

    /**
     * Get the name of the SaleItem
     * @return name of the SaleItem
     */
    public String getName() {
        return SaleItem.findById(saleItemId).name;
    }

    /**
     * Get the price of item*quantity
     * @return item*quantity
     */
    public float getPrice() {
        SaleItem item = SaleItem.findById(saleItemId);
        return item.price*quantity;
    }

    /**
     * Get the quantity of this LineItem
     * @return quantity of this LineItem
     */
    public int getQuantity() { return quantity; }

    /**
     * Get the saleItemId of this LineItem
     * @return saleItemId of this LineItem
     */
    public int getSaleItemId() { return saleItemId; }

    /**
     * Get the transactionId of this LineItem
     * @return transactionId of this LineItem
     */
    public int getTransactionId() { return transactionId; }

    /**
     * Set Quantity
     * @param quantity Quantity of SaleItem
     */
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
