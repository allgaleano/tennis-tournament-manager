package es.upm.tennis.tournament.manager.exceptions;

public class BadEnrollmentStatusException extends RuntimeException {
    public BadEnrollmentStatusException(String message) {
        super(message);
    }
}
