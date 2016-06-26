package controllers;

import com.avaje.ebean.Ebean;
import models.Sale;
import models.SaleItem;
import models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.loggedinNotFound;
import views.html.notFound;

import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by nathancheek on 6/25/16.
 */
public class DataController extends Controller {

    /**
     * Gets an image stored in the database
     * @param id Id of image to return
     * @return Image with given Id
     */
    @Security.Authenticated(Secured.class)
    public Result getImage(int id) { // This method is currently being used for testing random stuff
        //Picture picture = User.find.where().eq("id", dynamicForm.get("username")).findUnique();
        //Sale sale2 = Sale.find.where().eq("id", 1).findUnique();
        return ok();

    }

    /**
     * Lists data about a sale
     * @return JSON response of sale data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getSale() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int id;
        try {
            id = Integer.parseInt(dynamicForm.get("id"));
        } catch (NumberFormatException e) { // Null or non int string
            return notFound404();
        }
        Sale sale = Ebean.find(Sale.class).where().eq("id", id).findUnique();
        User user = null;
        if (sale != null) {
            user = Ebean.find(User.class).where().eq("id", sale.userCreatedId).findUnique();
        } else { // Sale doesn't exist
            return notFound404();
        }
        String createdBy = "";
        if ( user != null) {
           createdBy = user.getName();
        }
        JSONObject jo = null;
        try {
            jo = new JSONObject()
                    .put("name", sale.name)
                    .put("description", sale.description)
                    .put("street", sale.street)
                    .put("city", sale.city)
                    .put("state", sale.state)
                    .put("zip", sale.zip)
                    .put("createdBy", createdBy);
        } catch (JSONException e) {
            e.printStackTrace();
            return notFound404();
        }
        return ok(jo.toString()).as("application/json");
    }

    /**
     * Lists all data about all sales
     * @return JSON response of all sale data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getSales() {
        List<Sale> sales = Ebean.find(Sale.class).findList();
        return ok(toJson(sales));
    }


    /**
     * Returns list of posts listed at the queried location
     * @return
     */
    @Security.Authenticated(Secured.class)
    public Result getSearchLocations() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String query = dynamicForm.get("query");
        if (query != null) {
            List<Sale> sales = Ebean.find(Sale.class).where().like("city", query).findList();
            return ok(toJson(sales));
        }
        return notFound404();
    }


    /**
     * Lists all data about an item
     * @return JSON response of item data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getItem() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int id;
        try {
            id = Integer.parseInt(dynamicForm.get("id"));
        } catch (NumberFormatException e) { // Null or non int string
            return notFound404();
        }
        SaleItem item  = Ebean.find(SaleItem.class).where().eq("id", id).findUnique();
        return ok(toJson(item));
    }

    /**
     * Lists all data about all items
     * @return JSON response of all item data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getItems() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int saleId;
        try {
            saleId = Integer.parseInt(dynamicForm.get("saleId"));
        } catch (NumberFormatException e) { // Null or non int string
            return notFound404();
        }
        List<SaleItem> items = Ebean.find(SaleItem.class).where().eq("saleId", saleId).findList();
        return ok(toJson(items));
    }


    /**
     * Returns list of posts listed with the queried item name
     *
     * @return items to json
     */
    @Security.Authenticated(Secured.class)
    public Result getSearchItems() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int saleId;
        try {
            saleId = Integer.parseInt(dynamicForm.get("saleId"));
        } catch (NumberFormatException e) { // Null or non int string
            return notFound404();
        }
        String query = dynamicForm.get("query");
        if (query != null) {
            List<SaleItem> items = Ebean.find(SaleItem.class).where().eq("id", saleId).like("name", query).findList();
            items.addAll(Ebean.find(SaleItem.class).where().eq("id", saleId).like("description", query).findList());
            return ok(toJson(items));
        }
        return notFound404();
    }

    /**
     * Lists name, username, email of all users
     * @return JSON response of select user data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getUsers() {
        List<User> users = Ebean.find(User.class).findList();
        JSONArray ja = new JSONArray();
        for (User user : users) {
            JSONObject jo = null;
            try {
                jo = new JSONObject()
                        .put("name", user.getName())
                        .put("username", user.username)
                        .put("email", user.email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ja.put(jo);
        }
        return ok(ja.toString());
    }

    /**
     * Lists complete json data of all users
     * @return JSON response of all user data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getUsersAdmin() {
        User user = User.find.where().eq("username", session("username")).findUnique();
        if (user.superAdmin == 1) { // Show supersecret user data
            List<User> users = Ebean.find(User.class).findList();
            return ok(toJson(users));
        } else { // Return 404 if requesting user is not privileged
            return notFound404();
        }
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
}
