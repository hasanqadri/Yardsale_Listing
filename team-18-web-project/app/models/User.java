package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import java.util.ArrayList;
import java.util.List;
import play.data.format.*;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;


@Entity
@Table(name="users")
public class User extends Model {
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

    public static User findByUsername(String username) {
        return Ebean.find(User.class).where().eq("username", username).findUnique();
    }

    public static User findById(int id) {
        return Ebean.find(User.class).where().eq("id", id).findUnique();
    }

}
