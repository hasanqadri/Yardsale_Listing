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
    public Result home() {
        String username = session("connected");
        if (username != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/profile");
        }
        return ok(home.render("Welcome"));
    }

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

    public Result about() {
        String username = session("connected");
        if (username != null) { //Temporarily redirect all logged in users to /profile
            return redirect("/profile");
        }
        return ok(about.render());
    }

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


    public Result profile() {
        /*
        Code here to check if cookie is set, otherwise send to /login
         */
        String username = session("connected");
        User user = User.find.where().eq("username", username).findUnique();


        if (username == null) {
            return redirect("/login");
        }

        if (request().method() == "POST") {
            DynamicForm dynamicForm = Form.form().bindFromRequest();
            User userCheckUsername = User.find.where().eq("username", dynamicForm.get("username")).findUnique();
            if (!userCheckUsername.equals(username) && userCheckUsername != null) {
                return ok(profile.render(username, user.getPassword(), user.getName(), user.getEmail(),
                        "Error: username already in use"));
            }
            User userCheckEmail = User.find.where().eq("email", dynamicForm.get("email")).findUnique();
            if (!userCheckEmail.equals(user.getEmail()) && userCheckEmail != null) {
                return ok(profile.render(username, user.getPassword(), user.getName(), user.getEmail(),
                        "Error: Email already in use"));

            }
            //If username or email not already in use, update user
            user.setName(dynamicForm.get("name"));
            user.setEmail(dynamicForm.get("email"));
            user.setUsername(dynamicForm.get("username"));
            user.setPassword(dynamicForm.get("password"));
            user.save();
            session("connected",o do in por au prinncre dynamicForm.get("username"));
        }
        return ok(profile.render(username, user.getPassword(), user.getName(), user.getEmail(),
                ""));

    }

    public Result logout() {
        session().clear();
        return redirect("/");
    }

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

    public Result users() {
        String username = session("connected");
        if (username != null) {
            return ok(users.render());
        } else {
            return redirect("/login");
        }
    }
}
