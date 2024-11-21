package at.technikum.application.mctg.controllers;

import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class PackageController extends Controller {
    public Response handle(final Request request) {
        Response response = new Response();
        response.setStatus(Status.OK);
        return response;
    }
}
