package at.technikum.server.http;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private Status status;
    private Map<String, String> headers;
    private String body;

    public Response() {
        this.headers = new HashMap<String, String>();
        this.body = "";
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
