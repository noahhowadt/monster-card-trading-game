package at.technikum;

import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.util.HttpRequestParser;

public class Main {
    public static void main(String[] args) {
        Response res;
        try {
            Request req = HttpRequestParser.parse("""
                    POST /test HTTP/1.1
                    Host: example.com
                    Content-Type: application/json
                    Authorization: Bearer your_token_here
                    Content-Length: 85
                    
                    {
                        "name": "Sample Resource",
                        "description": "This is a sample resource",
                        "status": "active"
                    }""");
            System.out.println(req);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            res = new Response(400);
        }
    }
}