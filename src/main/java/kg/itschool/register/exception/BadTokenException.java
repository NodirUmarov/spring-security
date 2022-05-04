package kg.itschool.register.exception;

public class BadTokenException extends RuntimeException{
    public BadTokenException(String message) {
        super(message);
    }
}
