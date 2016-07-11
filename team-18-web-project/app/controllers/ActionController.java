package controllers;

import com.avaje.ebean.Ebean;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.formdata.userdata;
import views.html.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nathancheek on 6/25/16.
 */
public class ActionController extends Controller {
    /**
     * Add an item to a sale
     * @param saleId Id of sale to add item to
     * @return HTTP response to add item request
     */
    @Authenticated(Secured.class)
    public Result addItem(int saleId) {
        Sale s = Sale.findById(saleId);
        if (s != null) { // Check if sale exists
            DynamicForm f = Form.form().bindFromRequest();
            if (f.get("name") == null || f.get("description") == null || f.get("price") == null || f.get("quantity") == null) { // Improper request
                return notFound404();
            }
            User u = User.findByUsername(session("username"));
            s.addItem(f.get("name"), f.get("description"), f.get("price"), u.id, f.get("quantity"));
            return redirect("/sale/" + saleId);
        }
        return notFound404(); // Return 404 error if sale doesn't exist
    }

    /**
     * Confirms transaction
     * @return HTTP response to confirm transaction request
     */
    @Authenticated(Secured.class)
    public Result confirmTransaction(int saleId, int tranId) {

        User user = User.findByUsername(session("username"));
        DynamicForm f = Form.form().bindFromRequest();

        if (user != null && user.canBeSeller(saleId)) {
            // Handle transaction cancel form
            if (f.get("cancelTransactionId") != null) {
                // Delete line items
                List<LineItem> list = LineItem.findByTransactionId(tranId);
                for (LineItem li : list) {
                    li.delete();
                }
                return redirect("/sale/" + saleId);
            }

            //enters a confirmed transaction updating the buyer info using a form
            if (f.get("confirmTransaction") != null) {
                Sale s = Sale.findById(saleId);
                Transaction t = Transaction.findById(tranId);
                t.setBuyerName(f.get("name"));
                t.setBuyerAddress(f.get("address"));
                t.setBuyerEmail(f.get("email"));
                t.setCompleted(1);
                t.setPaymentMethod(f.get("payment"));
                t.save();

                //update item quantity for sale items
                List<SaleItem> saleItems = s.getItems();
                List<LineItem> lineItems = t.getLineItems();
                for (LineItem li : lineItems) {
                    for (SaleItem si: saleItems) {

                        if (li.getId() == si.getId()) {

                            si.setQuantity(si.getQuantity() - li.getQuantity());
                            si.save();
                        }
                    }
                }
            }
            return redirect("/sale/" + saleId + "/transactionReceipt/" + tranId);
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

    /**
     * Create a transaction
     * @param saleId Id of sale for which to create transaction
     * @return HTTP response to create transaction page request
     */
    @Authenticated(Secured.class)
    public Result createTransaction(int saleId) {
        Sale s = Sale.findById(saleId);
        User u = User.findByUsername(session("username"));
        if (u != null && u.canBeSeller(saleId)) { // User exists and can be a seller
            if (s.status == 2) { return redirect("/sale/" + s.id); } // If sale archived, redirect back to sale page
            Transaction transaction = new Transaction(saleId, u.id);
            return redirect("/sale/" + saleId + "/transaction/" + transaction.id);
        }
        return notFound404();
    }

    /**
     * Update a sale
     * @param saleId Id of sale to edit
     * @return HTTP response to edit sale page update request
     */
    @Authenticated(Secured.class)
    public Result editSale(int saleId) {
        Sale s = Sale.findById(saleId);
        User u = User.findByUsername(session("username"));
        if (!u.canBeAdmin(saleId) || s == null) {
            return notFound404(); // If user is not an administrator for the sale or sale doesn't exist, return 404 error
        }
        if (s.status == 2) {
            return redirect("/sale/" + saleId);
        }
        DynamicForm f = Form.form().bindFromRequest();

        // Handle role creation requests
        if (f.get("addRoleUsername") != null && f.get("addRoleName") != null &&
                Role.validRoles.contains(f.get("addRoleName"))) {
            User ua = User.findByUsername(f.get("addRoleUsername"));
            if (ua != null) {
                Role r = Role.findByIds(ua.id, s.id);
                if (r != null) { // There's already a role for this user; modify it
                    r.setName(f.get("addRoleUsername"));
                    r.save();
                }
                // Create a new role
                Role ra = new Role(f.get("addRoleName"), ua.id, s.id);
            }
            return ok(editSale.render(s, s.getRoles()));
        }

        // Handle role deletion requests
        if (f.get("deleteRoleUserId") != null) {
            int roleDeleteUserId;
            try {
                roleDeleteUserId = Integer.parseInt(f.get("deleteRoleUserId"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return notFound404();
            }
            Role r = Role.findByIds(roleDeleteUserId, s.id);
            if (r != null) {
                r.delete();
            }
            return ok(editSale.render(s,  s.getRoles()));
        }

        // Handle sale info update requests
        if (f.get("name") != null && f.get("description") != null && f.get("street") != null && f.get("city") != null &&
                f.get("state") != null && f.get("zip") != null && f.get("startDate") != null &&
                f.get("endDate") != null) {
            s.setName(f.get("name"));
            s.setDescription(f.get("description"));
            s.setStreet(f.get("street"));
            s.setCity(f.get("city"));
            s.setState(f.get("state"));
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
            } catch (ParseException | NumberFormatException e) {
                e.printStackTrace();
                return notFound404();
            }
            s.setZip(zip);
            s.setStartDate(startDate);
            s.setEndDate(endDate);
            s.save();
            return ok(editSale.render(s, s.getRoles()));
        }

        // Handle sale open requests
        if (f.get("open") != null) {
            s.setStatus(1);
            s.save();
            return ok(editSale.render(s, s.getRoles()));
        }

        // Handle sale close requests
        if (f.get("close") != null) {
            s.setStatus(0);
            s.save();
            return ok(editSale.render(s, s.getRoles()));
        }

        // Handle sale archive requests
        if (f.get("archive") != null) {
            s.setStatus(2);
            s.save();
            return redirect("/sale/" + saleId);
        }

        return notFound404(); // If invalid form return 404 error

    }

    /**
     * Update item page
     * @return HTTP response to item page update request
     */
    @Authenticated(Secured.class)
    public Result item(int saleId, int itemId) {
        User u = User.findByUsername(session("username"));
        Sale s = Sale.findById(saleId);
        SaleItem i = SaleItem.findById(itemId);
        DynamicForm f = Form.form().bindFromRequest();
        if (u != null && u.canBeSeller(saleId) && s != null && s.status != 2 && i != null &&
                f.get("name") != null && f.get("description") != null && f.get("price") != null && f.get("quantity") != null) {
            float price;
            int quantity;
            try {
                price = Float.parseFloat(f.get("price"));
                quantity = Integer.parseInt(f.get("quantity"));
            } catch (NumberFormatException e) {
                return notFound404();
            }

            i.setName(f.get("name"));
            i.setDescription(f.get("description"));
            i.setPrice(price);
            i.setQuantity(quantity);
            i.save();

            return ok(item.render(u, s, i));
        }
        return notFound404();
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
            if (user.getPassword().equals(User.hashPassword(f.get("password")))) { // Correct password
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

    public Result mobileScanRedirect(int itemId) {
        return ok(mobileScanRedirect.render("/mobileScan/" + itemId));
    }

    /**
     * Scan an item in via qr code
     * @param  itemId Id of item
     * @return Success or Failure page
     */
    public Result mobileScan(int itemId) {
        Transaction t = Transaction.findById(session("transactionId"));
        if (t == null || !t.checkNonce(session("transactionNonce"))) {
            return ok(mobileFailure.render("Error: Device not registered to Transaction"));
        }

        if (t.completed == 1) {
            return ok(mobileFailure.render("Error: Transaction completed"));
        }

        // First search to see if item exists and is part of sale
        SaleItem i = SaleItem.findById(itemId);
        if (i == null || i.saleId != t.saleId) {
            return ok(mobileFailure.render("Error: Invalid Item"));
        }

        // Search to see if item is already part of transaction
        LineItem li = LineItem.findByItemIdTransactionId(itemId, t.id);
        if (li != null) { // Item already part of transaction, update quantity
            li.setQuantity(li.quantity + 1);
            li.save();
        } else { // Item not part of transaction, create new LineItem
            LineItem lin = new LineItem(itemId, t.id, 1);
        }

        return ok(mobileSuccess.render("Item added"));
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
        return ok(postContact.render(user));
    }

    /**
     * Register a mobile device to a transaction so it can be used as an item scanner
     * @param saleId Id of Sale
     * @param tranId Id of Transaction
     * @param tranNonce Id of Transaction Nonce
     * @return Success or Failure page
     */
    public Result registerMobileTransaction(int saleId, int tranId, int tranNonce) {
        Sale s = Sale.findById(saleId);
        Transaction t = Transaction.findById(tranId);
        if (s != null && t != null && t.completed == 0 && tranNonce == t.randomNonce) {
            // Save cookie on mobile device so it can scan items into this transaction
            session("transactionNonce", Integer.toString(tranNonce));
            session("transactionId", Integer.toString(tranId));
            return ok(mobileSuccess.render("Registered to transaction " + tranId));
        }
        return ok(mobileFailure.render("Failed to register"));
    }

    /**
     * Handle POST requests to sale page
     * @param saleId Id of sale
     * @return HTTP response to sale page request
     */
    @Authenticated(Secured.class)
    public Result sale(int saleId) {
        User u = User.findByUsername(session("username"));
        Sale s = Sale.findById(saleId);
        DynamicForm f = Form.form().bindFromRequest();
        if (u != null && u.canBeAdmin(saleId) && s != null && f.get("unarchive") != null) {
            s.setStatus(0); // Change sale to closed
            s.save();
            return ok(sale.render(u, s, s.getItems()));
        }
        return notFound404();
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
     * Handle edit requests to transaction
     * @param saleId Id of sale
     * @param tranId Id of transaction
     * @return HTTP response to edit request
     */
    @Authenticated(Secured.class)
    public Result transaction(int saleId, int tranId) {
        Sale s = Sale.findById(saleId);
        User u = User.findByUsername(session("username"));
        Transaction t = Transaction.findById(tranId);
        if (s != null && u != null && u.canBeSeller(saleId) && t != null) {
            // Check if sale exists, user exists and can be a seller, and transaction exists

            if (s.status == 2) { return redirect("/sale/" + s.id); } // If sale archived, redirect back to sale page

            DynamicForm f = Form.form().bindFromRequest();

            // Handle item add form
            if (f.get("addItemId") != null && f.get("addItemQuantity") != null) {
                int itemId;
                int itemQuantity;
                try { // Convert strings to integers
                    itemId = Integer.parseInt(f.get("addItemId"));
                    itemQuantity = Integer.parseInt(f.get("addItemQuantity"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return notFound404();
                }

                // First search to see if item exists and is part of sale
                SaleItem i = SaleItem.findById(itemId);
                if (i == null || i.saleId != saleId) {
                    return ok(transaction.render(t, t.getLineItems(), "No item with that Id"));
                }

                // Then search to see if item is already part of transaction
                LineItem li = LineItem.findByItemIdTransactionId(itemId, t.id);
                if (li != null) { // Item already part of transaction, update quantity
                    li.setQuantity(li.quantity + itemQuantity);
                    li.save();
                } else { // Item not part of transaction, create new LineItem
                    LineItem lin = new LineItem(itemId, t.id, itemQuantity);
                }

                return ok(transaction.render(t, t.getLineItems(), ""));
            }

            // Handle item delete form
            if (f.get("deleteLineItemId") != null) {
                int lineItemId;
                try { // Convert string to integer
                    lineItemId = Integer.parseInt(f.get("deleteLineItemId"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return notFound404();
                }
                LineItem li = LineItem.findById(lineItemId);
                if (li != null) { // If user refreshes page after deleting, this prevents a null pointer exception
                    li.delete();
                }
                return ok(transaction.render(t, t.getLineItems(), ""));
            }

            // Handle item update quantity form
            if (f.get("updateLineItemId") != null && f.get("quantity") != null) {
                int lineItemId;
                int quantity;
                try { // Convert strings to integers
                    lineItemId = Integer.parseInt(f.get("lineItemId"));
                    quantity = Integer.parseInt(f.get("quantity"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return notFound404();
                }
                LineItem li = LineItem.findById(lineItemId);
                if (li != null) {
                    li.setQuantity(quantity);
                    li.save();
                }
                return ok(transaction.render(t, t.getLineItems(), ""));
            }

            // Handle transaction cancel form
            if (f.get("cancelTransactionId") != null) {
                int transactionId;
                try { // Convert string to integer
                    transactionId = Integer.parseInt(f.get("cancelTransactionId"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return notFound404();
                }

                // Delete line items
                List<LineItem> list = LineItem.findByTransactionId(tranId);
                for (LineItem li : list) {
                    li.delete();
                }

                t.delete();

                return redirect("/sale/" + saleId);
            }
        }
        return notFound404();
    }

     /**
     * Search sales
     * @return HTTP response to search results request
     */
    @Authenticated(Secured.class)
    public Result searchItemStock(int saleID, int tranId) {
        DynamicForm f = Form.form().bindFromRequest();
        String query = f.get("query");
        if (query != null) {
            return ok(searchItemStock.render(saleID, tranId, query));
        }
        return notFound404();
    }

    /**
     * Upload a profile picture (not working yet)
     * @return HTTP response to upload profile picture request
     */
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

    /**
     * Unlocks a user account
     * @return Response to user account request
     */
    @Authenticated(Secured.class)
    public Result users() {
        User u = User.findByUsername(session("username"));
        if (u.isSuperAdmin()) { // Requestor is super admin
            DynamicForm f = Form.form().bindFromRequest();
            if (f.get("userId") != null) { // Username was sent in post request
                User userReset = User.findById(f.get("userId"));
                userReset.setLoginAttempts(0);
                userReset.save();
                return ok(admin.render(User.findAll()));
            }
        }
        return notFound404(); // Return 404 error if user is not a super admin
    }
}
