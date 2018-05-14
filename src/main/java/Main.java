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

        Spark.before("/user/*", controller::userBefore);
        Spark.before("/admin/*", controller::adminBefore);

    }

    public static String renderTemplate(Map<String,Object> model, String path ) {
        return new HandlebarsTemplateEngine().render(new ModelAndView(model, path));
    }

}
