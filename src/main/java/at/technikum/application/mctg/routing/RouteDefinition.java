package at.technikum.application.mctg.routing;

import at.technikum.application.mctg.controllers.Controller;

public class RouteDefinition {
    final private String path;
    final private Controller controller;

    public RouteDefinition(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public boolean isMatched(String path) {
        return path.startsWith(this.path);
    }

    public Controller getController() {
        return this.controller;
    }
}
