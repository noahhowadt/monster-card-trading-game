package at.technikum.application.mctg;

import at.technikum.application.mctg.controllers.Controller;
import at.technikum.application.mctg.controllers.PackageController;
import at.technikum.application.mctg.controllers.UserController;
import at.technikum.application.mctg.exceptions.ExceptionHandler;
import at.technikum.application.mctg.routing.Router;
import at.technikum.server.Application;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class MonsterCardTradingGame implements Application {
    private final Router router;
    private final ExceptionHandler exceptionHandler;

    public MonsterCardTradingGame() {
        this.router = new Router();
        this.exceptionHandler = new ExceptionHandler();
        this.initRoutes();
    }

    private void initRoutes() {
        this.router.addRoute("/users", new UserController());
        this.router.addRoute("/packages", new PackageController());
    }

    @Override
    public Response handle(final Request request) {
        return this.exceptionHandler.wrap(() -> {
            final Controller controller = router.getController(request);
            return controller.handle(request);
        });
    }
}
