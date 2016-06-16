package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

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

    public User() {

    }

    public User(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.loggedin = true;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getLoggedin() {
        return loggedin;
    }

    public void setLoggedin(boolean b) {
        this.loggedin = b;
    }

    public String getUsername() {
        return username;
    }



    public String toString() {
        return String.format("[Name: '%s' Email: '%s' Username: %s Password: %s]", this.getName(),
                this.getEmail(), this.getUsername(), this.getPassword());
    }

    public static Finder<String, User> find = new Finder<String,User>(User.class);


    private static List<User> allUsers = new ArrayList<>();


/**
    public static User makeInstance(userdata formData) {
        val connection = DB.getConnection("team18");
        User user = new User();
        user.name = formData.name;
        user.password = formData.password;
        user.email = formData.email;
        user.username = formData.username;
        // create a Statement from the connection
        try {
            getConnection("users").executeUpdate("INSERT INTO team18.users " + "VALUES (, user.password, user.name, user.email, user.username, false, 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
*/
}
