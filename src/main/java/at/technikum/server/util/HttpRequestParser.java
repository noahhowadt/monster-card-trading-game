package at.technikum.server.util;

import at.technikum.server.http.Method;
import at.technikum.server.http.Request;

public class HttpRequestParser {
    public static Request parse(String rawRequest) {
        String[] lines = rawRequest.split("\\R");
        Request request = new Request();
        parseRequestLine(lines[0], request);

        int i = 1;
        while(i < lines.length) {
            if(lines[i].isEmpty()) break;
            parseHeader(lines[i], request);
            i++;
        }

        if(request.getHeader("Content-Type") == null) return request;
        StringBuilder bodyBuilder = new StringBuilder();
        while(i < lines.length) {
            bodyBuilder.append(lines[i]);
            i++;
        }
        request.setBody(bodyBuilder.toString().replaceAll(" ", ""));
        return request;
    }

    private static void parseRequestLine(String line, Request request) throws IllegalArgumentException {
        String[] tokens = line.split(" ");
        if (tokens.length != 3) throw new IllegalArgumentException("Invalid request: " + line);

        request.setMethod(Method.valueOf(tokens[0]));

        String path = tokens[1];
        if (path.isEmpty()) throw new IllegalArgumentException("Invalid request: " + line);
        request.setPath(path);

        if (!tokens[2].equals("HTTP/1.1")) throw new IllegalArgumentException("We only accept HTTP/1.1");
    }

    private static void parseHeader(String line, Request request) throws IllegalArgumentException {
        String[] tokens = line.split(":");
        if (tokens.length != 2) throw new IllegalArgumentException("Invalid request: " + line);
        request.setHeader(tokens[0], tokens[1].trim());
    }
}
