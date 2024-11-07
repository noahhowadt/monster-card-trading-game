package at.technikum.server;

import at.technikum.server.http.Response;
import at.technikum.server.http.Request;

public interface Application {
    Response handle(Request request);
}
