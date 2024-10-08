package es.upm.tennis.tournament.manager.exceptions;

public class InvalidCodeException extends RuntimeException {
    public InvalidCodeException(String message) {
        super(message);
    }
}
