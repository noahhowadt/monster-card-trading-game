package at.technikum.application.mctg;

import at.technikum.application.mctg.controllers.Controller;
import at.technikum.application.mctg.controllers.PackageController;
import at.technikum.application.mctg.controllers.SessionController;
import at.technikum.application.mctg.controllers.UserController;
import at.technikum.application.mctg.exceptions.ExceptionHandler;
import at.technikum.application.mctg.repositories.UserRepository;
import at.technikum.application.mctg.routing.Router;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.application.mctg.services.UserService;
import at.technikum.server.Application;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class MonsterCardTradingGame implements Application {
    private final Router router;
    private final ExceptionHandler exceptionHandler;
    private final UserService userService;
    private final AuthService authService;

    public MonsterCardTradingGame() {
        // init repositories
        UserRepository userRepository = new UserRepository();

        // init services
        this.userService = new UserService(userRepository);
        this.authService = new AuthService(userRepository);

        // init routing
        this.router = new Router();
        this.exceptionHandler = new ExceptionHandler();
        this.initRoutes();
    }

    private void initRoutes() {
        this.router.addRoute("/users", new UserController(this.userService));
        this.router.addRoute("/sessions", new SessionController(this.authService));
        this.router.addRoute("/packages", new PackageController(this.authService));
    }

    @Override
    public Response handle(final Request request) {
        return this.exceptionHandler.wrap(() -> {
            final Controller controller = router.getController(request);
            return controller.handle(request);
        });
    }
}
