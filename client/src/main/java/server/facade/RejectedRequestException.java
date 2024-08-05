package server.facade;

public class RejectedRequestException extends Exception {
    public RejectedRequestException(String message) { super(message); }
}
