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
        if( uname == null || !uname.equals("admin")) { // CHANGE THIS TO CHECK FOR ALL ADMINS
            halt(401, "Access denied");
        }
    }

    public void criticBefore(Request req, Response resp) {
        String uname = req.session().attribute("username");
        if( uname == null || !uname.equals("critic")) { // CHANGE THIS TO CHECK FOR ALL CRITICS AND ADMINS
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

    public Object grantAdmin(Request req, Response resp) {
        String userToGrantAdmin = req.queryParams("uname");

        try (DbFacade db = new DbFacade()) {

            db.grantAdministrator(userToGrantAdmin);

        } catch (SQLException e) {
            System.err.println("Error in deleteComment: " + e.getMessage());

            resp.status(500);
            return "";
        }

        return Main.renderTemplate(null, "adminHome.hbs");
    }

    public Object deleteAccount(Request req, Response resp) {
        String userToDelete = req.queryParams("uname");

        try (DbFacade db = new DbFacade()) {

            db.deleteUser(userToDelete);

        } catch (SQLException e) {
            System.err.println("Error in deleteComment: " + e.getMessage());

            resp.status(500);
            return "";
        }
        return Main.renderTemplate(null, "adminHome.hbs");
    }

    public Object deleteComment(Request req, Response resp) {
        String commentToDelete = req.queryParams("postid");

        try (DbFacade db = new DbFacade()) {

            db.deleteComment(commentToDelete);

        } catch (SQLException e) {
            System.err.println("Error in deleteComment: " + e.getMessage());

            resp.status(500);
            return "";
        }
        return Main.renderTemplate(null, "adminHome.hbs");
    }

    public Object grantCritic(Request req, Response resp) {
        String userToGrantCritic = req.queryParams("uname");

        try (DbFacade db = new DbFacade()) {

            db.grantCritic(userToGrantCritic);

        } catch (SQLException e) {
            System.err.println("Error in deleteComment: " + e.getMessage());

            resp.status(500);
            return "";
        }
        return Main.renderTemplate(null, "adminHome.hbs");
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
                row.put("post_date", rset.getString(1));
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

    public Object searchMovie(Request req, Response resp) {
        String movie = req.queryParams("movie");

        try (DbFacade db = new DbFacade()) {
            Map<String, Object> templateData = new HashMap<>();
            ResultSet rset = db.listMovieReviews(movie);

            ArrayList<Map<String, String>> movies = new ArrayList<>();

            while (rset.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("post_date", rset.getString(1));
                row.put("movie", rset.getString(2));
                row.put("rating", rset.getString(3));
                row.put("critic", rset.getString(4));
                movies.add(row);
            }

            templateData.put("movies", movies);
            return Main.renderTemplate(templateData, "movieReviews.hbs");

        } catch (SQLException e) {
            System.err.println("Error in searchMovie: " + e.getMessage());

            resp.status(500);
            return "";
        }
    }

    public Object getReviewByID(Request req, Response resp) {
        String reviewID = req.params(":reviewid");

        try (DbFacade db = new DbFacade()) {
            Map<String, Object> templateData = new HashMap<>();
            ResultSet rset = db.getReviewByID(reviewID);

            while( rset.next() ){

                templateData.put("post_date", rset.getString(1));
                templateData.put("movie", rset.getString(2));
                templateData.put("rating", rset.getString(3));
                templateData.put("critic", rset.getString(4));
            }


            return Main.renderTemplate(templateData, "movie-display.hbs");

        } catch (SQLException e) {
            System.err.println("Error in searchMovie: " + e.getMessage());

            resp.status(500);
            return "";
        }

    }

    public Object getComments(Request req, Response resp) {
        String movie = req.queryParams("movie");

        try (DbFacade db = new DbFacade()) {
            Map<String, Object> templateData = new HashMap<>();
            ResultSet rset = db.listMovieReviews(movie);

            ArrayList<Map<String, String>> movies = new ArrayList<>();

            while (rset.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("post_date", rset.getString(1));
                row.put("movie", rset.getString(2));
                row.put("rating", rset.getString(3));
                row.put("critic", rset.getString(4));
                row.put("review_id", rset.getString(5));
                movies.add(row);
            }

            templateData.put("movies", movies);

            rset = db.getComments(movie);

            ArrayList<Map<String, String>> comments = new ArrayList<>();

            while (rset.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("comment_id", rset.getString(1));
                row.put("author", rset.getString(2));
                row.put("content", rset.getString(3));
                row.put("post_date", rset.getString(4));
                comments.add(row);
            }
            templateData.put("comments", comments);

            return Main.renderTemplate(templateData, "movieAndComments.hbs");

        } catch (SQLException e) {
            System.err.println("Error in getComments: " + e.getMessage());

            resp.status(500);
            return "";
        }
    }

    public Object addComment(Request req, Response resp) {
        String comment = req.queryParams("comment");
        String uname = req.session().attribute("username");
        String review_id = req.queryParams("review_id");


        try (DbFacade db = new DbFacade()) {

            ResultSet rset = db.addComment(comment, uname, review_id);


            return Main.renderTemplate(null, "addCommentSuccess.hbs");

        } catch (SQLException e) {
            System.err.println("Error in addComment: " + e.getMessage());

            resp.status(500);
            return "";
        }
    }

    public Object addCommentSuccess(Request req, Response resp) {
        return Main.renderTemplate(null, "addCommentSuccess.hbs");
    }
}
