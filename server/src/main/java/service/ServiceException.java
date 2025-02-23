package service;

public class ServiceException extends Exception{
    private final int statusCode;

    public ServiceException(int statusCode, String message) {
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
