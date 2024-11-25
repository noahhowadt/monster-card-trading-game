package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.entities.UserCredentials;
import at.technikum.application.mctg.exceptions.MethodNotAllowedException;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.core.type.TypeReference;

public class SessionController extends Controller {
    private final AuthService authService;

    public SessionController(final AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response handle(Request request) {
        if (request.getMethod() != Method.POST) throw new MethodNotAllowedException("This method is not allowed");

        UserCredentials credentials = super.parseBody(request, new TypeReference<UserCredentials>() {
        });
        String token = this.authService.getToken(credentials);

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setBody(token);
        return response;
    }
}
