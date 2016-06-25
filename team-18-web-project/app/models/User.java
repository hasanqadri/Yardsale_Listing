package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Individual user that accesses the website, has name, user, pass, email, etc.
 */
@Entity
@Table(name="users")
public class User extends Model {
    @Id
    private int id;
    @Constraints.Required
    private String email;
    @Constraints.Required
    private String name;
    @Constraints.Required
    private String password;
    @Constraints.Required
    private String username;
    private int account_locked;
    private int login_attempts;
    private boolean loggedin = false;

    /**
     * no-args constructor required by compiler
     */
    public User() {

    }
    /**
     * Takes form data to create user object
     */
    public User(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.loggedin = true;
    }

    /**
     * Sets email
     * @param email setter for email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * get email string
     * @return email getter for email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets password
     * @param password setter for password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * gets password of user
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets name of user
     * @param name setter for name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets name of user
     * @retur  name
     */
    public String getName() {
        return name;
    }


    /**
     * sets username of user
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * gets whether user is loggedin
     * @retur  boolean logged in or not
     */
    public boolean getLoggedin() {
        return loggedin;
    }

    /**
     * sets boolean of logged in of user
     * @param b boolean
     */
    public void setLoggedin(boolean b) {
        this.loggedin = b;
    }

    /**
     * gets username of user
     * @return username
     */
    public String getUsername() {
        return username;
    }


    public String toString() {
        return String.format("[Name: '%s' Email: '%s' Username: %s Password: %s]", this.getName(),
                this.getEmail(), this.getUsername(), this.getPassword());
    }

    public static Finder<String, User> find = new Finder<String,User>(User.class);

}
