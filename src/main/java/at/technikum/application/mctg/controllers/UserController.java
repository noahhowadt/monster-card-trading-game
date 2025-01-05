package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.MethodNotAllowedException;
import at.technikum.application.mctg.exceptions.UnauthorizedException;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.application.mctg.services.UserService;
import at.technikum.application.mctg.util.Utils;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.core.type.TypeReference;

public class UserController extends Controller {
    private final UserService userService;
    private final AuthService authService;

    public UserController(final UserService userService, final AuthService authService) {
        this.userService = userService;
        this.authService = authService;
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
        User newUser = super.parseBody(request, new TypeReference<User>() {
        });
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
            throw new MethodNotAllowedException("Method not allowed");
        }
        String username = pathArr[1];
        System.out.println(username);
        User loggedInUser = this.authService.authenticate(request);
        if (!loggedInUser.getUsername().equals(username)) throw new UnauthorizedException("Cannot view other user");
        User user = userService.get(username);

        Response res = new Response();
        res.setStatus(Status.OK);
        res.setHeader("Content-Type", "application/json");
        res.setBody(super.stringifyObject(user));
        return res;
    }

    private Response update(final Request request) {
        String[] pathArr = Utils.getPathArray(request.getPath());
        if (pathArr.length != 2) {
            throw new MethodNotAllowedException("Method not allowed");
        }

        String username = pathArr[1];
        User user = authService.authenticate(request);
        if (!user.getUsername().equals(username)) throw new UnauthorizedException("Unauthorized");
        User body = super.parseBody(request, new TypeReference<User>() {
        });

        User newUser = this.userService.update(user, body);
        Response res = new Response();
        res.setStatus(Status.OK);
        res.setHeader("Content-Type", "application/json");
        res.setBody(super.stringifyObject(newUser));
        return res;
    }

    private Response delete(final Request request) {
        return null;
    }
}
