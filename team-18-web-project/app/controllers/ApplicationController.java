package controllers;

import com.avaje.ebean.Model;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.userdata;
import views.html.*;

import java.util.List;

import static play.libs.Json.toJson;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class ApplicationController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result home() { return ok(home.render("Welcome")); }

    /**
     * Registration page that takes in the form info and then send the user to postContact
     */
    public Result newContact() {
        Form<userdata> formdata = Form.form(userdata.class);
        return ok(NewContact.render(formdata));
    }

    /**
     * Page after registering, adds form data to user json
     */
    public Result postContact() {
        Form<userdata> formdata = Form.form(userdata.class).bindFromRequest();
        userdata data = formdata.get();
        User user = new User(data.name, data.email, data.username, data.password);
        user.save();
        return ok(postContact.render());

    }

    public Result getUsers() {
        List<User> users = new Model.Finder<>(String.class, User.class).all();
        return ok(toJson(users));
    }

    public Result about() { return ok(about.render()); }

    public Result login() {
        if (request().method() == "POST") {
            List<User> users = new Model.Finder<>(String.class, User.class).all();

            DynamicForm dynamicForm = Form.form().bindFromRequest();
            int x= 0;
            for (User user: users) {
                if (dynamicForm.get("username").equals(user.getUsername()) && dynamicForm.get("password").equals(user.getPassword())) {
                /* Set cookie and redirect to /loggedin */
                    response().setCookie("login", "1");
                    return redirect("/profile");
                }
            }
            return ok(login.render("Login failed"));
        } else {
            return ok(login.render(""));
        }
    }


    public Result profile() {
        /*
        Code here to check if cookie is set, otherwise send to /login
         */
        if (request().cookies().get("login") != null && request().cookies().get("login").value().equals("1")) {
            return ok(profile.render());
        } else {
            return redirect("/login");
        }

    }

    public Result logout() {
        response().discardCookie("login");
        return redirect("/");
    }

    public Result notFound404(String path) { return notFound(notFound.render()); }

    public Result listUsers() {
        //User user = new User("bla@bla.bla", "Test", "1234pass", "mynameis");
        List<User> users = User.find.all();
        return ok(toJson(users));
    }
}