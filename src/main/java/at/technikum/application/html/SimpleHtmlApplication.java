package at.technikum.application.html;

import at.technikum.server.Application;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class SimpleHtmlApplication implements Application {

    @Override
    public Response handle(Request request) {

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setHeader("Content-Type", "text/html");
        response.setBody("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Hello World</title>
                </head>
                <body>
                    <h1>Hello World!</h1>
                    <span>On path: %s</span>
                </body>
                </html>""".formatted(request.getPath()));

        return response;
    }
}