package models;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by portega on 6/20/2016.
 */
@Entity
@Table(name="items")
public class SaleItem {
    @Id
    public int saleItemId;
    public YardSale item_yardSale;
}
