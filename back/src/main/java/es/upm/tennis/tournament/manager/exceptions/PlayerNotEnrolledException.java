package es.upm.tennis.tournament.manager.exceptions;


public class PlayerNotEnrolledException extends RuntimeException {
    public PlayerNotEnrolledException(String message) {
        super(message);
    }
}
