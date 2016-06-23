package models;

import com.avaje.ebean.Model;
import play.data.format.*;
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
    public int id;
    @Constraints.Required
    public String email;
    @Constraints.Required
    public String first_name;
    @Constraints.Required
    public String last_name;
    @Constraints.Required
    public String password;
    @Constraints.Required
    public String username;
    //@OneToMany(mappedBy = "user")
    public List<YardSale> yardSales = new ArrayList<>();
    public int account_locked;
    public int login_attempts;
    //public boolean loggedin;

    public User(String first_name,String last_name, String email, String username, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }
    public String getLast_name(){
        return last_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public void setLast_name(String last_name){
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /*public boolean getLoggedin() {
        return loggedin;
    }

    public void setLoggedin(boolean b) {
        this.loggedin = b;
    }*/



    public static Finder<String, User> find = new Finder<String,User>(User.class);

    public String toString() {
        return String.format("[Name: '%s' Email: '%s' Username: %s Password: %s]", first_name + " " + last_name,
                email, username, password);
    }


    //private static List<User> allUsers = new ArrayList<>();


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
