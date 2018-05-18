import spark.ModelAndView;
import spark.Spark;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {

    public static void main(String[] args) {
        // Create the controller
        ExampleController controller = new ExampleController();

        // Configure routes.  This defines each URL and associates it with a controller method.
        // Use get to setup methods for GET requests, and post to setup methods for POST requests.
        get("/login", controller::getLoginForm);
        post( "/authenticate", controller::postLoginForm);
        get("/signUp", controller::signUp);
        post("/postSignUp", controller::postSignUp);
        get("/user/home", controller::userHome);
        get("/admin/home", controller::adminHome);
        get("/critic/home", controller::criticHome);
        get("/critic/newReview", controller::getNewReview);
        post("/critic/postNewReview", controller::postNewReview);
        get("/movies", controller::getMovies);
        get("/movieReviews", controller::getMovieReviews);
        post("/user/searchMovie", controller::searchMovie);
        post("/user/moviesAndComments", controller::getComments);
        post("/user/addComment", controller::addComment);
        get("/addCommentSuccess", controller::addCommentSuccess);
        get("/review/:reviewid", controller::getReviewByID);


        post("/admin/grantAdmin", controller::grantAdmin);
        post("/admin/deleteAccount", controller::deleteAccount);
        post("/admin/deleteComment", controller::deleteComment);
        post("/admin/grantCritic", controller::grantCritic);

        Spark.before("/user/*", controller::userBefore);
        Spark.before("/admin/*", controller::adminBefore);
        Spark.before("/critic/*", controller::criticBefore);



    }

    public static String renderTemplate(Map<String,Object> model, String path ) {
        return new HandlebarsTemplateEngine().render(new ModelAndView(model, path));
    }

}
