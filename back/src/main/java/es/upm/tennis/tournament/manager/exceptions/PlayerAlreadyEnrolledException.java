package es.upm.tennis.tournament.manager.exceptions;

public class PlayerAlreadyEnrolledException extends RuntimeException {
    public PlayerAlreadyEnrolledException(String message) {
        super(message);
    }
}
