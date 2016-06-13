package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.*;
import java.util.*;

@Entity
//@Table(name="users")
public class User extends Model {
    @Id
    public String email;
    @Constraints.Required
    public String name;
    public String password;
    public String username;
    /*public int account_locked;
    public int login_attempts;*/

    /*public User(String email, String name, String password, String username) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.username = username;
    }*/

    /*public static Finder<String,User> find = new Finder<String,User>(
            String.class, User.class
    );*/

    public static Finder<String, User> find = new Finder<String,User>(User.class);

    //public static final Find<String, User> find = new Find<String,User>(){};
}
