package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
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
     * Find all LineItems that are associated with a Sale
     * @param  saleId Id of Sale
     * @return LineItems associated with Sale
     */
    public static List<LineItem> findBySaleId(int saleId) {
        return Ebean.find(LineItem.class).where().eq("saleId", saleId).
                findList();
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

    /**
     * Find LineItems associated with a Sale and a Seller
     * @param saleId Id of Sale
     * @param userCreatedId Id ofSaleItem Seller
     * @return list of LineItems associated with Sale and Seller
     */
    public static List<LineItem> findBySaleIdUserCreatedId(int saleId,
            int userCreatedId) {
        return Ebean.find(LineItem.class).where().eq("saleId", saleId).
                eq("userCreatedId", userCreatedId).findList();
    }

    /**
     * sorts line items into a map associated with a seller
     * @return List of all open sales
     */
    public static  Map<User,List<LineItem>> getLineItemsBySeller(Sale s) {
        Map<User,List<LineItem>> itemsByUser = new TreeMap<User, List<LineItem>>();
        Map<User,Float> totals = new TreeMap<User, Float>();

        // Loop through all line items within a sale and add the
        // LineItem to the seller's key
        for (LineItem li: LineItem.findBySaleId(s.id)) {
            if (itemsByUser.containsKey(User.findById(li.userCreatedId))) {
                itemsByUser.get(User.findById(li.userCreatedId)).add(li);
            } else {
                List<LineItem> userSoldItems = new ArrayList<LineItem>(1);
                userSoldItems.add(li);
                itemsByUser.put(User.findById(li.userCreatedId), userSoldItems);
            }
        }
        return itemsByUser;
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
    public int saleId; // Id of sale this belongs to

    /**
     * Create an instance of LineItem
     * @param saleItemId Id of SaleItem
     * @param saleId Id of Sale
     * @param sellerId Id of SaleItem seller
     * @param transactionId Id of Transaction
     * @param quantity Quantity of SaleItem
     * @param unitPrice Unit price of SaleItem
     * @param name Name of SaleItem
     * @param userCreatedId Id of User who added the SaleItem to Sale
     */
    public LineItem(int saleItemId, int transactionId, int quantity,
            float unitPrice, String name, int userCreatedId, int saleId) {
        this.saleItemId = saleItemId;
        this.transactionId = transactionId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.name = name;
        this.userCreatedId = userCreatedId;
        this.saleId = saleId;
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
     * Get the saleId of this LineItem
     * @return saleId of this LineItem
     */
    public int getSaleId() {
        return saleId;
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
