package controllers;

import com.avaje.ebean.Ebean;
import java.sql.Timestamp;
import java.text.*;
import java.util.Date;
import models.Sale;
import models.SaleItem;
import models.User;
import models.Transaction;
import models.LineItem;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.formdata.userdata;
import views.html.*;

/**
 * Created by nathancheek on 6/25/16.
 */
public class ActionController extends Controller {
    /**
     * @param id place in db
     * @return
     */
    @Authenticated(Secured.class)
    public Result addItem(int id) {
        Sale s = Sale.findById(id);
        if (s != null) { // Check if sale exists
            DynamicForm f = Form.form().bindFromRequest();
            if (f.get("name") == null || f.get("description") == null || f.get("price") == null) { // Improper request
                return notFound404();
            }
            s.addItem(f.get("name"), f.get("description"), f.get("price"), 0, 0);
            return redirect("/sale/" + id);
        }
        return notFound404();
    }

    /**
     * Unlocks a user account
     * @return Response to user account request
     */
    @Authenticated(Secured.class)
    public Result admin() {
        User u = User.findByUsername(session("username"));
        if (u.superAdmin == 1) { // Requestor is super admin
            DynamicForm f = Form.form().bindFromRequest();
            if (f.get("userId") != null) { // Username was sent in post request
                User userReset = User.findById(f.get("userId"));
                userReset.setLoginAttempts(0);
                userReset.save();
                return ok(admin.render(User.findAll()));
            }
        }
        return notFound404();
    }

    /**
     * Creates a sale
     * @return HTTP response to create sale request
     */
    @Authenticated(Secured.class)
    public Result createSale() {
        User user = User.findByUsername(session("username"));
        DynamicForm f = Form.form().bindFromRequest();
        int zip;
        Timestamp startDate;
        Timestamp endDate;
        try {
            zip = Integer.parseInt(f.get("zip"));
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date startingDate = dateFormat.parse(f.get("startDate"));
            long startTime = startingDate.getTime();
            startDate = new Timestamp(startTime);

            Date endingDate = dateFormat.parse(f.get("endDate"));
            long endTime = endingDate.getTime();
            endDate = new Timestamp(endTime);
        } catch(ParseException|NumberFormatException e) {
            e.printStackTrace();
            return notFound404();
        }
        Sale sale = new Sale(f.get("name"), f.get("description"), f.get("street"), f.get("city"), f.get("state"),
                zip, startDate, endDate, user.id);
        return redirect("/sale/" + sale.id);
    }

    @Authenticated(Secured.class)
    public Result editSale(int saleId) {
        Sale s = Sale.findById(saleId);
        DynamicForm f = Form.form().bindFromRequest();
        //todo finish this
        return ok(editSale.render(s, s.getRoles()));
    }

    /**
     * Update item page
     * @return HTTP response to item page update request
     */
    @Authenticated(Secured.class)
    public Result item(int saleId, int itemId) {
        DynamicForm f = Form.form().bindFromRequest();
        if (f.get("name") == null || f.get("description") == null || f.get("price") == null) {
            return notFound404();
        }
        SaleItem i = SaleItem.findById(itemId);
        float price;
        try {
            price = Float.parseFloat(f.get("price"));
        } catch (NumberFormatException e) {
            return notFound404();
        }

        i.setName(f.get("name"));
        i.setDescription(f.get("description"));
        i.setPrice(price);
        i.save();

        return ok(item.render(i));
    }

    /**
     * Handle login requests
     * @return Response to login request
     */
    public Result login() {
        if (session("username") != null) { // Redirect if user already logged in
            return redirect("/");
        }
        DynamicForm f = Form.form().bindFromRequest();
        User user = User.findByUsername(f.get("username"));
        if (user != null) { // User exists
            if (user.getPassword().equals(f.get("password"))) { // Correct password
                if (user.loginAttempts == 3) { // Notify that user is locked out
                    return ok(login.render("Your account has been locked"));
                } else { // Create session
                    if (user.loginAttempts != 0) { // Reset loginAttempts if != 0
                        user.setLoginAttempts(0);
                        user.save();
                    }
                    session("username", f.get("username"));
                    return redirect("/");
                }
            } else if (user.loginAttempts < 3) { // Incorrect password - increment lockout counter up to 3
                user.setLoginAttempts(user.loginAttempts + 1);
                user.save();
            }
        }
        return ok(login.render("Login failed"));
    }

    /**
     * Displays a 404 error page
     * @return HTTP response to a nonexistant page
     */
    public Result notFound404() {
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
        return ok(postContact.render(user));
    }

    /**
     * Update profile page for user
     * @return HTTP response to profile page update request
     */
    @Authenticated(Secured.class)
    public Result profile() {
        String username = session("username");
        User user = User.findByUsername(session("username"));

        DynamicForm f = Form.form().bindFromRequest();
        User userCheckUsername = User.findByUsername(f.get("username"));
        if (userCheckUsername != null && !f.get("username").equals(username)) {
            return ok(profile.render(user, "Error: username already in use"));
        }
        User userCheckEmail = User.findByEmail(f.get("email"));
        if (userCheckEmail != null && !f.get("email").equals(user.getEmail())) {
            return ok(profile.render(user, "Error: email already in use"));

        }
        // Username and email not already in use, update user
        user.setFirstName(f.get("firstName"));
        user.setLastName(f.get("lastName"));
        user.setEmail(f.get("email"));
        user.setUsername(f.get("username"));
        user.setPassword(f.get("password"));
        user.save();
        session("username", f.get("username")); //Set new cookie in case username was changed

        return ok(profile.render(user, ""));
    }

    /**
     * Handle registration requests
     * @return HTTP response to registration request
     */
    public Result register() {
        if (session("username") != null) { // Redirect if user already logged in
            return redirect("/");
        }
        DynamicForm f = Form.form().bindFromRequest();
        User userCheckUsername = User.findByUsername(f.get("username"));
        if (userCheckUsername != null) {
            return ok(register.render("Error: username already in use"));
        }
        User userCheckEmail = User.findByEmail(f.get("email"));
        if (userCheckEmail != null) {
            return ok(register.render("Error: email already in use"));
        }
        //If username or email not already in use, create user
        User user = new User(f.get("firstName"), f.get("lastName"), f.get("email"), f.get("username"), f.get("password"));
        user.save();
        return ok(postContact.render(user));
    }

    /**
     * Search items in a sale
     * @param saleId Id of sale to search
     * @return HTTP response to search results request
     */
    @Authenticated(Secured.class)
    public Result searchItems(int saleId) {
        DynamicForm f = Form.form().bindFromRequest();
        String query = f.get("query");
        if (query != null) {
            return ok(searchItems.render(saleId, query));
        }
        return notFound404();
    }

    /**
     * Search sales
     * @return HTTP response to search results request
     */
    @Authenticated(Secured.class)
    public Result searchSales() {
        DynamicForm f = Form.form().bindFromRequest();
        String query = f.get("query");
        if (query != null) {
            return ok(searchSales.render(query));
        }
        return notFound404();
    }

    /**
     * @param id place in db
     * @return
     */
    @Authenticated(Secured.class)
    public Result transaction(int saleID) {
        Sale s = Ebean.find(Sale.class).where().eq("id", saleID).findUnique();
        if (s != null) { // Check if sale exists


        }
        return notFound404();
    }

    /**
     * @param id place in db
     * @return
     */
    @Authenticated(Secured.class)
    public Result addLineItem(int saleID, int transactionID, int lineItemID, int saleItemID) {
        Sale s = Ebean.find(Sale.class).where().eq("id", saleID).findUnique();
        if (s != null) { //check if transaction exists
            Transaction t = Ebean.find(Transaction.class).where().eq("id", transactionID).findUnique();
            if (t != null) {

                LineItem item = new LineItem(lineItemID, saleItemID, transactionID, 1);
                item.save();
                return ok(transaction.render(transactionID, "Item added! Add another item?"));
            }
        }
        return notFound404();
    }
    @Authenticated(Secured.class)
    public Result uploadProfilePicture() {
        MultipartFormData body = request().body().asMultipartFormData();
        /*FilePart picture = body.getFile("picture");
        if (picture != null) {
            picture.getFile();
            return ok("Picture uploaded");
        } else {
            redirect("/profile");
        }*/
        User user = Ebean.find(User.class).where().eq("username", session("username")).findUnique();
        return ok("in progress");
    }
}
