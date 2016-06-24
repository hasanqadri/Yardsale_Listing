package models;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="usersNcheek3")
public class User extends Model {
    @Id
    public int id;
    @Constraints.Required
    public String email;
    @Constraints.Required
    public String name;
    @Constraints.Required
    public String password;
    @Constraints.Required
    public String username;
    public int loginAttempts;
    @Column(columnDefinition = "integer default 1")
    public int

    public User(String name, String email, String username, String password) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public static Finder<String, User> find = new Finder<String,User>(User.class);

    public String toString() {
        return String.format("[Name: '%s' Email: '%s' Username: %s Password: %s]", name,
                email, username, password);
    }


    //private static List<User> allUsers = new ArrayList<>();


}
