package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.Constraints;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

/**
 * Represents a User
 */
@Entity
@Table(name="users")
public class User extends Model {

    public static List<User> findAll() {
        return Ebean.find(User.class).findList();
    }

    public static User findByEmail(String email) {
        return Ebean.find(User.class).where().eq("email", email).findUnique();
    }

    public static User findById(int id) {
        return Ebean.find(User.class).where().eq("id", id).findUnique();
    }

    public static User findById(String idStr) {
        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return findById(id);
    }

    public static User findByUsername(String username) {
        return Ebean.find(User.class).where().eq("username", username).findUnique();
    }

    @Id
    public int id;
    @Constraints.Required
    public String email;
    @Constraints.Required
    public String firstName;
    @Constraints.Required
    public String lastName;
    @Constraints.Required
    public String username;
    @Constraints.Required
    public String password;
    @Column(columnDefinition = "tinyint default 0") // 0 login attempts at creation
    public int loginAttempts;
    @Column(columnDefinition = "tinyint default 0") // Default not a superUser
    public int superAdmin;
    @Column(columnDefinition = "int default 0")
    public int profilePictureId;

    public User (String firstName, String lastName, String email, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    //For backwards compatibility - this may be removed in future
    public User (String name, String email, String username, String password) {
        this.setName(name);
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public int getId() { return id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getName() { return firstName + " " + lastName; }

    public void setName(String name) {
        int space = name.indexOf(" ");
        if (space != -1) {
            this.firstName = name.substring(0, space);
            this.lastName = name.substring(space+1);
        } else {
            this.firstName = name;
        }
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public int getLoginAttempts() { return loginAttempts; }

    public void setLoginAttempts(int loginAttempts) { this.loginAttempts = loginAttempts; }

    public String toString() {
        return String.format("[Name: '%s' Email: '%s' Username: %s Password: %s]", firstName + " " + lastName,
                email, username, password);
    }

    public boolean isSuperAdmin() { return superAdmin == 1; }

    public boolean canBeAdmin(int saleId) {
        if (superAdmin == 1) { return true; }
        Role role = Ebean.find(Role.class).where().eq("userId", id).eq("saleId", saleId).eq("name", "admin").findUnique();
        if (role == null) { return false; }
        return true;
    }

    public boolean canBeBookkeeper(int saleId) {
        if (superAdmin == 1) { return true; }
        Role role = Ebean.find(Role.class).where().eq("userId", id).eq("saleId", saleId).or(
            Expr.eq("name", "admin"),
            Expr.or(
                Expr.eq("name", "bookkeeper"),
                Expr.eq("name", "seller")
            )
        ).findUnique();
        if (role == null) { return false; }
        return true;
    }

    public boolean canBeCashier(int saleId) {
        if (superAdmin == 1) { return true; }
        Role role = Ebean.find(Role.class).where().eq("userId", id).eq("saleId", saleId).or(
            Expr.eq("name", "admin"),
            Expr.or(
                Expr.eq("name", "cashier"),
                Expr.eq("name", "seller")
            )
        ).findUnique();
        if (role == null) { return false; }
        return true;
    }

    public boolean canBeClerk(int saleId) {
        if (superAdmin == 1) { return true; }
        Role role = Ebean.find(Role.class).where().eq("userId", id).eq("saleId", saleId).or(
            Expr.eq("name", "admin"),
            Expr.or(
                Expr.eq("name", "clerk"),
                Expr.eq("name", "seller")
            )
        ).findUnique();
        if (role == null) { return false; }
        return true;
    }

    public boolean canBeSeller(int saleId) {
        if (superAdmin == 1) { return true; }
        Role role = Ebean.find(Role.class).where().eq("userId", id).eq("saleId", saleId).or(
            Expr.eq("name", "admin"),
            Expr.eq("name", "seller")
        ).findUnique();
        if (role == null) { return false; }
        return true;
    }
}
