package controllers;

import com.avaje.ebean.Ebean;
import java.util.List;
import models.Sale;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

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
    public Result getImage(String id) { // This method is currently being used for testing random stuff
        //Picture picture = User.find.where().eq("id", dynamicForm.get("username")).findUnique();
        //Sale sale2 = Sale.find.where().eq("id", 1).findUnique();
        return ok();

        //return notFound(notFound.render());
    }

    /**
     * Lists all data about a sale
     * @return JSON response of sale data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getSale() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int id;
        try {
            id = Integer.parseInt(dynamicForm.get("id"));
        } catch (NumberFormatException e) { // Null or non int string
            return notFound404("");
        }
        Sale sale = Ebean.find(Sale.class).where().eq("id", id).findUnique();
        return ok(toJson(sale));
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
     *
     * @return
     */
    @Security.Authenticated(Secured.class)
    public Result getSearchLocations() {
        if (request().method() == "POST") {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            String query = dynamicForm.get("query");
            if (query != null) {
                List<Sale> sales = Ebean.find(Sale.class).where().like("city", query).findList();
                return ok(toJson(sales));
            }
        }
        return notFound404("/getSearchLocations");
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
            JSONObject jo = new JSONObject()
                    .put("name", user.getName())
                    .put("username", user.username)
                    .put("email", user.email);
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
            return notFound404("/adminGetUsers");
        }
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
}
