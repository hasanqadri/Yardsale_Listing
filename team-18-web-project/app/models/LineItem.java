package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.validation.Constraints;

/**
 * Represents a Line Item in a transaction
 * Created by nyokley on 6/28/2016.
 */
@Entity
@Table(name = "lineItems")
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
        return Ebean.find(LineItem.class).where().eq("transactionId",
                transactionId).findList();
    }

    /**
     * Find a LineItem associated with a SaleItem and a Transaction
     * @param itemId Id of SaleItem
     * @param transactionId Id of Transaction
     * @return LineItem associated with a SaleItem and a Transaction
     */
    public static LineItem findByItemIdTransactionId(int itemId,
            int transactionId) {
        return Ebean.find(LineItem.class).where().eq("saleItemId", itemId).
                eq("transactionId", transactionId).findUnique();
    }

    @Id
    public int id;
    @Constraints.Required
    public int saleItemId; // References specific Sale
    public int transactionId; //References specific Transaction
    public int quantity; //item quantity with transaction
    public float unitPrice; // Store unit price in case of SaleItem modification
    public String name; // Store item name in case of SaleItem modification
    public int userCreatedId; // Store user id in case of SaleItem deletion

    /**
     * Create an instance of LineItem
     * @param saleItemId Id of SaleItem
     * @param transactionId Id of Transaction
     * @param quantity Quantity of SaleItem
     * @param unitPrice Unit price of SaleItem
     * @param name Name of SaleItem
     * @param userCreatedId Id of User who added the SaleItem to Sale
     */
    public LineItem(int saleItemId, int transactionId, int quantity,
            float unitPrice, String name, int userCreatedId) {
        this.saleItemId = saleItemId;
        this.transactionId = transactionId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.name = name;
        this.userCreatedId = userCreatedId;
        this.save();
    }

    /**
     * Get a formatted price of item*quantity
     * @return item*quantity to 2 decimal places
     */
    public String formatTotalPrice() {
        float price = getTotalPrice();
        return String.format("%.2f", price);
    }

    /**
     * Get a formatted price of item
     * @return item to 2 decimal places
     */
    public String formatUnitPrice() {
        float price = getUnitPrice();
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
    public int getId() {
        return id;
    }

    /**
     * Get the name of the item when it was sold
     * @return name of the item when it was sold
     */
    public String getName() {
        return name;
    }

    /**
     * Get the total price when it was sold
     * @return unitPrice * quantity
     */
    public float getTotalPrice() {
        return unitPrice * quantity;
    }

    /**
     * Get the unit price of item when it was sold
     * @return unitPrice
     */
    public float getUnitPrice() {
        return unitPrice;
    }

    /**
     * Get the quantity of this LineItem
     * @return quantity of this LineItem
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Get the saleItemId of this LineItem
     * @return saleItemId of this LineItem
     */
    public int getSaleItemId() {
        return saleItemId;
    }

    /**
     * Get the transactionId of this LineItem
     * @return transactionId of this LineItem
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * Set Quantity
     * @param quantity Quantity of SaleItem
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
