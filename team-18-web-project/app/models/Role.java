package models;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores roles relating a specific user to a specific sale
 * Created by nathancheek on 6/24/16.
 */
@Entity
@Table(name="roles")
public class Role extends Model {
    private static final Set<String> validRoles = new HashSet<>();
    static {
        validRoles.add("admin");
        validRoles.add("bookkeeper");
        validRoles.add("cashier");
        validRoles.add("clerk");
        validRoles.add("seller");
    }

    @Id
    public int id;
    @Column(nullable=false)
    public String name;
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    public User user;
    @ManyToOne
    @JoinColumn(name = "saleId", referencedColumnName = "id")
    public Sale sale;

}
