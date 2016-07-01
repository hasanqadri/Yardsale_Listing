package models;

import com.avaje.ebean.Ebean;
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

    public static Role findByIds(int userId, int saleId) {
        return Ebean.find(Role.class).where().eq("userId", userId).eq("saleId", saleId).findUnique();
    }

    public static List<Role> findBySaleId(int saleId) {
        return Ebean.find(Role.class).where().eq("saleId", saleId).findList();
    }

    public static List<Role> findByUserId(int userId) {
        return Ebean.find(Role.class).where().eq("userId", userId).findList();
    }

    public static String findRole(int userId, int saleId) {
        if (User.findById(userId).isSuperAdmin()) {
            return "superAdmin";
        }
        Role r = findByIds(userId, saleId);
        if (r == null) {
            return "guest";
        }
        return r.name;
    }

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
    @Column(nullable=false)
    public int userId;
    @Column(nullable=false)
    public int saleId;

    public Role (String name, int userId, int saleId) {
        this.name = name;
        this.userId = userId;
        this.saleId = saleId;
        this.save();
    }

    public User getUser() {
        return User.findById(userId);
    }
}
