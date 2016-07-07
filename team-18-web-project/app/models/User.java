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
    /**
     * Find all Users
     * @return List of all Users
     */
    public static List<User> findAll() {
        return Ebean.find(User.class).findList();
    }

    /**
     * Find a User by its email address
     * @param email Email address of User
     * @return User if it exists
     */
    public static User findByEmail(String email) {
        return Ebean.find(User.class).where().eq("email", email).findUnique();
    }

    /**
     * Find a User by its Id
     * @param id Id of User
     * @return User if it exists
     */
    public static User findById(int id) {
        return Ebean.find(User.class).where().eq("id", id).findUnique();
    }

    /**
     * Find a User by its Id
     * @param idStr Id of User as String
     * @return User if it exists
     */
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

    /**
     * Find a User by its username
     * @param username Username of User
     * @return User if it exists
     */
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
    @Column(columnDefinition = "tinyint default 0") // Default not a super Admin
    public int superAdmin;
    @Column(columnDefinition = "int default 0")
    public int profilePictureId;

    /**
     * Create an instance of User and save it
     * @param firstName First Name of User
     * @param lastName Last Name of User
     * @param email Email address of User
     * @param username Username of User
     * @param password Password of User
     */
    public User (String firstName, String lastName, String email, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.save();
    }

    /**
     * Create an instance of User and save it
     * todo : probably remove this, it shouldn't be needed
     * @param name Name of User
     * @param email Email address of User
     * @param username Username of User
     * @param password Password of User
     */
    public User (String name, String email, String username, String password) {
        this.setName(name);
        this.email = email;
        this.username = username;
        this.password = password;
        this.save();
    }

    /**
     * Return whether the user can act as an administrator of a sale
     * This is true if the user is a Super Admin or sale Admin
     * @param  saleId Id of sale
     * @return Whether the user can act as an administrator
     */
    public boolean canBeAdmin(int saleId) {
        if (superAdmin == 1) { return true; }
        Role role = Ebean.find(Role.class).where().eq("userId", id).eq("saleId", saleId).eq("name", "admin").findUnique();
        if (role == null) { return false; }
        return true;
    }

    /**
     * Return whether the user can act as a bookkeeper of a sale
     * This is true if the user is a Super Admin, sale Admin, sale Seller, or sale Bookkeeper
     * @param saleId Id of sale
     * @return Whether the user can act as a bookkeeper
     */
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

    /**
     * Return whether the user can act as a cashier of a sale
     * This is true if the user is a Super Admin, sale Admin, sale Seller, or sale Cashier
     * @param saleId Id of sale
     * @return Whether the user can act as a cashier
     */
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

    /**
     * Return whether the user can act as a clerk of a sale
     * This is true if the user is a Super Admin, sale Admin, sale Seller, or sale Clerk
     * @param saleId Id of sale
     * @return Whether the user can act as a clerk
     */
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

    /**
     * Return whether the user can act as a seller of a sale
     * This is true if the user is a Super Admin, sale Admin, or sale Seller
     * @param saleId Id of sale
     * @return Whether the user can act as a seller
     */
    public boolean canBeSeller(int saleId) {
        if (superAdmin == 1) { return true; }
        Role role = Ebean.find(Role.class).where().eq("userId", id).eq("saleId", saleId).or(
            Expr.eq("name", "admin"),
            Expr.eq("name", "seller")
        ).findUnique();
        if (role == null) { return false; }
        return true;
    }

    /**
     * Get the Email address of the User
     * @return Email address of the User
     */
    public String getEmail() { return email; }

    /**
     * Get the First Name of the User
     * @return First Name of the User
     */
    public String getFirstName() { return firstName; }

    /**
     * Get the Id of the User
     * @return Id of the User
     */
    public int getId() { return id; }

    /**
     * Get the Last Name of the User
     * @return Last Name of the User
     */
    public String getLastName() { return lastName; }

    /**
     * Get the Login Attempts of the User
     * @return Login Attempts of the User
     */
    public int getLoginAttempts() { return loginAttempts; }

    /**
     * Get the Name of the User
     * @return Name of the User
     */
    public String getName() { return firstName + " " + lastName; }

    /**
     * Get the Password of the User
     * @return Password of the User
     */
    public String getPassword() { return password; }

    /**
     * Get the Profile Picture Id of the User
     * @return Profile Picture Id of the User
     */
    public int getProfilePictureId() { return profilePictureId; }

    /**
     * Get the Super Admin value of the User (1 == is Super Admin, 0 == is not Super Admin)
     * @return Super Admin value of the User
     */
    public int getSuperAdmin() { return superAdmin; }

    /**
     * Get the Username of the User
     * @return Username of the User
     */
    public String getUsername() { return username; }

    /**
     * Returns whether the user is a super admin
     * @return Whether the user is a super admin
     */
    public boolean isSuperAdmin() { return superAdmin == 1; }

    /**
     * Set the Email address of the user
     * @param email Email address of the user
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Set the First Name of the user
     * @param firstName First Name of the user
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Set the Last Name of the user
     * @param lastName Last Name of the user
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Set the Login Attempts of the user
     * @param loginAttempts Login Attempts of the user
     */
    public void setLoginAttempts(int loginAttempts) { this.loginAttempts = loginAttempts; }

    /**
     * Set the Name of the user
     * @param name Name of the user
     */
    public void setName(String name) {
        int space = name.indexOf(" ");
        if (space != -1) {
            this.firstName = name.substring(0, space);
            this.lastName = name.substring(space+1);
        } else {
            this.firstName = name;
        }
    }

    /**
     * Set the Password of the user
     * @param password Password of the user
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Set the Profile Picture Id of the user
     * @param profilePictureId Profile Picture Id of the user
     */
    public void setProfilePictureId(int profilePictureId) { this.profilePictureId = profilePictureId; }

    /**
     * Set the Super Admin value of the user
     * @param superAdmin Super Admin value of the user
     */
    public void setSuperAdmin(int superAdmin) { this.superAdmin = superAdmin; }

    /**
     * Set the Username of the user
     * @param username Username of the user
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * String representation of the user object
     * @return User object description
     */
    public String toString() {
        return String.format("[Name: '%s' Email: '%s' Username: %s Password: %s]", firstName + " " + lastName,
                email, username, password);
    }
}
