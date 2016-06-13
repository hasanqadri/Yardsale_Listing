package controllers;

import play.mvc.*;
import play.data.DynamicForm;
import play.data.Form;

import java.util.List;
import static play.libs.Json.toJson;

import views.html.*;

import models.User;

import com.avaje.ebean.*;

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

    public Result about() { return ok(about.render()); }

    public Result login() {
        if (request().method() == "POST") {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            if (dynamicForm.get("username").equals("user") && dynamicForm.get("password").equals("pass")) {
                /* Set cookie and redirect to /loggedin */
                response().setCookie("login", "1");
                return redirect("/loggedin");
            } else {
                return ok(login.render("Login failed"));
            }
        } else {
            return ok(login.render(""));
        }
    }

    public Result loggedin() {
        /*
        Code here to check if cookie is set, otherwise send to /login
         */
        if (request().cookies().get("login") != null && request().cookies().get("login").value().equals("1")) {
            return ok(loggedin.render());
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
