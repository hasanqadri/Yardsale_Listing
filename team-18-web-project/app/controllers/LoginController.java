package controllers;

import play.mvc.*;
import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the login page.
 */
public class LoginController extends Controller {

    public Result index() {
        return ok(index.render("Welcome"));
    }

}
