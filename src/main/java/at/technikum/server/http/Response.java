package at.technikum.server.http;

public class Response {
    private final int status;

    public Response(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
