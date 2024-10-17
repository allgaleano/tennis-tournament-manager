package es.upm.tennis.tournament.manager.exceptions;

public class UnauthorizedUserAction extends RuntimeException {
    public UnauthorizedUserAction(String message) {
        super(message);
    }
}
