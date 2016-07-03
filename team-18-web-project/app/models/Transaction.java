package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by nathancheek on 6/24/16.
 */
@Entity
@Table(name="transactions")
public class Transaction extends Model {

    public static Sale findById(int saleId) {
        return Ebean.find(Sale.class).where().eq("saleId", saleId).findUnique();
    }

    @Id
    public int id;
    @Constraints.Required
    public int saleId;
    @Constraints.Required
    public int cashierId;
    public int saleItemId; // Id of item in transaction
    public String buyerName; //buyers name

    public Transaction(int saleId) {

        this.saleId = saleId;
    }
}
