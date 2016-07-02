package models;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by nyokley on 6/28/2016.
 */
@Entity
@Table(name="lineItems")
public class LineItem extends Model {

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

    }

    //returns price of item*quantity
    public float getPrice() {

        SaleItem item = SaleItem.findById(saleItemId);

        return item.price*quantity;
    }

}

