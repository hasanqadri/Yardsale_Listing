package controllers;

import com.avaje.ebean.Model;
import models.User;
import models.Sale;
import play.data.DynamicForm;
import play.data.Form;

import com.avaje.ebean.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.formdata.userdata;
import views.html.*;

import java.util.List;

import static play.libs.Json.toJson;


/**
 * @author Nathan Cheek, Pablo Ortega, Hasan Qadri, Nick Yokley
 * @version 0.4
 * This controller contains an action to handle HTTP requests
 * to the application.
 */
public class ApplicationController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * @return HTTP response to home page request
     */
    public Result home() {
        if (session("username") != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/profile");
        }
        return ok(home.render());
    }

    /**
     * Renders about page
     * @return HTTP response to about page request
     */
    public Result about() {
        if (session("username") != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/profile");
        }
        return ok(about.render());
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
     * Page after registering, adds form data to user json
     * @return HTTP response after successful user registration
     */
    public Result postContact() {
        Form<userdata> formdata = Form.form(userdata.class).bindFromRequest();
        userdata data = formdata.get();
        User user = new User(data.firstName, data.lastName, data.email, data.username, data.password);
        user.save();
        return ok(postContact.render(data.firstName + " " + data.lastName));
    }

    /**
     * Display login page and handle login requests
     * @return Login page or response to login request
     */
    public Result login() {
        if (request().method() == "POST") { // login request
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            User user = User.find.where().eq("username", dynamicForm.get("username")).findUnique();
            if (user != null) { // User exists
                if (user.getPassword().equals(dynamicForm.get("password"))) { // Correct password
                    if (user.loginAttempts == 3) { // Notify that user is locked out
                        return ok(login.render("Your account has been locked"));
                    } else { // Create session
                        if (user.loginAttempts != 0) { // Reset loginAttempts if != 0
                            user.loginAttempts = 0;
                            user.save();
                        }
                        session("username", dynamicForm.get("username"));
                        return redirect("/profile");
                    }
                } else if (user.loginAttempts < 3) { // Incorrect password - increment lockout counter up to 3
                    user.loginAttempts++;
                    user.save();
                }
            }
            return ok(login.render("Login failed"));
        } else { // load login page
            if (session("username") != null) {
                return redirect("/");
            }
            return ok(login.render(""));
        }
    }

    /**
     * Display registration page and handle registration requests
     * @return HTTP response to registration page request or registration request
     */
    public Result register() {
        String username = session("username");
        if (username != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/profile");
        }
        if (request().method() == "POST") {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            User userCheckUsername = User.find.where().eq("username", dynamicForm.get("username")).findUnique();
            if (userCheckUsername != null) {
                return ok(register.render("Error: username already in use"));
            }
            User userCheckEmail = User.find.where().eq("email", dynamicForm.get("email")).findUnique();
            if (userCheckEmail != null) {
                return ok(register.render("Error: email already in use"));
            }
            //If username or email not already in use, create user
            User user = new User(dynamicForm.get("firstName"), dynamicForm.get("lastName"), dynamicForm.get("email"), dynamicForm.get("username"), dynamicForm.get("password"));
            user.save();
            return ok(postContact.render(dynamicForm.get("firstName") + " " + dynamicForm.get("lastName")));
        }
        return ok(register.render(""));
    }

    /**
     * Display profile page for user
     * @return HTTP response to profile page request
     */
    @Authenticated(Secured.class)
    public Result profile() {
        String username = session("username");
        User user = User.find.where().eq("username", username).findUnique();

        if (request().method() == "POST") {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            User userCheckUsername = User.find.where().eq("username", dynamicForm.get("username")).findUnique();
            if (userCheckUsername != null && !dynamicForm.get("username").equals(username)) {
                return ok(profile.render(username, user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail(),
                        "Error: username already in use"));
            }
            User userCheckEmail = User.find.where().eq("email", dynamicForm.get("email")).findUnique();
            if (userCheckEmail != null && !dynamicForm.get("email").equals(user.getEmail())) {
                return ok(profile.render(username, user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail(),
                        "Error: email already in use"));

            }
            //If username or email not already in use, update user
            user.setFirstName(dynamicForm.get("firstName"));
            user.setLastName(dynamicForm.get("lastName"));
            user.setEmail(dynamicForm.get("email"));
            user.setUsername(dynamicForm.get("username"));
            user.setPassword(dynamicForm.get("password"));
            user.save();
            session("username", dynamicForm.get("username"));

        }
        return ok(profile.render(username, user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail(), ""));

    }

    @Authenticated(Secured.class)
    public Result getSales() {
        List<Sale> sales = new Model.Finder<>(String.class, Sale.class).all(); // Alternative way of doing it
        //List<Sale> sales = Sale.find.all();
        return ok(toJson(sales));
    }

    //creates the sale
    @Authenticated(Secured.class)
    public Result createSale() {

        //enters sale into database
        if (request().method() == "POST") {

            DynamicForm createSaleForm = Form.form().bindFromRequest();
            Sale sale = new Sale();
            sale.name = createSaleForm.get("name");
            sale.street = createSaleForm.get("street");
            sale.city = createSaleForm.get("city");
            sale.state = createSaleForm.get("state");
            sale.zip = Integer.parseInt(createSaleForm.get("zip"));
            sale.startDate = Double.parseDouble(createSaleForm.get("startDate"));
            sale.endDate = Double.parseDouble(createSaleForm.get("endDate"));
            sale.save();
            return ok(createSale.render("Sale Created! Create Another Sale?"));
        } else {
            return ok(createSale.render(""));
        }
    }

    @Authenticated(Secured.class)
    public Result sale() {
        return ok(sale.render());
    }

    @Authenticated(Secured.class)
    public Result searchLocations() {
        if (request().method() == "POST") {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            String query = dynamicForm.get("query");
            if (query != null) {
                return ok(searchLocations.render(query));
            }
        }
        return redirect("/");
    }

    @Authenticated(Secured.class)
    public Result getSearchLocations() {
        if (request().method() == "POST") {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            String query = dynamicForm.get("query");
            if (query != null) {
                List<Sale> sales = new Model.Finder<>(String.class, Sale.class).where().like("city", query).findList();
                return ok(toJson(sales));
            }
        }
        return notFound404("/getSearchLocations");
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
     * Lists complete json data of all users
     * @return JSON response of all user data stored in database
     */
    @Authenticated(Secured.class)
    public Result getUsers() {
        //todo make this only return some data
        //List<User> users = new Model.Finder<>(String.class, User.class).all(); // Alternative way of doing it
        List<User> users = User.find.all();
        return ok(toJson(users));
    }

    /**
     * Displays a list of all users
     * @return HTTP response to users list request
     */
    @Authenticated(Secured.class)
    public Result users() {
        return ok(users.render());
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
     * Lists complete json data of all users
     * @return JSON response of all user data stored in database
     */
    @Authenticated(Secured.class)
    public Result adminGetUsers() {
        User user = User.find.where().eq("username", session("username")).findUnique();
        if (user.superAdmin == 1) { // Show supersecret user data
            List<User> users = new Model.Finder<>(String.class, User.class).all();
            return ok(toJson(users));
        } else { // Return 404
            return notFound404("/adminGetUsers");
        }
    }

    /**
     * Unlocks a user account
     * @return nothing
     */
    @Authenticated(Secured.class)
    public Result adminResetUser() {
        User user = User.find.where().eq("username", session("username")).findUnique();
        if (user.superAdmin == 1 && request().method() == "POST") { // Requestor is super admin and is sending post req
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            if (dynamicForm.get("username") != null) { // Username was sent in post request
                User userReset = User.find.where().eq("username", dynamicForm.get("username")).findUnique();
                userReset.loginAttempts = 0;
                userReset.save();
                return ok();
            }
        }
        return notFound404("/adminResetUser");
    }

    @Authenticated(Secured.class)
    public Result getImage(String id) {
        //Picture picture = User.find.where().eq("id", dynamicForm.get("username")).findUnique();
        Sale sale = new Sale();
        sale.city = "Atlanta";
        sale.state = "GA";
        sale.userCreatedId = 123;
        sale.save();
        //Sale sale2 = Sale.find.where().eq("id", 1).findUnique();
        List<Sale> sales = new Model.Finder<>(String.class, Sale.class).all();
        return ok(toJson(sales));

        //return notFound(notFound.render());
    }
}
