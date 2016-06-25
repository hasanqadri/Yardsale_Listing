package controllers;

import play.mvc.Security.Authenticator;
import play.mvc.Http.Context;
import play.mvc.Result;

/**
 * Created by nathancheek on 6/24/16.
 */
public class Secured extends Authenticator {
    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("username");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.PageController.login());
    }
}