
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
@Table(name="yardSales")
public class YardSale extends Model {
    @Id
    public int yardSale_id;
    @Constraints.Required
    public String city_name;
    @Constraints.Required
    public String state_name;
    //@ManyToOne()
    public User user;
    public boolean isCurrent;
    public List<SaleItem> items = new ArrayList<>();

}
