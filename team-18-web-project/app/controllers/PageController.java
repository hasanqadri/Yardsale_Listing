package controllers;

import com.avaje.ebean.Ebean;
import java.util.List;
import models.Sale;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.formdata.userdata;
import views.html.*;

/**
 * @author Nathan Cheek, Pablo Ortega, Hasan Qadri, Nick Yokley
 * @version 0.5
 * This controller handles normal HTTP GET page requests
 */
public class PageController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * @return HTTP response to home page request
     */
    public Result home() {
        if (session("username") != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/sales");
        }
        return ok(home.render());
    }

    /**
     * Renders about page
     * @return HTTP response to about page request
     */
    public Result about() {
        if (session("username") != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/");
        }
        return ok(about.render());
    }

    /**
     * Displays admin console
     * @return HTTP response to admin console page request
     */
    @Authenticated(Secured.class)
    public Result admin() {
        User user = User.find.where().eq("username", session("username")).findUnique();
        if (user.superAdmin == 1) { // Show supersecret admin page
            return ok(admin.render());
        } else { // Return 404
            return notFound404("/admin");
        }
    }

    /**
     * Display create sale page
     * @return HTTP response to create sale page request
     */
    @Authenticated(Secured.class)
    public Result createSale() {
        return ok(createSale.render(""));
    }

    /**
     * Display login page
     * @return Login page
     */
    public Result login() {
        if (session("username") != null) {
            return redirect("/");
        }
        return ok(login.render(""));
    }

    /**
     * Log out the user by deleting session cookies
     * @return HTTP redirection response to home page
     */
    public Result logout() {
        session().clear();
        return redirect("/");
    }

    /**
     * Registration page that takes in the form info and then send the user to postContact
     * @return HTTP response to newContact request
     */
    public Result newContact() {
        Form<userdata> formdata = Form.form(userdata.class);
        return ok(NewContact.render(formdata));
    }

    /**
     * Displays a 404 error page
     * @param path URI of the page that doesn't exist
     * @return HTTP response to a nonexistant page
     */
    public Result notFound404(String path) {
        if (session("username") != null) {
            return notFound(loggedinNotFound.render());
        } else {
            return notFound(notFound.render());
        }
    }

    /**
     * Display profile page for user
     * @return HTTP response to profile page request
     */
    @Authenticated(Secured.class)
    public Result profile() {
        String username = session("username");
        User user = User.find.where().eq("username", username).findUnique();
        return ok(profile.render(username, user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail(), ""));
    }

    /**
     * Display registration page
     * @return HTTP response to registration page request
     */
    public Result register() {
        String username = session("username");
        if (username != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/profile");
        }
        return ok(register.render(""));
    }

    @Authenticated(Secured.class)
    public Result sale(String strId) {
        //return ok(sale.render(id));
        return ok();
    }

    @Authenticated(Secured.class)
    public Result sales() {
        return ok(sales.render());
    }

    /**
     * Displays a list of all users
     * @return HTTP response to users list request
     */
    @Authenticated(Secured.class)
    public Result users() {
        return ok(users.render());
    }
}
