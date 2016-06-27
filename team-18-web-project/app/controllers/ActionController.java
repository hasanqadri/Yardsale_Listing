package controllers;

import com.avaje.ebean.Ebean;
import java.sql.Timestamp;
import java.text.*;
import java.util.Date;
import models.Sale;
import models.SaleItem;
import models.User;
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
        Sale s = Ebean.find(Sale.class).where().eq("id", id).findUnique();
        if (s != null) { // Check if sale exists
            DynamicForm f = Form.form().bindFromRequest();
            if (f.get("name") == null || f.get("description") == null || f.get("price") == null) { // Improper request
                return notFound404();
            }
            float price;
            try {
                price = Float.parseFloat(f.get("price"));
            } catch (NumberFormatException e) {
                return ok(addItem.render(id, "Error: bad price"));
            }
            SaleItem item = new SaleItem(f.get("name"), f.get("description"), price, id);
            item.save();
            return ok(addItem.render(id, "Item added! Add another item?"));
        }
        return notFound404();
    }

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
        return notFound404();
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
        sale.userCreatedId = user.id;

        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date startingDate = dateFormat.parse(createSaleForm.get("startDate"));
            long startTime = startingDate.getTime();
            sale.startDate = new Timestamp(startTime);

            Date endingDate = dateFormat.parse(createSaleForm.get("startDate"));
            long endTime = endingDate.getTime();
            sale.endDate = new Timestamp(endTime);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        sale.save();
        return ok(createSale.render("Sale Created! Create Another Sale?"));
    }

    /**
     * Creates a sale item
     * @return HTTP response to create sale item request
     */
    @Authenticated(Secured.class)
    public Result createSaleItem() {
        //todo this should be much like createSale() above. Doesnt addItem achieve this function?
        return ok();
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
        SaleItem i = Ebean.find(SaleItem.class).where().eq("id", itemId).findUnique();
        float price;
        try {
            price = Float.parseFloat(f.get("price"));
        } catch (NumberFormatException e) {
            return ok(item.render(saleId, itemId, i.name, i.description, i.price, "Error: bad price"));
        }

        i.name = f.get("name");
        i.description = f.get("description");
        i.price = price;
        i.save();

        return ok(item.render(saleId, itemId, i.name, i.description, i.price, ""));
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


    //TODO Method doesnt work as intended, also not in routes
    /**
     * Update Item page for item
     * @return HTTP response to profile page update request
     */
    @Authenticated(Secured.class)
    public Result item(String name) {
        SaleItem item = SaleItem.find.where().eq("name", name).findUnique();
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        // update Item
        item.setName(dynamicForm.get("name"));
        item.setDescription(dynamicForm.get("description"));
        item.setPrice(Float.parseFloat(dynamicForm.get("price")));
        item.save();
        //return ok(item.render(item.getName(), item.getDescription(), item.getPrice(), ""));
        return ok();
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
    public Result searchItems(int saleId) {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String query = dynamicForm.get("query");
        if (query != null) {
            return ok(searchItems.render(saleId, query));
        }
        return notFound404();
    }

    @Authenticated(Secured.class)
    public Result searchSales() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String query = dynamicForm.get("query");
        if (query != null) {
            return ok(searchSales.render(query));
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
        User user = User.find.where().eq("username", session("username")).findUnique();
        return ok("in progress");
    }
}
