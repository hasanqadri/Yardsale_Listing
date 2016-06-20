package views.formdata;

import play.data.validation.Constraints;

/**
 * Created by Hasan on 6/16/2016.
 * provides the backing class for the registration form
 */

public class userdata {
    public userdata() {

    }
    public userdata(String first_name,String last_name, String email, String username, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    //@Id
    //public int id = 0;
    @Constraints.Required
    public String email = "";
    @Constraints.Required
    public String first_name = "";
    @Constraints.Required
    public String last_name = " ";
    @Constraints.Required
    public String password = "";
    @Constraints.Required
    public String username = "";
    public boolean account_locked = false;
    public int login_attempts = 0;


}
