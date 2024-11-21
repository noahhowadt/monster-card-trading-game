package at.technikum.application.mctg.routing;

import at.technikum.application.mctg.controllers.Controller;
import at.technikum.application.mctg.exceptions.NotFoundException;
import at.technikum.server.http.Request;

import java.util.ArrayList;
import java.util.List;

public class Router {
    private final List<RouteDefinition> routes;

    public Router() {
        this.routes = new ArrayList<RouteDefinition>();
    }

    public Controller getController(Request request) throws NotFoundException {
        RouteDefinition routeDefinition = routes.stream().filter(route -> route.isMatched(request.getPath())).findFirst().orElseThrow(() -> new NotFoundException("Controller not found"));
        return routeDefinition.getController();
    }

    public void addRoute(String route, Controller controller) {
        this.routes.add(new RouteDefinition(route, controller));
    }
}
