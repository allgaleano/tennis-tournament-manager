package es.upm.tennis.tournament.manager.exceptions;

public class EmailNotSentException extends RuntimeException {
    public EmailNotSentException(String message) {
        super(message);
    }
}
