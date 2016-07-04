package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.List;

/**
 * Created by nathancheek on 6/24/16.
 */
@Entity
@Table(name="transactions")
public class Transaction extends Model {
    public static Transaction findById(int id) {
        return Ebean.find(Transaction.class).where().eq("id", id).findUnique();
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
    @Column(columnDefinition = "tinyint default 0") // Default not a completed transaction
    public int completed;
    public String paymentMethod;

    public Transaction(int saleId, int cashierId) {

        this.saleId = saleId;
        this.cashierId = cashierId;
        this.save();
    }

    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public void setBuyerAddress(String buyerAddress) { this.buyerAddress = buyerAddress; }

    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public void setCompleted(int completed) { this.completed = completed; }

    public void setPaymentMethod(String payment) { this.paymentMethod = payment; }

    public List<LineItem> getLineItems() {
        return LineItem.findByTransactionId(id);
    }

    /**
     * Get the total of all the line items in a transaction
     * @return item totals
     */
    public float getTotal() {

        float total = 0;

        List<LineItem> lineItems = getLineItems();
        for(LineItem li: lineItems) {

            total += li.getPrice();
        }
        return total;
    }
}
