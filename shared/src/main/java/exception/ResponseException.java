package exception;

public class ResponseException extends Exception {
    private final int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String toJson() {
        return "{ \"message\": \"Error: %s\" }".formatted(getMessage());
    }
}
