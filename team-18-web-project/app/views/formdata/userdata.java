package views.formdata;

import play.data.validation.Constraints;

/**
 * Created by Hasan on 6/16/2016.
 * provides the backing class for the registration form
 */

public class userdata {
    public userdata() {

    }
    public userdata(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    //@Id
    //public int id = 0;
    @Constraints.Required
    public String email = "";
    @Constraints.Required
    public String name = "";
    @Constraints.Required
    public String password = "";
    @Constraints.Required
    public String username = "";
    public int login_attempts = 0;


}
