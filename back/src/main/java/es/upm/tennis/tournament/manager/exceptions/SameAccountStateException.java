package es.upm.tennis.tournament.manager.exceptions;

public class SameAccountStateException extends RuntimeException {
    public SameAccountStateException(String message) {
        super(message);
    }
}
