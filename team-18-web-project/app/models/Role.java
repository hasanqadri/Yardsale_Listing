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

    public static List<Role> findByUserId(int userId) {
        return Ebean.find(Role.class).where().eq("userId", userId).findList();
    }

    public static List<Role> findBySaleId(int saleId) {
        return Ebean.find(Role.class).where().eq("saleId", saleId).findList();
    }

    public static Role findByIds(int userId, int saleId) {
        return Ebean.find(Role.class).where().eq("userId", userId).eq("saleId", saleId).findUnique();
    }

    public static boolean isAdmin(int userId, int saleId) {
        Role r = findByIds(userId, saleId);
        if (r == null) { return false; }
        return r.name.equals("admin");
    }

    public static boolean isBookkeeper(int userId, int saleId) {
        Role r = findByIds(userId, saleId);
        if (r == null) { return false; }
        return r.name.equals("bookkeeper");
    }

    public static boolean isCashier(int userId, int saleId) {
        Role r = findByIds(userId, saleId);
        if (r == null) { return false; }
        return r.name.equals("cashier");
    }

    public static boolean isClerk(int userId, int saleId) {
        Role r = findByIds(userId, saleId);
        if (r == null) { return false; }
        return r.name.equals("clerk");
    }

    public static boolean isSeller(int userId, int saleId) {
        Role r = findByIds(userId, saleId);
        if (r == null) { return false; }
        return r.name.equals("seller");
    }
}
