package es.upm.tennis.tournament.manager.exceptions;

public class SameRoleException extends RuntimeException {
    public SameRoleException(String message) {
        super(message);
    }
}
