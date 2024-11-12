package at.technikum.server.http;

public enum Status {
    OK(200, "OK"),
    NOT_FOUND(404, "NOT FOUND"),
    BAD_REQUEST(400, "BAD REQUEST"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}