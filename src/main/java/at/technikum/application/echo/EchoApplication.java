package at.technikum.application.echo;

import at.technikum.server.Application;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import at.technikum.server.util.HttpResponseFormatter;

public class EchoApplication implements Application {

    @Override
    public Response handle(Request request) {

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setHeader("Content-Type", "text/plain");
        response.setBody("This is an example body!");

        HttpResponseFormatter formatter = new HttpResponseFormatter();
        String http = formatter.format(response);

        response.setBody(http);

        return response;
    }
}