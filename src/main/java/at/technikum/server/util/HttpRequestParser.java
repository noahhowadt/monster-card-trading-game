package at.technikum.server.util;

import at.technikum.server.http.Method;
import at.technikum.server.http.Request;

public class HttpRequestParser {
    public Request parse(String rawRequest) {
        String[] lines = rawRequest.split("\\R");
        Request request = new Request();
        parseRequestLine(lines[0], request);

        int i = 1;
        while (i < lines.length) {
            if (lines[i].isEmpty()) break;
            parseHeader(lines[i], request);
            i++;
        }

        if (request.getHeader("Content-Type") == null) return request;
        StringBuilder bodyBuilder = new StringBuilder();
        while (i < lines.length) {
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
        if (path.contains("?")) {
            request.setPath(path.split("\\?")[0]);
            String query = path.split("\\?")[1];
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length != 2) throw new IllegalArgumentException("Invalid query parameter: " + param);
                request.setQueryParam(keyValue[0], keyValue[1]);
            }
        } else {
            request.setPath(path);
        }

        if (!tokens[2].equals("HTTP/1.1")) throw new IllegalArgumentException("We only accept HTTP/1.1");
    }

    private static void parseHeader(String line, Request request) {
        String[] tokens = line.split(":", 2);
        request.setHeader(tokens[0], tokens[1].trim());
    }
}
