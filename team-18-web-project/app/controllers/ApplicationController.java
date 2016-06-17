package controllers;

import com.avaje.ebean.Model;
import models.User;
import play.data.DynamicForm;
import play.data.Form;

import com.avaje.ebean.*;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.userdata;
import views.html.*;

import java.util.List;

import static play.libs.Json.toJson;


/**
 * @author Nathan Cheek, Pablo Ortega, Hasan Qadri, Nick Yokley
 * @version 0.4
 * This controller contains an action to handle HTTP requests
 * to the application.
 */
public class ApplicationController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * @return HTTP response to home page request
     */
    public Result home() {
        String username = session("connected");
        if (username != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/profile");
        }
        return ok(home.render());
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
     * Page after registering, adds form data to user json
     * @return HTTP response after successful user registration
     */
    public Result postContact() {
        Form<userdata> formdata = Form.form(userdata.class).bindFromRequest();
        userdata data = formdata.get();
        User user = new User(data.name, data.email, data.username, data.password);
        user.save();
        return ok(postContact.render());

    }

    /**
     * Lists complete json data of all users
     * @return JSON response of all user data stored in database
     */
    public Result getUsers() {
        List<User> users = new Model.Finder<>(String.class, User.class).all();
        return ok(toJson(users));
    }

    /**
     * Renders about page
     * @return HTTP response to about page request
     */
    public Result about() {
        String username = session("connected");
        if (username != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/profile");
        }
        return ok(about.render());
    }

    /**
     * Display login page and handle login requests
     * @return Login page or response to login request
     */
    public Result login() {
        if (request().method() == "POST") {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            User user = User.find.where().eq("username", dynamicForm.get("username")).findUnique();
            if (user != null && user.getPassword().equals(dynamicForm.get("password"))) {
                //Create session
                session("connected", dynamicForm.get("username"));
                return redirect("/profile");
            } else {
                return ok(login.render("Login failed"));
            }
        } else {
            String username = session("connected");
            if (username != null) { //Temporarily redirect all logged in users to /profile
                return redirect("/profile");
            }
            return ok(login.render(""));
        }
    }

    /**
     * Display registration page and handle registration requests
     * @return HTTP response to registration page request or registration request
     */
    public Result register() {
        String username = session("connected");
        if (username != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/profile");
        }
        if (request().method() == "POST") {
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
            User user = new User(dynamicForm.get("name"), dynamicForm.get("email"), dynamicForm.get("username"), dynamicForm.get("password"));
            user.save();
            return ok(postContact.render());
        }
        return ok(register.render(""));
    }

    /**
     * Display profile page for user
     * @return HTTP response to profile page request
     */
    public Result profile() {
        /*
        Code here to check if cookie is set, otherwise send to /login
         */
        String username = session("connected");
        if (username != null) {
            return ok(profile.render());
        } else {
            return redirect("/login");
        }

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
     * Displays a 404 error page
     * @param path URI of the page that doesn't exist
     * @return HTTP response to a nonexistant page
     */
    public Result notFound404(String path) { return notFound(notFound.render()); }

    public Result userList() {
        String username = session("connected");
        if (username != null) {
            List<User> users = User.find.all();
            return ok(toJson(users));
        } else {
            return forbidden();
        }
    }

    /**
     * Displays a list of all users
     * @return HTTP response to users list request
     */
    public Result users() {
        String username = session("connected");
        if (username != null) {
            return ok(users.render());
        } else {
            return redirect("/login");
        }
    }
}
