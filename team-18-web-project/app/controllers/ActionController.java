package controllers;

import com.avaje.ebean.Ebean;
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
 * Created by nathancheek on 6/25/16.
 */
public class ActionController extends Controller {
    /**
     * Unlocks a user account
     * @return Response to user account request
     */
    @Authenticated(Secured.class)
    public Result adminResetUser() {
        User user = User.find.where().eq("username", session("username")).findUnique();
        if (user.superAdmin == 1) { // Requestor is super admin
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            if (dynamicForm.get("username") != null) { // Username was sent in post request
                User userReset = User.find.where().eq("username", dynamicForm.get("username")).findUnique();
                userReset.loginAttempts = 0;
                userReset.save();
                return noContent(); // Return HTTP code 204
            }
        }
        return notFound404("/adminResetUser");
    }

    /**
     * Creates a sale
     * @return HTTP response to create sale request
     */
    @Authenticated(Secured.class)
    public Result createSale() {
        //enters sale into database
        User user = User.find.where().eq("username", session("username")).findUnique();
        DynamicForm createSaleForm = Form.form().bindFromRequest();
        Sale sale = new Sale();
        sale.name = createSaleForm.get("name");
        sale.street = createSaleForm.get("street");
        sale.city = createSaleForm.get("city");
        sale.state = createSaleForm.get("state");
        sale.zip = Integer.parseInt(createSaleForm.get("zip"));
        sale.startDate = Double.parseDouble(createSaleForm.get("startDate"));
        sale.endDate = Double.parseDouble(createSaleForm.get("endDate"));
        sale.userCreatedId = user.id;
        sale.save();
        return ok(createSale.render("Sale Created! Create Another Sale?"));
    }

    /**
     * Creates a sale item
     * @return HTTP response to create sale item request
     */
    @Authenticated(Secured.class)
    public Result createSaleItem() {
        //todo this should be much like createSale() above
        return ok();
    }

    /**
     * Handle login requests
     * @return Response to login request
     */
    public Result login() {
        if (session("username") != null) { // Redirect if user already logged in
            return redirect("/");
        }
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
                    return redirect("/");
                }
            } else if (user.loginAttempts < 3) { // Incorrect password - increment lockout counter up to 3
                user.loginAttempts++;
                user.save();
            }
        }
        return ok(login.render("Login failed"));
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
     * Update profile page for user
     * @return HTTP response to profile page update request
     */
    @Authenticated(Secured.class)
    public Result profile() {
        String username = session("username");
        User user = User.find.where().eq("username", username).findUnique();

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
        // Username and email not already in use, update user
        user.setFirstName(dynamicForm.get("firstName"));
        user.setLastName(dynamicForm.get("lastName"));
        user.setEmail(dynamicForm.get("email"));
        user.setUsername(dynamicForm.get("username"));
        user.setPassword(dynamicForm.get("password"));
        user.save();
        session("username", dynamicForm.get("username"));

        return ok(profile.render(username, user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail(), ""));

    }

    /**
     * Handle registration requests
     * @return HTTP response to registration request
     */
    public Result register() {
        if (session("username") != null) { // Redirect if user already logged in
            return redirect("/");
        }
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

    /**
     * Update a sale
     * @param strId Id of sale
     * @return
     */
    @Authenticated(Secured.class)
    public Result sale(String strId) {
        int id = Integer.parseInt(strId);
        //todo check if user is authorized for this sale, then update
        return ok();
    }

    @Authenticated(Secured.class)
    public Result searchLocations() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String query = dynamicForm.get("query");
        if (query != null) {
            return ok(searchLocations.render(query));
        }
        return notFound404("/searchLocations");
    }
}
