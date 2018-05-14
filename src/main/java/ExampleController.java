import spark.Request;
import spark.Response;
import spark.Session;
import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.halt;
import static spark.Spark.webSocket;

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

        try(DbFacade db = new DbFacade()) {

            if( db.authenticateUser(uname, pwd) ) {
                Session sess = req.session();
                sess.attribute("username", uname);
                sess.attribute("authenticated", true);
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

    public Object postSignUp(Request req, Response resp) {
        String uname = req.queryParams("uname");
        String pwd = req.queryParams("password");
        String email = req.queryParams("email");

        try(DbFacade db = new DbFacade()) {

            if( db.accountCreation(uname, pwd, email) == 1) { // if it returns 1 then the statement worked?
                return Main.renderTemplate(null, "signUpSuccess.hbs");
            }

        } catch(SQLException e) {
            resp.status(500);
            System.err.println("signUp failed: " + e.getMessage());
            return "";
        }
        // If we got here, sign up failed.  Show sign up form again with error message.
        Map<String,Object> data = new HashMap<>();
        data.put("errorMsg", "Sign up failed!");
        return Main.renderTemplate(data, "signUp.hbs");
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
        Boolean auth = req.session().attribute("authenticated");
        if( auth == null || (!auth) ) {
            halt(401, "Access denied");
        }
    }

    public void adminBefore(Request req, Response resp) {
        String uname = req.session().attribute("username");
        if( uname == null || !uname.equals("admin")) { // CHANGE THIS TO HAVE ALL ADMINS
            halt(401, "Access denied");
        }
    }

    public void criticBefore(Request req, Response resp) {
        String uname = req.session().attribute("username");
        if( uname == null || !uname.equals("critic") || !uname.equals("admin")) { // CHANGE THIS TO HAVE ALL CRITICS AND ADMINS
            halt(401, "Access denied");
        }
    }

    public Object getNewReview(Request req, Response resp) {
        return Main.renderTemplate(null, "new-review-form.hbs");
    }

    public Object postNewReview(Request req, Response resp) {

        String movieName = req.queryParams("movieName");
        String review = req.queryParams("review");
        String rating = req.queryParams("rating");

        try(DbFacade db = new DbFacade()) {
            db.postReview(movieName, review, rating);

        } catch(SQLException e) {
            resp.status(500);
            System.err.println("postLoginForm: " + e.getMessage());
            return "";
        }

        try {

        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/Users/christien"));
        Part filePart = req.raw().getPart("myfile");

        InputStream inputStream = filePart.getInputStream() ;

            OutputStream outputStream = new FileOutputStream("/Users/christien" + filePart.getSubmittedFileName());
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();

        } catch (IOException e) {
            System.out.println("IO Exception.");
        } catch (ServletException e) {
            System.out.println(e.getMessage());
        }

        return Main.renderTemplate(null, "new-review-success.hbs");
    }

    public Object criticHome(Request req, Response resp) {
        return Main.renderTemplate(null, "criticHome.hbs");
    }

    public Object getMovies(Request req, Response resp) {

        try (DbFacade db = new DbFacade()) {
            Map<String, Object> templateData = new HashMap<>();
            ResultSet rset = db.allMovies();

            ArrayList<Map<String, String>> movies = new ArrayList<>();

            while (rset.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("movieName", rset.getString(1));
                movies.add(row);
            }

            templateData.put("movies", movies);
            return Main.renderTemplate(templateData, "movies.hbs");

        } catch (SQLException e) {
            System.err.println("Error in getMovies: " + e.getMessage());

            resp.status(500);
            return "";
        }


    }

    public Object getMovieReviews(Request req, Response resp) {

        try (DbFacade db = new DbFacade()) {
            Map<String, Object> templateData = new HashMap<>();
            ResultSet rset = db.listAllMovieReviews();

            ArrayList<Map<String, String>> movies = new ArrayList<>();

            while (rset.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("postDate", rset.getString(1));
                row.put("movie", rset.getString(2));
                row.put("rating", rset.getString(3));
                row.put("critic", rset.getString(4));
                movies.add(row);
            }

            templateData.put("movies", movies);
            return Main.renderTemplate(templateData, "movieReviews.hbs");

        } catch (SQLException e) {
            System.err.println("Error in getMovieReviews: " + e.getMessage());

            resp.status(500);
            return "";
        }


    }
}
