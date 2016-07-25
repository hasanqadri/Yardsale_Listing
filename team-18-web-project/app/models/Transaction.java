package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Represents a Transaction
 * Created by nathancheek on 6/24/16.
 */
@Entity
@Table(name = "transactions")
public class Transaction extends Model {
    /**
     * Find transaction by its Id
     * @param id Id of transaction
     * @return Transaction if it exists
     */
    public static Transaction findById(int id) {
        return Ebean.find(Transaction.class).where().eq("id", id).findUnique();
    }

    /**
     * Find transaction by its Id
     * @param idStr Id of transaction
     * @return Transaction if it exists
     */
    public static Transaction findById(String idStr) {
        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return findById(id);
    }

    /**
     * Find all completed transactions by Sale Id
     * @param  saleId Id of sale
     * @return Completed transactions associated with a Sale
     */
    public static List<Transaction> findCompletedBySaleId(int saleId) {
        return Ebean.find(Transaction.class).where().eq("saleId", saleId)
                .eq("completed", 1).findList();
    }

    @Id
    public int id;
    @Constraints.Required
    public int saleId;
    @Constraints.Required
    public int cashierId;
    public String buyerName; //buyers name
    public String buyerAddress;
    public String buyerEmail;
    // Default not a completed transaction
    @Column(columnDefinition = "tinyint default 0")
    public int completed;
    public String paymentMethod;
    public int randomNonce; // Used to authorize a mobile device to scan items
    public Timestamp date; // Date when transaction was completed

    /**
     * Create an instance of Transaction and save it
     * @param saleId Id of Sale
     * @param cashierId Id of Cashier user
     */
    public Transaction(int saleId, int cashierId) {
        this.saleId = saleId;
        this.cashierId = cashierId;
        Random r = new SecureRandom();
        this.randomNonce = r.nextInt();
        this.save();
    }

    public boolean checkNonce(String nonceStr) {
        int nonce = 0;
        try {
            nonce = Integer.parseInt(nonceStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return this.randomNonce == nonce;
    }

    /**
     * Get a formatted string of the transaction date/time
     * @return Get a formatted string of the transaction date/time
     */
    public String formatDate() {
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy H:m:s");
        return f.format(this.date);
    }

    /**
     * Format the total of all line items in the transaction
     * @return Total transaction cost formatted to 2 decimal places
     */
    public String formatTotal() {
        return String.format("%.2f", this.getTotal());
    }

    /**
     * Get LineItems associated with the transaction
     * @return LineItems associated with the transaction
     */
    public List<LineItem> getLineItems() {
        return LineItem.findByTransactionId(id);
    }

    /**
     * Get the total of all the line items in the transaction
     * @return Total transaction cost
     */
    public double getTotal() {

        double total = 0;

        List<LineItem> lineItems = getLineItems();
        for (LineItem li: lineItems) {
            total += li.getTotalPrice();
        }
        return total;
    }

    /**
     * Set buyerName
     * @param buyerName Name of Buyer
     */
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    /**
     * Set buyerAddress
     * @param buyerAddress Address of Buyer
     */
    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    /**
     * Set buyerEmail
     * @param buyerEmail Email of Buyer
     */
    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    /**
     * Set if sale is completed
     * @param completed Is sale completed
     */
    public void setCompleted(int completed) {
        this.completed = completed;
        Date date = new Date();
        this.date = new Timestamp(date.getTime());
    }

    /**
     * Set payment method
     * @param payment Payment method
     */
    public void setPaymentMethod(String payment) {
        this.paymentMethod = payment;
    }
}
