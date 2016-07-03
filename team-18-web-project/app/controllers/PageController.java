package controllers;

import com.avaje.ebean.Ebean;
import models.Role;
import models.Sale;
import models.SaleItem;
import models.User;
import models.Transaction;
import models.LineItem;
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
     * Displays addItem page
     * @return HTTP response to addItem page request
     */
    @Authenticated(Secured.class)
    public Result addItem(int saleId) {
        Sale s = Sale.findById(saleId);
        User u = User.findByUsername(session("username"));
        if (s != null && u.canBeSeller(saleId)) { // Check if sale exists and user can act as seller
            return ok(addItem.render(saleId));
        }
        return notFound404();
    }

    /**
     * Displays admin console
     * @return HTTP response to admin console page request
     */
    @Authenticated(Secured.class)
    public Result admin() {
        User u = User.findByUsername(session("username"));
        if (u.isSuperAdmin()) { // Show supersecret admin page
            return ok(admin.render(User.findAll()));
        } else { // Return 404
            return notFound404();
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
     * Display edit sale page
     * @return HTTP response to edit sale page request
     */
    @Authenticated(Secured.class)
    public Result editSale(int saleId) {
        Sale s = Sale.findById(saleId);
        if (s == null) { // A user might request a url of a sale that doesn't exist
            return notFound404();
        }
        User u = User.findByUsername(session("username"));
        if (u.canBeAdmin(s.id)) { // If user is a sale administrator, show edit sale page
            return ok(editSale.render(s,  s.getRoles()));
        }
        return redirect("/sale/" + s.id);
    }

    /**
     * Display item page
     * @return HTTP response to item page request
     */
    @Authenticated(Secured.class)
    public Result item(int saleId, int itemId) {
        SaleItem i = SaleItem.findById(itemId);
        return ok(item.render(i));
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
     * Displays a 404 error page
     * @param path URI of the page that doesn't exist
     * @return HTTP response to a nonexistant page
     */
    public Result notFound404(String path) {
        return notFound404();
    }

    /**
     * Display profile page for user
     * @return HTTP response to profile page request
     */
    @Authenticated(Secured.class)
    public Result profile() {
        User user = User.findByUsername(session("username"));
        if (user == null) {
            return notFound404();
        }
        return ok(profile.render(user, ""));
    }

    /**
     * Display registration page
     * @return HTTP response to registration page request
     */
    public Result register() {
        String username = session("username");
        if (username != null) {
            return redirect("/");
        }
        return ok(register.render(""));
    }

    /**
     * Display a sale page
     * @param id Id of sale to display
     * @return HTTP response to sale page request
     */
    @Authenticated(Secured.class)
    public Result sale(int id) {
        Sale s = Sale.findById(id);
        User u = User.findByUsername(session("username"));
        if (s != null) { // Check if sale exists
            return ok(sale.render(u, s, s.getItems()));
        }
        return notFound404();
    }

    /**
     * Display a Tag page
     * @param saleId Id of item to display
     * @param itemId Id of item to display
     * @return HTTP response to tag page request
     */
    @Authenticated(Secured.class)
    public Result itemTag(int saleId, int itemId) {
        SaleItem s = Ebean.find(SaleItem.class).where().eq("id", itemId).findUnique();
        if (s != null) { // Check if sale exists
            return ok(itemTag.render(saleId, itemId));
        }
        return notFound404();
    }

    /**
     * Display a sale items tags page
     * @return HTTP response to tag page request
     */
    @Authenticated(Secured.class)
    public Result saleTag(int saleId) {
            return ok(saleTag.render(saleId));
    }




    /**
     * Display list of sales
     * @return HTTP response to sales page request
     */
    @Authenticated(Secured.class)
    public Result sales() {
        return ok(sales.render());
    }


    /**
     * Display a reciept page
     * @param saleId Id of item to display
     * @param tranId Id of transaction to display
     * @return HTTP response to tag page request
     */
    @Authenticated(Secured.class)
    public Result buy(int saleId, int tranId) {
            return ok(buy.render(saleId, tranId));
    }
    /**
     * Displays transaction page
     * @return HTTP response to addItem page request
     */
    @Authenticated(Secured.class)
    public Result transaction(int saleId, int tranId) {
        Sale s = Ebean.find(Sale.class).where().eq("id", saleId).findUnique();
        if (s != null) { //check if transaction exists
            return ok(transaction.render(saleId, tranId, ""));
        }
        return notFound404();
    }
    /**
     * Displays a list of all users
     * @return HTTP response to users list request
     */
    @Authenticated(Secured.class)
    public Result users() {
        return ok(users.render( User.findAll()));
    }
}