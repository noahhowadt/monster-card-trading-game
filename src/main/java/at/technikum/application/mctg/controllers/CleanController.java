package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.exceptions.MethodNotAllowedException;
import at.technikum.application.mctg.services.CleanService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class CleanController extends Controller {
    private final CleanService cleanService;

    public CleanController(final CleanService cleanService) {
        this.cleanService = cleanService;
    }

    public Response handle(final Request request) {
        if (request.getMethod() != Method.POST) throw new MethodNotAllowedException("Method not allowed");
        cleanService.clean();

        Response response = new Response();
        response.setStatus(Status.OK);
        return response;
    }
}
