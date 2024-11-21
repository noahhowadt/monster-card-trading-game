package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.services.UserService;
import at.technikum.application.mctg.util.Utils;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class UserController extends Controller {
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    public Response handle(final Request request) {
        return switch (request.getMethod()) {
            case POST -> create(request);
            case GET -> read(request);
            case PUT -> update(request);
            case DELETE -> delete(request);
        };
    }

    private Response create(final Request request) {
        User newUser = super.parseBody(request, User.class);
        this.userService.create(newUser);

        Response res = new Response();
        res.setStatus(Status.CREATED);
        res.setHeader("Content-Type", "application/json");
        res.setBody(super.stringifyObject(newUser));
        return res;
    }

    private Response read(final Request request) {
        String[] pathArr = Utils.getPathArray(request.getPath());
        if (pathArr.length != 2) {
            Response res = new Response();
            res.setStatus(Status.METHOD_NOT_ALLOWED);
            return res;
        }
        String username = pathArr[1];
        System.out.println(username);
        return null;
    }

    private Response update(final Request request) {
        return null;
    }

    private Response delete(final Request request) {
        return null;
    }
}
