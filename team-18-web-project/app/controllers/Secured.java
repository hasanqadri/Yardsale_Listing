package controllers;

import play.mvc.Security.Authenticator;
import play.mvc.Http.Context;
import play.mvc.Result;

/**
 * Checks if user is logged in
 * Created by nathancheek on 6/24/16.
 */
public class Secured extends Authenticator {
    @Override
    /**
     * Get Username of User
     * @param ctx Context
     * @return Username of User
     */
    public String getUsername(Context ctx) {
        return ctx.session().get("username");
    }

    @Override
    /**
     * HTTP Result to non-logged in User
     * @param ctx Context
     * @return Redirect to login page
     */
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.PageController.login());
    }
}
