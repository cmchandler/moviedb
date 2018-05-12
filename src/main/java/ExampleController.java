import spark.Request;
import spark.Response;
import spark.Session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.halt;

public class ExampleController {

    public ExampleController() {
        // Any initialization for your controller can go here
    }

    public Object getLoginForm(Request req, Response resp) {
        return Main.renderTemplate(null, "login-form.hbs");
    }

    public Object postLoginForm(Request req, Response resp) {
        String uname = req.queryParams("uname");
        String pwd = req.queryParams("password");

        Session sess = req.session();
        sess.attribute(uname, true);

        try(DbFacade db = new DbFacade()) {

            if( db.authenticateUser(uname, pwd) ) {
                return Main.renderTemplate(null, "login-success.hbs");
            }

        } catch(SQLException e) {
            resp.status(500);
            System.err.println("postLoginForm: " + e.getMessage());
            return "";
        }

        // If we got here, authentication failed.  Show login form again with error message.
        Map<String,Object> data = new HashMap<>();
        data.put("errorMsg", "Login failed!");
        return Main.renderTemplate(data, "login-form.hbs");
    }

    public Object signUp(Request req, Response resp) {
        return Main.renderTemplate(null, "signUp.hbs");
    }

    public Object signUpSuccess(Request req, Response resp) {
        return Main.renderTemplate(null, "signUpSuccess.hbs");
    }


    /*

     */
    public Object userHome(Request req, Response resp) {
        return Main.renderTemplate(null, "userHome.hbs");
    }

    public Object adminHome(Request req, Response resp) {
        return Main.renderTemplate(null, "adminHome.hbs");
    }

    public void userBefore(Request req, Response resp) {
        Session sess = req.session();
        Boolean auth = sess.attribute("authenticated");

        if ( auth == null || (!auth)) {
            halt(401, "Access denied");
        }
    }

    public void adminBefore(Request req, Response resp) {
        Session sess = req.session();
        Boolean auth = sess.attribute("authenticated");

        if ( auth == null || (!auth)) {
            halt(401, "Access denied");
        }
    }
}
