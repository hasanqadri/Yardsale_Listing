package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores roles relating a specific user to a specific sale
 * Created by nathancheek on 6/24/16.
 */
@Entity
@Table(name = "roles")
public class Role extends Model {
    /**
     * Find a Role by User Id and Sale Id
     * @param userId Id of User
     * @param saleId Id of Sale
     * @return Role if it exists
     */
    public static Role findByIds(int userId, int saleId) {
        return Ebean.find(Role.class).where().eq("userId", userId).eq("saleId",
                saleId).findUnique();
    }

    /**
     * Find all Roles that are associated with a Sale
     * @param  saleId Id of Sale
     * @return Roles if they exist
     */
    public static List<Role> findBySaleId(int saleId) {
        return Ebean.find(Role.class).where().eq("saleId", saleId).findList();
    }

    /**
     * Find all Roles that are associated with a User
     * @param  userId Id of User
     * @return Roles if they exist
     */
    public static List<Role> findByUserId(int userId) {
        return Ebean.find(Role.class).where().eq("userId", userId).findList();
    }

    /**
     * Find the name of a Role by its User Id and SaleId
     * @param userId Id of User
     * @param saleId Id of Sale
     * @return Name of role
     */
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

    /**
     * Set of all valid role names
     */
    public static final Set<String> VALIDROLES = new HashSet<>();
    static {
        VALIDROLES.add("admin");
        VALIDROLES.add("bookkeeper");
        VALIDROLES.add("cashier");
        VALIDROLES.add("clerk");
        VALIDROLES.add("seller");
    }

    @Id
    public int id;
    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public int userId;
    @Column(nullable = false)
    public int saleId;

    /**
     * Create an instance of Role and save it
     * @param name Name of Role
     * @param userId Id of User
     * @param saleId Id of Sale
     */
    public Role(String name, int userId, int saleId) {
        this.name = name;
        this.userId = userId;
        this.saleId = saleId;
        this.save();
    }

    /**
     * Set the Name of the Role
     * @param name of the Role
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the User Id the Role is associated with
     * @param userId User Id the Role is associated with
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Set the Sale Id the Role is associated with
     * @param saleId Sale Id the Role is assocated with
     */
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    /**
     * Get the User associated with the Role
     * @return User associated with the Role
     */
    public User getUser() {
        return User.findById(userId);
    }
}
