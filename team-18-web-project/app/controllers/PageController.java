package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Nathan Cheek, Pablo Ortega, Hasan Qadri, Nick Yokley
 * @version 0.5
 * This controller handles normal HTTP GET page requests
 */
public class PageController extends Controller {

    /**
     * Display home page
     * @return HTTP response to home page request
     */
    public Result home() {
        if (session("username") != null) {
            // Redirect all logged in users to /sales
            return redirect("/sales");
        }
        return ok(home.render());
    }

    /**
     * Display about page
     * @return HTTP response to about page request
     */
    public Result about() {
        if (session("username") != null) {
            // Redirect all logged in users to /
            return redirect("/");
        }
        return ok(about.render());
    }

    /**
     * Display addItem page
     * @return HTTP response to addItem page request
     */
    @Authenticated(Secured.class)
    public Result addItem(int saleId) {
        Sale s = Sale.findById(saleId);
        User u = User.findByUsername(session("username"));
        if (s != null && u != null && u.canBeSeller(saleId)) {
            // Check if sale exists and user can act as seller
            if (s.status == 2) {
                // If sale archived, redirect back to sale page
                return redirect("/sale/" + s.id);
            }
            return ok(addItem.render(saleId));
        }
        return notFound404();
    }

    /**
     * Display transaction page
     * @return HTTP response to transaction page request
     */
    @Authenticated(Secured.class)
    public Result confirmTransaction(int saleId, int tranId) {
        Sale s = Sale.findById(saleId);
        Transaction t = Transaction.findById(tranId);
        User u = User.findByUsername(session("username"));
        if (s != null && t != null && t.completed == 0 && u != null
                && u.canBeSeller(saleId)) {
            // Check if sale exists, and transaction exists
            // and is not completed, and user exists and can be seller
            return ok(confirmTransaction.render(saleId, tranId, ""));
        }
        return notFound404();
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
        User u = User.findByUsername(session("username"));
        if (s != null && u != null && u.canBeAdmin(s.id)) {
            // If user is a sale administrator, show edit sale page
            if (s.status == 2) {
                return redirect("/sale/" + s.id);
            }
            // If sale archived, redirect back to sale page
            return ok(editSale.render(s, s.getRoles()));
        }
        return notFound404();
    }

    /**
     * Display Features page
     * @return HTTP response to features page request
     */
    @Authenticated(Secured.class)
    public Result features() {
        return ok(features.render());
    }

    /**
     * Display financial report page
     * @return HTTP response to financial report page request
     */
    @Authenticated(Secured.class)
    public Result financialReport(int saleId) {
        Sale s = Sale.findById(saleId);
        String donor = s.donor;
        User u = User.findByUsername(session("username"));
        if (s != null && u != null && u.canBeBookkeeper(s.id)) {
            // If user has bookkeeper permissions, show page
            List<Transaction> transactions =
                    Transaction.findCompletedBySaleId(saleId);
            float total = 0;
            for (Transaction t : transactions) {
                total += t.getTotal();
            }
            return ok(financialReport.render(transactions, saleId,
                    String.format("%.2f", total), donor));
        }
        return notFound404();
    }

    /**
     * Display financial report page in grouped by seller format
     * @return HTTP response to financial report page request
     */
    @Authenticated(Secured.class)
    public Result financialReportBySeller(int saleId) {
        Sale s = Sale.findById(saleId);
        User u = User.findByUsername(session("username"));

        // If sale exists and user exists and has bookkeeper permissions
        if (s != null && u != null && u.canBeBookkeeper(s.id)) {
            Map<User,List<LineItem>> items = LineItem.getLineItemsBySeller(s);
            return ok(financialReportBySeller.render(items, saleId));
        }
        return notFound404();
    }

    /**
     * Display financial report page for a single seller
     * @return HTTP response to financial report page request
     */
    @Authenticated(Secured.class)
    public Result financialReportSingleSeller(int saleId) {
        Sale s = Sale.findById(saleId);
        User u = User.findByUsername(session("username"));

        // If sale exists and user exists and has seller permissions
        if (s != null && u != null && u.canBeSeller(s.id)) {
            return ok(financialReportSingleSeller.render(
                    LineItem.findBySaleIdUserCreatedId(s.id, u.id), s.id));
        }
        return notFound404();
    }

    /**
     * Display help page
     * @return HTTP response to help page request
     */
    @Authenticated(Secured.class)
    public Result help() {
        return ok(help.render());
    }

    /**
     * Display item page
     * @return HTTP response to item page request
     */
    @Authenticated(Secured.class)
    public Result item(int saleId, int itemId) {
        User u = User.findByUsername(session("username"));
        Sale s = Sale.findById(saleId);
        SaleItem i = SaleItem.findById(itemId);
        if (u != null && s != null & i != null && (s.status == 1
                || Role.findByIds(u.id, saleId) != null)) {
            return ok(item.render(u, s, i));
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
        User u = User.findByUsername(session("username"));
        SaleItem i = SaleItem.findById(itemId);
        if (i != null && u != null && u.canBeClerk(saleId)) {
            // Check if sale exists and user has clerk permissions
            return ok(itemTag.render(i, saleId));
        }
        return notFound404();
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
     * Get page that redirects to mobile scanner page
     * Otherwise, QR code reader would scan each item twice
     * @param itemId Id of Item
     * @return HTTP response to mobile scan redirect page
     */
    public Result mobileScanRedirect(int itemId) {
        return ok(mobileScanRedirect.render("/mobileScan/" + itemId));
    }

    /**
     * Display list of user's sales
     * @return HTTP response to My Sales page request
     */
    @Authenticated(Secured.class)
    public Result mysales() {
        User u = User.findByUsername(session("username"));
        List<Role> roles = Role.findByUserId(u.id);
        List<Sale> sales = new ArrayList();
        for (Role r : roles) {
            sales.add(Sale.findById(r.saleId));
        }
        return ok(mysales.render(sales));
    }

    /**
     * Displays a 404 error page
     * @return HTTP response to a non existent page
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
     * @return HTTP response to a non existent page
     */
    public Result notFound404(String path) {
        return notFound404();
    }

    /**
     * Display profile page for user
     * @param userId Id of User to display
     * @return HTTP response to profile page request
     */
    @Authenticated(Secured.class)
    public Result profile(int userId) {
        User u = User.findByUsername(session("username"));
        User user = User.findById(userId);
        if (u == null) {
            return redirect("/logout");
        }
        // Display edit profile page if viewing own profile or is a superadmin
        if (u.id == user.id || u.isSuperAdmin()) {
            return ok(profileEdit.render(user, ""));
        }
        if (user != null) {
            return ok(profile.render(user));
        }
        return notFound404();
    }

    /**
     * Redirect to profile page for current user
     * @return HTTP redirect to profile page
     */
    @Authenticated(Secured.class)
    public Result profileRedirect() {
        User u = User.findByUsername(session("username"));
        if (u == null) {
            return redirect("/logout");
        }
        return redirect("/profile/" + u.id);
    }

    /**
     * Display registration page
     * @return HTTP response to registration page request
     */
    public Result register() {
        if (session("username") != null) {
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
        if (s != null && u != null) { // Check if sale and user exist
            // If sale is open to public, or user has a role on the sale
            // or user is Super Admin
            if (s.status == 1 || (Role.findByIds(u.id, s.id) != null)
                    || u.superAdmin == 1) {
                return ok(sale.render(u, s, s.getItems()));
            }
        }
        return notFound404();
    }

    /**
     * Display list of sales
     * @return HTTP response to sales page request
     */
    @Authenticated(Secured.class)
    public Result sales() {
        return ok(sales.render(Sale.findAllOpen()));
    }

    /**
     * Display a sale items tags page
     * @return HTTP response to tag page request
     */
    @Authenticated(Secured.class)
    public Result saleTag(int saleId) {
        User u = User.findByUsername(session("username"));
        if (u != null && u.canBeClerk(saleId)) {
            return ok(saleTag.render(SaleItem.findBySaleId(saleId), saleId));
        }
        return notFound404();
    }

    /**
     * Displays transaction page
     * @return HTTP response to transaction page request
     */
    @Authenticated(Secured.class)
    public Result transaction(int saleId, int tranId) {
        Sale s = Sale.findById(saleId);
        Transaction t = Transaction.findById(tranId);
        User u = User.findByUsername(session("username"));
        if (s != null && s.status == 1 && t != null && t.completed == 0
                && u != null && u.canBeSeller(saleId)) {
            // Check if sale exists, and transaction exists
            // and is not completed, and user exists and can be seller
            return ok(transaction.render(t, t.getLineItems(), ""));
        }
        return notFound404();
    }

    /**
     * Displays transaction receipt
     * @return HTTP response to transaction receipt page request
     */
    @Authenticated(Secured.class)
    public Result transactionReceipt(int saleId, int tranId) {
        Sale s = Sale.findById(saleId);
        Transaction t = Transaction.findById(tranId);
        User u = User.findByUsername(session("username"));
        if (s != null && s.status == 1 && t != null && t.completed == 1
                && u != null && u.canBeSeller(saleId)) {
            // Check if sale exists, and transaction exists and is completed,
            // and user exists and can be seller
            return ok(transactionReceipt.render(t, t.getLineItems(), saleId,
                    tranId, ""));
        }
        return notFound404();
    }


    /**
     * Displays a list of all users
     * @return HTTP response to users list request
     */
    @Authenticated(Secured.class)
    public Result users() {
        User u = User.findByUsername(session("username"));
        if (u != null && u.isSuperAdmin()) { // Show supersecret admin page
            return ok(admin.render(User.findAll()));
        } else { // Show normal user page
            return ok(users.render(User.findAll()));
        }
    }
}
