package at.technikum.server.http;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Method method;
    private String path;
    private Map<String, String> headers;
    private Map<String, String> query;
    private String body;

    public Request() {
        this.headers = new HashMap<String, String>();
        this.query = new HashMap<String, String>();
    }

    @Override
    public String toString() {
        String result = method + " request to " + path;
        for (Map.Entry<String, String> header : headers.entrySet()) {
            result += "\n" + header.getKey() + ": " + header.getValue();
        }
        result += "\n\n" + body;
        return result;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getHeader(String key) {
        return this.headers.get(key);
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

    public String getQueryParam(String key) {
        return this.query.get(key);
    }

    public void setQueryParam(String key, String value) {
        this.query.put(key, value);
    }
}
