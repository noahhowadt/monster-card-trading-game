package at.technikum.server.util;

import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParserTest {

    private final static String HTTP_GET = """
            GET /home HTTP/1.1
            Host: localhost:10001
            Authentication: Bearer example-token
            \n""".replace("\n","\r\n");

    private final static String HTTP_POST = """
            POST /users HTTP/1.1
            Host: localhost:8080
            Content-Type: application/json
            Content-Length: 31
            
            {
                "username": "example"
            }""".replace("\n","\r\n");

    private final HttpRequestParser requestParser = new HttpRequestParser();

    @Test
    public void given_httpGetRequest_when_useParser_then_parseMethod() {
        Request request = requestParser.parse(HTTP_GET);

        assertEquals(Method.GET, request.getMethod());
    }

    @Test
    public void given_httpGetRequest_when_useParser_then_parsePath() {
        Request request = requestParser.parse(HTTP_GET);

        assertEquals("/home", request.getPath());
    }

    @Test
    public void given_httpGetRequest_when_useParser_then_parseHostHeader() {
        Request request = requestParser.parse(HTTP_GET);

        assertEquals("localhost:10001", request.getHeader("Host"));
    }

    @Test
    public void given_httpGetRequest_when_useParser_then_parseAuthToken() {
        Request request = requestParser.parse(HTTP_GET);

        assertEquals("Bearer example-token", request.getHeader("Authentication"));
    }

    @Test
    public void given_httpPostRequest_when_useParser_then_parseMethod() {
        Request request = requestParser.parse(HTTP_POST);

        assertEquals(Method.POST, request.getMethod());
    }

    @Test
    public void given_httpPostRequest_when_useParser_then_parseContentLength() {
        Request request = requestParser.parse(HTTP_POST);

        assertEquals("31", request.getHeader("Content-Length"));
    }

    @Test
    public void given_httpPostRequest_when_useParser_then_parseContentType() {
        Request request = requestParser.parse(HTTP_POST);

        assertEquals("application/json", request.getHeader("Content-Type"));
    }

    @Test
    public void given_httpPostRequest_when_useParser_then_parseBody() {
        Request request = requestParser.parse(HTTP_POST);

        String body = """
                {
                    "username": "example"
                }""".replace("\n", "\r\n");
        assertEquals(body, request.getBody());
    }
}