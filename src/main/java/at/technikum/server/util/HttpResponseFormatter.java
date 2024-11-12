package at.technikum.server.util;

import at.technikum.server.http.Response;

public class HttpResponseFormatter {
    public String format(Response response) {
        if(response.getStatus() == null) throw new NoHttpStatusException("Response does not contain a status");

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(response.getStatus().toString());

        response.setHeader("Content-Length", "%s".formatted(response.getBody().length()));
        response.getHeaders().forEach((key, value) -> {
            responseBuilder.append("\r\n").append(key).append(": ").append(value);
        });

        responseBuilder.append("\r\n").append("\r\n");
        responseBuilder.append(response.getBody());
        return responseBuilder.toString();
    }
}
