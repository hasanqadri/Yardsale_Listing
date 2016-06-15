package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.*;
import java.util.*;

@Entity
@Table(name="users")
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
    public int account_locked;
    public int login_attempts;

    /*public User(String email, String name, String password, String username) { //This is probably wrong
        this.email = email;
        this.name = name;
        this.password = password;
        this.username = username;
    }*/

    public static Finder<String, User> find = new Finder<String,User>(User.class);
}
