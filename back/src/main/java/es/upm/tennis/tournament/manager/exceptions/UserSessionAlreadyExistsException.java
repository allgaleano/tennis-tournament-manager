package es.upm.tennis.tournament.manager.exceptions;

public class UserSessionAlreadyExistsException extends RuntimeException {
    public UserSessionAlreadyExistsException(String message) {
        super(message);
    }
}
