package at.technikum.server;

import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.util.HttpRequestParser;
import at.technikum.server.util.HttpResponseFormatter;
import at.technikum.server.util.HttpSocket;

import java.io.IOException;
import java.net.Socket;

public class RequestHandler {

    // [x] receive socket
    // [x] wrap socket in HttpSocket
    // [x] get HTTP request
    // [x] parse to request obj
    // give request to application
    // receive response
    // format response to HTTP response
    // send response to client

    private final Socket socket;
    private final Application application;

    public RequestHandler(
            Socket socket,
            Application application
    ) {
        this.socket = socket;
        this.application = application;
    }

    public void handle() {
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpResponseFormatter httpResponseFormatter = new HttpResponseFormatter();

        try (HttpSocket httpSocket = new HttpSocket(this.socket)) {
            String http = httpSocket.read();
            Request request = httpRequestParser.parse(http);

            Response response = this.application.handle(request);

            http = httpResponseFormatter.format(response);
            httpSocket.write(http);
        } catch (IOException e) {

            // send standard error response

            throw new RuntimeException(e);
        }

        /*
        this is the idea: do something but close the socket in any case
        try {
            // try something risky
        } catch () {
            // handle the problem
        } finally {
            // cleanup
        }
        */
    }
}