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
 * Created by portega on 6/20/2016.
 */
@Entity
@Table(name="saleItems")
public class SaleItem extends Model {
    @Id
    public int id;
    @Constraints.Required
    public int saleId; // References specific Sale
    @Constraints.Required
    public String name;
    @Constraints.Required
    public String description;
    //public YardSale item_yardSale;
}
