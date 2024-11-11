package es.upm.tennis.tournament.manager.exceptions;

public class PlayerAlreadyAcceptedException extends RuntimeException {
    public PlayerAlreadyAcceptedException(String message) {
        super(message);
    }
}
